package com.cleancode.chapter14.step5_refactoring_argumentMarshaler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

class ArgsTest {

	@Test
	void testBoolean_whenOneKey() throws Exception {
		Args args = new Args("x", new String[] { "-x", "true" });
		assertEquals(1, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x]", args.usage());
		assertEquals(true, args.getBoolean('x'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.isValid());

	}

	@Test
	void testBoolean_whenTwoKey() throws Exception {
		Args args = new Args("x,y", new String[] { "-xy", "true", "false" });
		assertEquals(2, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x,y]", args.usage());
		assertEquals(true, args.getBoolean('x'));
		assertEquals(false, args.getBoolean('y'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.has('y'));
		assertEquals(true, args.isValid());

	}

	@Test
	void testBoolean_whenOneKeyAndTwoParameter() throws Exception {
		Args args = new Args("x", new String[] { "-x", "false", "true" });

		assertEquals(1, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x]", args.usage());
		assertEquals(false, args.getBoolean('x'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.isValid());
	}

	@Test
	void testBoolean_whenKeyisNull() throws Exception {
		Args args = new Args("", new String[] { "-x", "true" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -x unexpected.", args.errorMessage());
		assertEquals("", args.usage());
		assertEquals(false, args.getBoolean('x'));
		assertEquals(false, args.has('x'));
		assertEquals(false, args.isValid());

	}

	@Test
	void testBoolean_whenParameterNotIncludedKey() throws Exception {
		Args args = new Args("x", new String[] { "-p", "true" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -p unexpected.", args.errorMessage());
		assertEquals("-[x]", args.usage());
		assertEquals(false, args.getBoolean('x'));
		assertEquals(false, args.has('x'));
		assertEquals(false, args.isValid());
	}

	@Test
	void testBoolean_whenInvalidBoolean() throws Exception {
		Args args = new Args("x", new String[] { "-x", "Truthy" });

		assertEquals(1, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x]", args.usage());
		assertEquals(false, args.getBoolean('x'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.isValid());
	}

	@Test
	void testBoolean_whenParameterDoesntHaveDash() throws Exception {
		Args args = new Args("x", new String[] { "x", "true" });

		assertEquals(0, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x]", args.usage());
		assertEquals(false, args.getBoolean('x'));
		assertEquals(false, args.has('x'));
		assertEquals(true, args.isValid());
	}

	@Test
	void testBoolean_whenKeyHaveDash() throws Exception {
		assertThrows(ParseException.class, () -> {
			new Args("-x", new String[] { "-x", "true" });
		});
	}

	@Test
	void testString_whenOneKey() throws Exception {
		Args args = new Args("x*", new String[] { "-x", "xyz" });
		assertEquals(1, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x*]", args.usage());
		assertEquals("xyz", args.getString('x'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.isValid());

	}

	@Test
	void testString_whenTwoKey() throws Exception {
		Args args = new Args("x*,y*", new String[] { "-xy", "xyz", "abc" });
		assertEquals(2, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x*,y*]", args.usage());
		assertEquals("xyz", args.getString('x'));
		assertEquals("abc", args.getString('y'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.has('y'));
		assertEquals(true, args.isValid());

	}

	@Test
	void testString_whenOneKeyAndTwoParameter() throws Exception {
		Args args = new Args("x*", new String[] { "-x", "xyz", "abc" });

		assertEquals(1, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x*]", args.usage());
		assertEquals("xyz", args.getString('x'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.isValid());
	}

	@Test
	void testString_whenKeyisNull() throws Exception {
		Args args = new Args("", new String[] { "-x", "xyz" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -x unexpected.", args.errorMessage());
		assertEquals("", args.usage());
		assertEquals("", args.getString('x'));
		assertEquals(false, args.has('x'));
		assertEquals(false, args.isValid());

	}

	@Test
	void testString_whenKeyisStar() throws Exception {
		assertThrows(ParseException.class, () -> {
			new Args("*", new String[] { "-x", "xyz" });
		});

	}

	@Test
	void testString_whenParameterNotIncludedKey() throws Exception {
		Args args = new Args("x*", new String[] { "-p", "xyz" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -p unexpected.", args.errorMessage());
		assertEquals("-[x*]", args.usage());
		assertEquals("", args.getString('x'));
		assertEquals(false, args.has('x'));
		assertEquals(false, args.isValid());
	}

	@Test
	void testString_whenParameterDoesntHaveDash() throws Exception {
		Args args = new Args("x*", new String[] { "x", "xyz" });

		assertEquals(0, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x*]", args.usage());
		assertEquals("", args.getString('x'));
		assertEquals(false, args.has('x'));
		assertEquals(true, args.isValid());
	}

	@Test
	void testString_whenKeyHaveDash() throws Exception {
		assertThrows(ParseException.class, () -> {
			new Args("-x*", new String[] { "-x", "xyz" });
		});
	}

	@Test
	void testInteger_whenOneKey() throws Exception {
		Args args = new Args("x#", new String[] { "-x", "123" });
		assertEquals(1, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x#]", args.usage());
		assertEquals(123, args.getInt('x'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.isValid());

	}

	@Test
	void testInteger_whenTwoKey() throws Exception {
		Args args = new Args("x#,y#", new String[] { "-xy", "123", "456" });
		assertEquals(2, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x#,y#]", args.usage());
		assertEquals(123, args.getInt('x'));
		assertEquals(456, args.getInt('y'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.has('y'));
		assertEquals(true, args.isValid());

	}

	@Test
	void testInteger_whenOneKeyAndTwoParameter() throws Exception {
		Args args = new Args("x#", new String[] { "-x", "123", "456" });

		assertEquals(1, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x#]", args.usage());
		assertEquals(123, args.getInt('x'));
		assertEquals(true, args.has('x'));
		assertEquals(true, args.isValid());
	}

	@Test
	void testInteger_whenKeyisNull() throws Exception {
		Args args = new Args("", new String[] { "-x", "123" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -x unexpected.", args.errorMessage());
		assertEquals("", args.usage());
		assertEquals(0, args.getInt('x'));
		assertEquals(false, args.has('x'));
		assertEquals(false, args.isValid());

	}

	@Test
	void testInteger_whenParameterNotIncludedKey() throws Exception {
		Args args = new Args("x#", new String[] { "-p", "123" });

		assertEquals(0, args.cardinality());
		assertEquals("Argument(s) -p unexpected.", args.errorMessage());
		assertEquals("-[x#]", args.usage());
		assertEquals(0, args.getInt('x'));
		assertEquals(false, args.has('x'));
		assertEquals(false, args.isValid());
	}

	@Test
	void testInteger_whenInvalidInteger() throws Exception {
		assertThrows(NumberFormatException.class, () -> {
			new Args("x#", new String[] { "-x", "Truthy" });
		});
	}

	@Test
	void testInteger_whenParameterDoesntHaveDash() throws Exception {
		Args args = new Args("x#", new String[] { "x", "123" });

		assertEquals(0, args.cardinality());
		try {
			assertEquals("", args.errorMessage());
		} catch (Exception e) {
			assertEquals("TILT: Should not get here.", e.getMessage());
		}
		assertEquals("-[x#]", args.usage());
		assertEquals(0, args.getInt('x'));
		assertEquals(false, args.has('x'));
		assertEquals(true, args.isValid());
	}

	@Test
	void testInteger_whenKeyHaveDash() throws Exception {
		assertThrows(ParseException.class, () -> {
			new Args("-x#", new String[] { "-x", "123" });
		});
	}
}
