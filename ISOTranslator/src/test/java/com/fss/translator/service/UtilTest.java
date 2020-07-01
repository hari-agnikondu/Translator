/**
 * 
 */
package com.fss.translator.service;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fss.translator.util.Util;

/**
 * @author ravinaganaboyina
 *
 */
public class UtilTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_isEmpty_With_NullValue() {

		assertEquals(true, Util.isEmpty(null));
	}

	@Test
	public void test_isEmpty_With_EmptyValue() {

		assertEquals(true, Util.isEmpty(" "));
	}
	
	@Test
	public void test_isEmpty_With_ValidValue() {

		assertEquals(false, Util.isEmpty("test"));
	}
	
	
	
}
