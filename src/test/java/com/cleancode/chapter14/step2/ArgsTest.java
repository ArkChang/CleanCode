package com.cleancode.chapter14.step2;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

public class ArgsTest {

	@Test
	public void testSimpleBooleanPresent() throws ParseException {
		Args args = new Args("x", new String[] { "-x" });
		assertEquals(1, args.cardinality());
		assertEquals(true, args.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_KeyisNull() throws Exception {
		Args args = new Args("", new String[] { "-y" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -y unexpected.", args.errorMessage());
		assertEquals("", args.usage());
		assertFalse(args.getBoolean('y'));
		assertFalse(args.has('x'));
		assertFalse(args.isValid());

	}

	@Test
	public void testBoolean_by_parameterNotIncludedKey() throws Exception {
		Args args = new Args("x", new String[] { "-p" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -p unexpected.", args.errorMessage());
		assertFalse(args.isValid());
		assertEquals("-[x]", args.usage());
		assertFalse(args.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_parameterIncludedKey() throws Exception {
		Args args = new Args("x", new String[] { "-x" });

		assertEquals(1, args.cardinality());
		// 沒錯誤時讀取會有Exception
//		assertTrue(null != args.errorMessage());
//		assertTrue(StringUtils.isBlank(args.errorMessage()));
		assertTrue(args.isValid());
		assertEquals("-[x]", args.usage());
		assertTrue(args.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_keyShowTwiceInParameter() throws Exception {
		Args args = new Args("x", new String[] { "-x", "-x" });

		// 就算出現兩次，也會因為Map Key值重複所以只會顯示一次
//		assertEquals(2, args.cardinality());
		assertEquals(1, args.cardinality());
		// 沒錯誤時讀取會有Exception
//		assertTrue(null != args.errorMessage());
//		assertTrue(StringUtils.isBlank(args.errorMessage()));
		assertTrue(args.isValid());
		assertEquals("-[x]", args.usage());
		assertTrue(args.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_OneRightOneFaultInParameter() throws Exception {
		Args args = new Args("x", new String[] { "-x", "-y" });

		assertEquals(1, args.cardinality());
		// 沒錯誤時讀取會有Exception
//		assertTrue(null != args.errorMessage());
//		assertTrue(StringUtils.isBlank(args.errorMessage()));
		assertFalse(args.isValid());
		assertEquals("-[x]", args.usage());
		assertTrue(args.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_parameterDoesntHaveDash() throws Exception {
		Args args = new Args("x", new String[] { "x" });

		assertEquals(0, args.cardinality());
		// 沒錯誤時讀取會有Exception
//		assertTrue(null != args.errorMessage());
//		assertTrue(StringUtils.isBlank(args.errorMessage()));
		assertTrue(args.isValid());
		assertEquals("-[x]", args.usage());
		assertFalse(args.getBoolean('x'));
	}

	@Test
	public void testBoolean_by_keyHaveDash() throws Exception {

		assertThrows(ParseException.class, () -> {
			Args args = new Args("-x", new String[] { "-x" });
		});

		// 新增判斷只要參數非字母，就會出現ParseException
// 		Args args = new Args("-x", new String[] { "-x" });

//		assertEquals(0, args.cardinality());
//		assertEquals("Argument(s) -x unexpected.", args.errorMessage());
//		assertFalse(args.isValid());
//		assertEquals("-[-x]", args.usage());
//		assertThrows(NullPointerException.class, () -> {
//			args.getBoolean('x');
//		});
	}

	@Test
	public void testString_by_KeyisNull() throws Exception {

		try {
			Args args = new Args("*", new String[] { "-y" });
		} catch (ParseException e) {
			assertEquals("Bad character:*in Args format:*", e.getMessage());
		}

	}

	@Test
	public void testString_by_parameterIncludedKey() throws ParseException {
		Args args = new Args("x*", new String[] { "-x" });

		assertEquals(1, args.cardinality());
		assertEquals("-[x*]", args.usage());
		assertEquals("-x", args.getString('x'));
		assertTrue(args.has('x'));
		assertTrue(args.isValid());
	}

	@Test
	public void testString_by_parameterNotIncludedKey() throws Exception {
		Args args = new Args("x*", new String[] { "-p" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -p unexpected.", args.errorMessage());
		assertEquals("-[x*]", args.usage());
		assertEquals("", args.getString('x'));
		assertFalse(args.has('x'));
		assertFalse(args.isValid());

	}

	@Test
	public void testString_by_keyShowTwiceInParameter() throws Exception {
		Args args = new Args("x*", new String[] { "-x", "-x" });

		assertEquals(1, args.cardinality());
		assertEquals("-[x*]", args.usage());
		assertEquals("-x", args.getString('x'));
		assertTrue(args.has('x'));
		assertTrue(args.isValid());
	}

	@Test
	public void testString_by_OneRightOneFaultInParameter() throws Exception {
		Args args = new Args("x*", new String[] { "-x", "-y" });

		assertEquals(1, args.cardinality());
		try {
			args.errorMessage();
		} catch (Exception e) {
			// 錯誤流程，應該要是Argument(s) -y unexpected.
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x*]", args.usage());
		assertEquals("-x", args.getString('x'));
		assertTrue(args.has('x'));
		assertTrue(args.isValid());
	}
}
