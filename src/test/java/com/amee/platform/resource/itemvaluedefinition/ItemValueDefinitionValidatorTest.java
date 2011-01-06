package com.amee.platform.resource.itemvaluedefinition;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.ILocaleService;
import com.amee.domain.IMetadataService;
import com.amee.domain.data.ItemValueDefinition;
import com.amee.service.locale.LocaleService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
        ItemValueDefinitionValidator validator = new ItemValueDefinitionValidator();
        ItemValueDefinition good = new ItemValueDefinition();

        when(mockLocaleService.getLocaleNameValue(good, "name"))
                .thenReturn("name");

        BindException errorsGood = new BindException(good, "good");

        good.setName("name");
        good.setPath(RandomStringUtils.randomAlphanumeric(10));
        good.setWikiDoc(RandomStringUtils.random(10));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testNameGreaterThanMax() {
        ItemValueDefinitionValidator validator = new ItemValueDefinitionValidator();
        ItemValueDefinition bad = new ItemValueDefinition();

        String nameGreaterThanMax = RandomStringUtils.random(ItemValueDefinition.NAME_MAX_SIZE + 1);

        when(mockLocaleService.getLocaleNameValue(bad, nameGreaterThanMax))
                .thenReturn(nameGreaterThanMax);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(nameGreaterThanMax);

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testNameLessThanMin() {
        ItemValueDefinitionValidator validator = new ItemValueDefinitionValidator();
        ItemValueDefinition bad = new ItemValueDefinition();

        String nameLessThanMin = RandomStringUtils.random(ItemValueDefinition.NAME_MIN_SIZE - 1);

        when(mockLocaleService.getLocaleNameValue(bad, nameLessThanMin))
                .thenReturn(nameLessThanMin);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(nameLessThanMin);

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathGreaterThanMax() {
        ItemValueDefinitionValidator validator = new ItemValueDefinitionValidator();
        ItemValueDefinition bad = new ItemValueDefinition();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setPath(RandomStringUtils.random(ItemValueDefinition.PATH_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathLessThanMin() {
        ItemValueDefinitionValidator validator = new ItemValueDefinitionValidator();
        ItemValueDefinition bad = new ItemValueDefinition();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setPath(RandomStringUtils.random(ItemValueDefinition.PATH_MIN_SIZE - 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathBadChars() {
        ItemValueDefinitionValidator validator = new ItemValueDefinitionValidator();
        ItemValueDefinition bad = new ItemValueDefinition();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setPath("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiDocGreaterThanMax() {
        ItemValueDefinitionValidator validator = new ItemValueDefinitionValidator();
        ItemValueDefinition bad = new ItemValueDefinition();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiDoc(RandomStringUtils.random(ItemValueDefinition.WIKI_DOC_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
}
