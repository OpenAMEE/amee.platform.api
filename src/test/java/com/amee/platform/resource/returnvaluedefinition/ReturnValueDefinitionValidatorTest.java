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

//    @Test
//    public void testUnitGreaterThanMax() {
//        ReturnValueDefinitionValidator validator = new ReturnValueDefinitionValidator();
//        ReturnValueDefinition bad = new ReturnValueDefinition();
//        BindException errorsBad = new BindException(bad, "bad");
//
//        bad.setType("CO2");
//        String unitGreaterThanMax = RandomStringUtils.random(ReturnValueDefinition.UNIT_MAX_SIZE + 1);
//        bad.setUnit(unitGreaterThanMax);
//
//        validator.validate(bad, errorsBad);
//        assertTrue("Object should fail validation", errorsBad.hasErrors());
//        assertTrue("'unit' field should be in error", errorsBad.hasFieldErrors("unit"));
//    }
//
//    @Test
//    public void testUnitBadChars() {
//        ReturnValueDefinitionValidator validator = new ReturnValueDefinitionValidator();
//        ReturnValueDefinition bad = new ReturnValueDefinition();
//        BindException errorsBad = new BindException(bad, "bad");
//
//        bad.setType("CO2");
//        bad.setUnit("!!!!");
//
//        validator.validate(bad, errorsBad);
//        assertTrue("Object should fail validation", errorsBad.hasErrors());
//        assertTrue("'unit' field should be in error", errorsBad.hasFieldErrors("unit"));
//    }
}
