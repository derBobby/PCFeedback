package eu.planlos.pcfeedback.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class GenderUnitTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public final void exampleTest2() {
		int idOne = Gender.MALE.ordinal();
		int idTwo = Gender.FEMALE.ordinal();
		assertEquals(idOne, 0);
		assertEquals(idTwo, 1);
	}

}
