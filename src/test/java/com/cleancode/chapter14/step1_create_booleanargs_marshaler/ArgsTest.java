package com.cleancode.chapter14.step1_create_booleanargs_marshaler;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

class ArgsTest {

	@Test
	void testSimpleBooleanPresent() {
		Args args = new Args("x", new String[] { "-x" });
		assertEquals(1, args.cardinality());
		assertEquals(true, args.getBoolean('x'));
	}

	@Test
	void testBoolean_by_parameterNotIncludedKey() {
		Args args = new Args("x", new String[] { "-p" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -p unexpected.", args.errorMessage());
		assertNotNull(args.errorMessage());
		assertFalse(args.isValid());
		assertEquals("-[x]", args.usage());
		assertFalse(args.getBoolean('x'));
	}

	@Test
	void testBoolean_by_parameterIncludedKey() {
		Args args = new Args("x", new String[] { "-x" });

		assertEquals(1, args.cardinality());
		assertNotNull(args.errorMessage());
		assertTrue(StringUtils.isBlank(args.errorMessage()));
		assertTrue(args.isValid());
		assertEquals("-[x]", args.usage());
		assertTrue(args.getBoolean('x'));
	}

	@Test
	void testBoolean_by_keyShowTwiceInParameter() {
		Args args = new Args("x", new String[] { "-x", "-x" });

		assertEquals(2, args.cardinality());
		assertNotNull(args.errorMessage());
		assertTrue(StringUtils.isBlank(args.errorMessage()));
		assertTrue(args.isValid());
		assertEquals("-[x]", args.usage());
		assertTrue(args.getBoolean('x'));
	}

	@Test
	void testBoolean_by_OneRightOneFaultInParameter() throws Exception {
		Args args = new Args("x", new String[] { "-x", "-y" });

		assertEquals(1, args.cardinality());
		assertNotNull(args.errorMessage());
		assertEquals("Argument(s) -y unexpected.", args.errorMessage());
		assertFalse(args.isValid());
		assertEquals("-[x]", args.usage());
		assertTrue(args.getBoolean('x'));
	}
	
	@Test
	void testBoolean_by_parameterDoesntHaveDash() {
		Args args = new Args("x", new String[] { "x" });

		assertEquals(0, args.cardinality());
		assertNotNull(args.errorMessage());
		assertTrue(StringUtils.isBlank(args.errorMessage()));
		assertTrue(args.isValid());
		assertEquals("-[x]", args.usage());
		assertFalse(args.getBoolean('x'));
	}

	@Test
	void testBoolean_by_keyHaveDash() {
		Args args = new Args("-x", new String[] { "-x" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -x unexpected.", args.errorMessage());
		assertFalse(args.isValid());
		assertEquals("-[-x]", args.usage());
		assertThrows(NullPointerException.class, () -> {
			args.getBoolean('x');
		});
	}

}
