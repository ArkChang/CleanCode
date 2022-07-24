package com.cleancode.chapter14;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import com.cleancode.chapter14.practice.ArgsDismantling;

public class ArgsDismantlingTest {

	@Test
	public void testSimpleBooleanPresent() {
		ArgsDismantling argsDismantling = new ArgsDismantling("x", new String[] { "-x" });
		assertEquals(1, argsDismantling.cardinality());
		assertEquals(true, argsDismantling.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_parameterNotIncludedKey() {
		ArgsDismantling argsDismantling = new ArgsDismantling("x", new String[] { "-p" });

		assertEquals(0, argsDismantling.cardinality());
		assertNotNull(argsDismantling.errorMessage());
		assertFalse(argsDismantling.isValid());
		assertEquals("-[x]", argsDismantling.usage());
		assertFalse(argsDismantling.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_parameterIncludedKey() {
		ArgsDismantling argsDismantling = new ArgsDismantling("x", new String[] { "-x" });

		assertEquals(1, argsDismantling.cardinality());
		assertTrue(null != argsDismantling.errorMessage());
		assertTrue(StringUtils.isBlank(argsDismantling.errorMessage()));
		assertTrue(argsDismantling.isValid());
		assertEquals("-[x]", argsDismantling.usage());
		assertTrue(argsDismantling.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_keyShowTwiceInParameter() {
		ArgsDismantling argsDismantling = new ArgsDismantling("x", new String[] { "-x", "-x" });

		assertEquals(2, argsDismantling.cardinality());
		assertTrue(null != argsDismantling.errorMessage());
		assertTrue(StringUtils.isBlank(argsDismantling.errorMessage()));
		assertTrue(argsDismantling.isValid());
		assertEquals("-[x]", argsDismantling.usage());
		assertTrue(argsDismantling.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_parameterDoesntHaveDash() {
		ArgsDismantling argsDismantling = new ArgsDismantling("x", new String[] { "x" });

		assertEquals(0, argsDismantling.cardinality());
		assertTrue(null != argsDismantling.errorMessage());
		assertTrue(StringUtils.isBlank(argsDismantling.errorMessage()));
		assertTrue(argsDismantling.isValid());
		assertEquals("-[x]", argsDismantling.usage());
		assertFalse(argsDismantling.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_keyHaveDash() {
		ArgsDismantling argsDismantling = new ArgsDismantling("-x", new String[] { "-x" });

		assertEquals(0, argsDismantling.cardinality());
		assertEquals("Argument(s) -x unexpected.", argsDismantling.errorMessage());
		assertFalse(argsDismantling.isValid());
		assertEquals("-[-x]", argsDismantling.usage());
		assertThrows(NullPointerException.class, () -> {
			argsDismantling.getBoolean('x');
		});
	}

}
