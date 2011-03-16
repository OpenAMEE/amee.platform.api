package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.ILocaleService;
import com.amee.domain.IMetadataService;
import com.amee.domain.ValueDefinition;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.platform.resource.PerUnitEditor;
import com.amee.platform.resource.UnitEditor;
import com.amee.platform.resource.itemvaluedefinition.v_3_0.ItemValueDefinitionValidator_3_0_0;
import com.amee.platform.science.AmountPerUnit;
import com.amee.platform.science.AmountUnit;
import com.amee.service.locale.LocaleService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.validation.BindException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemValueDefinitionValidatorTest {

    private IMetadataService mockMetadataService;
    private LocaleService mockLocaleService;

    @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        mockMetadataService = mock(IMetadataService.class);
        ThreadBeanHolder.set(IMetadataService.class, mockMetadataService);
        mockLocaleService = mock(LocaleService.class);
        ThreadBeanHolder.set(ILocaleService.class, mockLocaleService);
    }

    @Test
    public void testValid() {

        ItemValueDefinitionValidator_3_0_0 validator = new ItemValueDefinitionValidator_3_0_0();
        ItemValueDefinition good = getItemValueDefinition("test");
        validator.setObject(good);
        validator.initialise();

        when(mockLocaleService.getLocaleNameValue(good, "test"))
                .thenReturn("test");

        BindException errorsGood = new BindException(good, "good");

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testNameLessThanMin() throws Exception {
        lessThanMin("name", ItemValueDefinition.NAME_MIN_SIZE);
    }

    @Test
    public void testNameGreaterThanMax() throws Exception {
        greaterThanMax("name", ItemValueDefinition.NAME_MAX_SIZE + 1);
    }

    @Test
    public void testPathLessThanMin() throws Exception {
        lessThanMin("path", ItemValueDefinition.PATH_MIN_SIZE);
    }

    @Test
    public void testPathGreaterThanMax() throws Exception {
        greaterThanMax("path", ItemValueDefinition.PATH_MAX_SIZE);
    }

    @Test
    public void testPathBadChars() {
        ItemValueDefinitionValidator_3_0_0 validator = new ItemValueDefinitionValidator_3_0_0();
        ItemValueDefinition bad = getItemValueDefinition("test");
        validator.setObject(bad);
        validator.initialise();

        when(mockLocaleService.getLocaleNameValue(bad, "test"))
                .thenReturn("test");

        BindException errorsBad = new BindException(bad, "bad");
        bad.setPath("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
        assertEquals("Got unexpected field error", "path", errorsBad.getFieldError().getField());
    }

    @Test
    public void testWikiDocGreaterThanMax() throws Exception {
        greaterThanMax("wikiDoc", ItemValueDefinition.WIKI_DOC_MAX_SIZE);
    }

    @Test
    public void testBadUnit() throws Exception {
        ItemValueDefinitionValidator_3_0_0 validator = new ItemValueDefinitionValidator_3_0_0();
        ItemValueDefinition bad = getItemValueDefinition("test");
        validator.setObject(bad);
        validator.initialise();

        when(mockLocaleService.getLocaleNameValue(bad, "test"))
                .thenReturn("test");

        BindException errorsBad = new BindException(bad, "bad");
        bad.setUnit("Not a real unit");

        try {
            validator.validate(bad, errorsBad);
            fail("Should have thrown exception");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testBadPerUnit() throws Exception {
        ItemValueDefinitionValidator_3_0_0 validator = new ItemValueDefinitionValidator_3_0_0();
        ItemValueDefinition bad = getItemValueDefinition("test");
        validator.setObject(bad);
        validator.initialise();

        when(mockLocaleService.getLocaleNameValue(bad, "test"))
                .thenReturn("test");

        BindException errorsBad = new BindException(bad, "good");
        bad.setUnit("Not a real perUnit");
        try {
            validator.validate(bad, errorsBad);
            fail("Should have thrown exception");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    public void testChoicesGreaterThanMax() throws Exception {
        greaterThanMax("choices", ItemValueDefinition.CHOICES_MAX_SIZE);
    }

    @Test
    public void testValueGreaterThanMax() throws Exception {
        greaterThanMax("value", ItemValueDefinition.VALUE_MAX_SIZE);
    }

    private void lessThanMin(String field, int minLength) throws Exception {

        // The dodgy string to set
        String lessThanMin = RandomStringUtils.random(minLength - 1);

        // Set up the mock LocaleService
        String name;
        if (field.equals("name")) {
            name = lessThanMin;
        } else {
            name = "test";
        }
        ItemValueDefinition bad = getItemValueDefinition(name);
        when(mockLocaleService.getLocaleNameValue(bad, name))
                .thenReturn(name);

        BindException errorsBad = new BindException(bad, "bad");

        // Set the field under test
        BeanWrapper bean = new BeanWrapperImpl(bad);
        bean.registerCustomEditor(AmountUnit.class, "unit", new UnitEditor());
        bean.registerCustomEditor(AmountPerUnit.class, "perUnit", new PerUnitEditor());
        bean.setPropertyValue(field, lessThanMin);

        // Validate the object
        ItemValueDefinitionValidator_3_0_0 validator = new ItemValueDefinitionValidator_3_0_0();
        validator.setObject(bad);
        validator.initialise();
        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
        assertEquals("Got unexpected field error", field, errorsBad.getFieldError().getField());
    }

    private void greaterThanMax(String field, int maxLength) throws Exception {

        // The dodgy string to set
        String greaterThanMax = RandomStringUtils.random(maxLength + 1);

        // Set up the mock LocaleService
        String name;
        if (field.equals("name")) {
            name = greaterThanMax;
        } else {
            name = "test";
        }
        ItemValueDefinition bad = getItemValueDefinition(name);
        when(mockLocaleService.getLocaleNameValue(bad, name))
                .thenReturn(name);

        BindException errorsBad = new BindException(bad, "bad");

        // Set the field under test
        BeanWrapper bean = new BeanWrapperImpl(bad);
        bean.registerCustomEditor(AmountUnit.class, "unit", new UnitEditor());
        bean.registerCustomEditor(AmountPerUnit.class, "perUnit", new PerUnitEditor());
        bean.setPropertyValue(field, greaterThanMax);

        // Validate the object
        ItemValueDefinitionValidator_3_0_0 validator = new ItemValueDefinitionValidator_3_0_0();
        validator.setObject(bad);
        validator.initialise();
        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
        assertEquals("Got unexpected field error", field, errorsBad.getFieldError().getField());
    }

    private ItemValueDefinition getItemValueDefinition(String name) {
        ItemValueDefinition ivd = new ItemValueDefinition();
        ivd.setName(name);
        ivd.setPath(RandomStringUtils.randomAlphanumeric(10));
        ivd.setValueDefinition(new ValueDefinition());
        return ivd;
    }
}
