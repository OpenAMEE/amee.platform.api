package com.amee.platform.resource.datacategory;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.IMetadataService;
import com.amee.domain.data.DataCategory;
import com.amee.service.data.DataService;
import com.amee.service.locale.LocaleService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataCategoryValidatorTest {

    private DataService mockDataService;
    private IMetadataService mockMetadataService;
    private LocaleService mockLocaleService;

    @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        mockMetadataService = mock(IMetadataService.class);
        ThreadBeanHolder.set("metadataService", mockMetadataService);
        mockLocaleService = mock(LocaleService.class);
        ThreadBeanHolder.set("localeService", mockLocaleService);
        mockDataService = mock(DataService.class);
        ThreadBeanHolder.set("dataService", mockDataService);
    }

    @Test
    public void testValid() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory good = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(good, "name"))
                .thenReturn("name");
        when(mockDataService.isDataCategoryUniqueByWikiName(good))
                .thenReturn(true);

        BindException errorsGood = new BindException(good, "good");

        good.setName("name");
        good.setPath(RandomStringUtils.randomAlphanumeric(10));
        good.setWikiName(RandomStringUtils.randomAlphanumeric(10));
        good.setWikiDoc(RandomStringUtils.random(10));
        good.setProvenance(RandomStringUtils.random(10));
        good.setAuthority(RandomStringUtils.random(10));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testNameGreaterThanMax() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        String nameGreaterThanMax = RandomStringUtils.random(DataCategory.NAME_MAX_SIZE + 1);

        when(mockLocaleService.getLocaleNameValue(bad, nameGreaterThanMax))
                .thenReturn(nameGreaterThanMax);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(nameGreaterThanMax);

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testNameLessThanMin() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        String nameLessThanMin = RandomStringUtils.random(DataCategory.NAME_MIN_SIZE - 1);

        when(mockLocaleService.getLocaleNameValue(bad, nameLessThanMin))
                .thenReturn(nameLessThanMin);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(nameLessThanMin);

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathGreaterThanMax() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setPath(RandomStringUtils.random(DataCategory.PATH_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathBadChars() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setPath("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiNameGreaterThanMax() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiName(RandomStringUtils.random(DataCategory.WIKI_NAME_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiNameLessThanMin() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiName(RandomStringUtils.random(DataCategory.WIKI_NAME_MIN_SIZE - 0));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiNameBadChars() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiName("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiDocGreaterThanMax() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiDoc(RandomStringUtils.random(DataCategory.WIKI_NAME_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testProvenanceGreaterThanMax() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setProvenance(RandomStringUtils.random(DataCategory.PROVENANCE_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testAuthorityGreaterThanMax() {

        DataCategoryValidator validator = new DataCategoryValidator();
        validator.setDataService(mockDataService);

        DataCategory bad = new DataCategory();

        when(mockLocaleService.getLocaleNameValue(bad, "name"))
                .thenReturn("name");

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setAuthority(RandomStringUtils.random(DataCategory.AUTHORITY_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
}
