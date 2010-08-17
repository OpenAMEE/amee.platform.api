package com.amee.platform.resource.returnvaluedefinition;

import com.amee.domain.data.ReturnValueDefinition;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReturnValueDefinitionValidatorTest {

    @Test
    public void testValid() {
        ReturnValueDefinitionValidator validator = new ReturnValueDefinitionValidator();
        ReturnValueDefinition good = new ReturnValueDefinition();
        BindException errorsGood = new BindException(good, "good");

        good.setType("CO2");
        good.setUnit(AmountUnit.valueOf("kg"));
        good.setPerUnit(AmountPerUnit.valueOf("month"));
        good.setDefaultType(true);

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testTypeGreaterThanMax() {
        ReturnValueDefinitionValidator validator = new ReturnValueDefinitionValidator();
        ReturnValueDefinition bad = new ReturnValueDefinition();
        BindException errorsBad = new BindException(bad, "bad");

        String typeGreaterThanMax = RandomStringUtils.random(ReturnValueDefinition.TYPE_MAX_SIZE + 1);
        bad.setType(typeGreaterThanMax);

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
        assertTrue("'type' field should be in error", errorsBad.hasFieldErrors("type"));        
    }

    @Test
    public void testEmptyType() {
        ReturnValueDefinitionValidator validator = new ReturnValueDefinitionValidator();
        ReturnValueDefinition bad = new ReturnValueDefinition();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setType("");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
        assertTrue("'type' field should be in error", errorsBad.hasFieldErrors("type"));
    }

    @Test
    public void testTypeBadChars() {
        ReturnValueDefinitionValidator validator = new ReturnValueDefinitionValidator();
        ReturnValueDefinition bad = new ReturnValueDefinition();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setType("!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
        assertTrue("'type' field should be in error", errorsBad.hasFieldErrors("type"));        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUnit() {
        ReturnValueDefinitionValidator validator = new ReturnValueDefinitionValidator();
        ReturnValueDefinition bad = new ReturnValueDefinition();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setType("CO2");
        bad.setUnit(AmountUnit.valueOf("NOT_A_UNIT"));
        bad.setPerUnit(AmountPerUnit.valueOf("month"));

        validator.validate(bad, errorsBad);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidPerUnit() {
        ReturnValueDefinitionValidator validator = new ReturnValueDefinitionValidator();
        ReturnValueDefinition bad = new ReturnValueDefinition();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setType("CO2");
        bad.setUnit(AmountUnit.valueOf("kg"));
        bad.setPerUnit(AmountPerUnit.valueOf("NOT_A_PER_UNIT"));

        validator.validate(bad, errorsBad);
    }
}
