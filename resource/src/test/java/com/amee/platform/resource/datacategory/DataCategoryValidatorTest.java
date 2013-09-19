package com.amee.platform.resource.datacategory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.MetadataService;
import com.amee.domain.data.DataCategory;
import com.amee.platform.resource.datacategory.v_3_3.DataCategoryValidator_3_3_0;
import com.amee.service.data.DataService;

public class DataCategoryValidatorTest {

    private DataService mockDataService;
    private MetadataService mockMetadataService;

    @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        mockMetadataService = mock(MetadataService.class);
        ThreadBeanHolder.set(MetadataService.class, mockMetadataService);
        mockDataService = mock(DataService.class);
        ThreadBeanHolder.set(DataService.class, mockDataService);
    }

    @Test
    public void testValid() {

        DataCategory root = new DataCategory();
        root.setPath("");
        root.setWikiName("Root");
        root.setName("Root");

        DataCategory good = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(good);
        validator.initialise();

        when(mockDataService.isDataCategoryUniqueByPath(good))
                .thenReturn(true);
        when(mockDataService.isDataCategoryUniqueByWikiName(good))
                .thenReturn(true);

        BindException errorsGood = new BindException(good, "good");

        good.setDataCategory(root);
        good.setName("name");
        good.setPath(RandomStringUtils.randomAlphanumeric(10));
        good.setWikiName(RandomStringUtils.randomAlphanumeric(10));
        good.setWikiDoc(RandomStringUtils.random(10));
        good.setProvenance(RandomStringUtils.random(10));
        good.setAuthority(RandomStringUtils.random(10));
        good.setHistory(RandomStringUtils.random(10));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testNameGreaterThanMax() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        String nameGreaterThanMax = RandomStringUtils.random(DataCategory.NAME_MAX_SIZE + 1);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(nameGreaterThanMax);

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testNameLessThanMin() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        String nameLessThanMin = RandomStringUtils.random(DataCategory.NAME_MIN_SIZE - 1);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(nameLessThanMin);

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathGreaterThanMax() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setPath(RandomStringUtils.random(DataCategory.PATH_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathBadChars() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setPath("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiNameGreaterThanMax() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiName(RandomStringUtils.random(DataCategory.WIKI_NAME_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiNameLessThanMin() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiName(RandomStringUtils.random(DataCategory.WIKI_NAME_MIN_SIZE - 0));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiNameBadChars() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiName("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiDocGreaterThanMax() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setWikiDoc(RandomStringUtils.random(DataCategory.WIKI_NAME_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testProvenanceGreaterThanMax() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setProvenance(RandomStringUtils.random(DataCategory.PROVENANCE_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testAuthorityGreaterThanMax() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setAuthority(RandomStringUtils.random(DataCategory.AUTHORITY_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testHistoryGreaterThanMax() {

        DataCategory bad = new DataCategory();

        DataCategoryResource.DataCategoryValidator validator = new DataCategoryValidator_3_3_0();
        validator.setDataService(mockDataService);
        validator.setObject(bad);
        validator.initialise();

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName("name");
        bad.setHistory(RandomStringUtils.random(DataCategory.HISTORY_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
}
