package com.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;

class CollectionUtilsTest {

	// TODO 需要補充
	@Test
	void test_isEmpty_whenNewList() {
		List<String> newList = new ArrayList<>();
		List<String> nullList = null;
		List<String> haveDataList = new ArrayList<>();
		haveDataList.add("A");
		haveDataList.add("B");
		
		assertTrue(CollectionUtils.isEmpty(newList));
		assertTrue(CollectionUtils.isEmpty(nullList));
		assertFalse(CollectionUtils.isEmpty(haveDataList));
	}
	
}
