package com.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class CharacterTest {

	@Test
	void test_Character_isLetter() {
		assertTrue(Character.isLetter('x'));
		assertTrue(Character.isLetter('X'));
		assertTrue(Character.isLetter('測'));

		assertFalse(Character.isLetter(0));
		assertFalse(Character.isLetter('!'));
		assertFalse(Character.isLetter(' '));
		
		Character.charCount('x');
	}
	
	// TODO Character All Mathod Test
	
}
