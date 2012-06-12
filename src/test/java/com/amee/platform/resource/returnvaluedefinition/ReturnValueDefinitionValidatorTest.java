package com.amee.platform.resource.returnvaluedefinition;

import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.resource.returnvaluedefinition.v_3_1.ReturnValueDefinitionValidator_3_1_0;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReturnValueDefinitionValidatorTest {

	@Test
	public void testValid() {
		ReturnValueDefinitionValidator_3_1_0 validator = new ReturnValueDefinitionValidator_3_1_0();
		ReturnValueDefinition good = new ReturnValueDefinition();
		BindException errorsGood = new BindException(good, "good");

		good.setType("CO2");
		good.setUnit(AmountUnit.valueOf("kg"));
		good.setPerUnit(AmountPerUnit.valueOf("month"));
		good.setDefaultType(true);
		good.setName("kg CO2 per month");

		validator.setObject(good);
		validator.initialise();
		validator.validate(good, errorsGood);
		assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
	}

	@Test
	public void testTypeGreaterThanMax() {
		ReturnValueDefinitionValidator_3_1_0 validator = new ReturnValueDefinitionValidator_3_1_0();
		ReturnValueDefinition bad = new ReturnValueDefinition();
		BindException errorsBad = new BindException(bad, "bad");

		String typeGreaterThanMax = RandomStringUtils.random(ReturnValueDefinition.TYPE_MAX_SIZE + 1);
		bad.setType(typeGreaterThanMax);

		validator.setObject(bad);
		validator.initialise();
		validator.validate(bad, errorsBad);
		assertTrue("Object should fail validation", errorsBad.hasErrors());
		assertTrue("'type' field should be in error", errorsBad.hasFieldErrors("type"));
	}

	@Test
	public void testEmptyType() {
		ReturnValueDefinitionValidator_3_1_0 validator = new ReturnValueDefinitionValidator_3_1_0();
		ReturnValueDefinition bad = new ReturnValueDefinition();
		BindException errorsBad = new BindException(bad, "bad");

		bad.setType("");

		validator.setObject(bad);
		validator.initialise();
		validator.validate(bad, errorsBad);
		assertTrue("Object should fail validation", errorsBad.hasErrors());
		assertTrue("'type' field should be in error", errorsBad.hasFieldErrors("type"));
	}

	@Test
	public void testNameGreaterThanMax() {
		ReturnValueDefinitionValidator_3_1_0 validator = new ReturnValueDefinitionValidator_3_1_0();
		ReturnValueDefinition bad = new ReturnValueDefinition();
		BindException errorsBad = new BindException(bad, "bad");

		bad.setType("CO2");
		String nameGreaterThanMax = RandomStringUtils.random(ReturnValueDefinition.TYPE_MAX_SIZE + 1);
		bad.setName(nameGreaterThanMax);

		validator.setObject(bad);
		validator.initialise();
		validator.validate(bad, errorsBad);
		assertTrue("Object should fail validation", errorsBad.hasErrors());
		assertTrue("'name' field should be in error", errorsBad.hasFieldErrors("name"));
	}

	@Test
	public void testEmptyName() {
		ReturnValueDefinitionValidator_3_1_0 validator = new ReturnValueDefinitionValidator_3_1_0();
		ReturnValueDefinition bad = new ReturnValueDefinition();
		BindException errorsBad = new BindException(bad, "bad");

		bad.setType("CO2");
		bad.setName("");

		validator.setObject(bad);
		validator.initialise();
		validator.validate(bad, errorsBad);
		assertTrue("Object should fail validation", errorsBad.hasErrors());
		assertTrue("'name' field should be in error", errorsBad.hasFieldErrors("name"));
	}

	@Test
	public void testTypeBadChars() {
		ReturnValueDefinitionValidator_3_1_0 validator = new ReturnValueDefinitionValidator_3_1_0();
		ReturnValueDefinition bad = new ReturnValueDefinition();
		BindException errorsBad = new BindException(bad, "bad");

		bad.setType("!!!!");

		validator.setObject(bad);
		validator.initialise();
		validator.validate(bad, errorsBad);
		assertTrue("Object should fail validation", errorsBad.hasErrors());
		assertTrue("'type' field should be in error", errorsBad.hasFieldErrors("type"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidUnit() {
		ReturnValueDefinitionValidator_3_1_0 validator = new ReturnValueDefinitionValidator_3_1_0();
		ReturnValueDefinition bad = new ReturnValueDefinition();
		BindException errorsBad = new BindException(bad, "bad");

		bad.setType("CO2");
		bad.setUnit(AmountUnit.valueOf("NOT_A_UNIT"));
		bad.setPerUnit(AmountPerUnit.valueOf("month"));

		validator.setObject(bad);
		validator.initialise();
		validator.validate(bad, errorsBad);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidPerUnit() {
		ReturnValueDefinitionValidator_3_1_0 validator = new ReturnValueDefinitionValidator_3_1_0();
		ReturnValueDefinition bad = new ReturnValueDefinition();
		BindException errorsBad = new BindException(bad, "bad");

		bad.setType("CO2");
		bad.setUnit(AmountUnit.valueOf("kg"));
		bad.setPerUnit(AmountPerUnit.valueOf("NOT_A_PER_UNIT"));

		validator.setObject(bad);
		validator.initialise();
		validator.validate(bad, errorsBad);
	}
}