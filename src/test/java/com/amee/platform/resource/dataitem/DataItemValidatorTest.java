package com.amee.platform.resource.dataitem;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.DataItemService;
import com.amee.domain.Metadata;
import com.amee.domain.MetadataService;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitem.v_3_6.DataItemValidator_3_6_0;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;

@RunWith(MockitoJUnitRunner.class)
public class DataItemValidatorTest {

    @Mock
    private MetadataService mockMetadataService;
    @Mock
    private DataItemService mockDataItemService;

    @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        ThreadBeanHolder.set(MetadataService.class, mockMetadataService);
        ThreadBeanHolder.set(DataItemService.class, mockDataItemService);
    }

    @Test
    public void testValid() {
        DataItemValidator_3_6_0 validator = new DataItemValidator_3_6_0();
        validator.setDataItemService(mockDataItemService);
        DataItem good = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(good);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(good, "*"))
            .thenReturn(new Metadata());
        when(mockDataItemService.isUnique(any(DataItem.class)))
            .thenReturn(true);
        when(mockDataItemService.isDataItemUniqueByPath(any(DataItem.class)))
            .thenReturn(true);

        BindException errorsGood = new BindException(good, "good");

        good.setName(RandomStringUtils.random(10));
        good.setPath(RandomStringUtils.randomAlphanumeric(10));
        good.setWikiDoc(RandomStringUtils.random(10));
        good.setProvenance(RandomStringUtils.random(10));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testNameGreaterThanMax() {
        DataItemValidator_3_6_0 validator = new DataItemValidator_3_6_0();
        validator.setDataItemService(mockDataItemService);
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
            .thenReturn(new Metadata());
        when(mockDataItemService.isDataItemUniqueByPath(any(DataItem.class)))
            .thenReturn(true);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(RandomStringUtils.random(DataItem.NAME_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathGreaterThanMax() {
        DataItemValidator_3_6_0 validator = new DataItemValidator_3_6_0();
        validator.setDataItemService(mockDataItemService);
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
            .thenReturn(new Metadata());
        when(mockDataItemService.isDataItemUniqueByPath(any(DataItem.class)))
            .thenReturn(true);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setPath(RandomStringUtils.random(DataItem.PATH_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathBadChars() {
        DataItemValidator_3_6_0 validator = new DataItemValidator_3_6_0();
        validator.setDataItemService(mockDataItemService);
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
            .thenReturn(new Metadata());
        when(mockDataItemService.isDataItemUniqueByPath(any(DataItem.class)))
            .thenReturn(true);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setPath("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiDocGreaterThanMax() {
        DataItemValidator_3_6_0 validator = new DataItemValidator_3_6_0();
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setDataItemService(mockDataItemService);
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
            .thenReturn(new Metadata());
        when(mockDataItemService.isDataItemUniqueByPath(any(DataItem.class)))
            .thenReturn(true);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setWikiDoc(RandomStringUtils.random(DataItem.WIKI_DOC_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testProvenanceGreaterThanMax() {
        DataItemValidator_3_6_0 validator = new DataItemValidator_3_6_0();
        validator.setDataItemService(mockDataItemService);
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
            .thenReturn(new Metadata());
        when(mockDataItemService.isDataItemUniqueByPath(any(DataItem.class)))
            .thenReturn(true);

        BindException errorsBad = new BindException(bad, "bad");

        bad.setProvenance(RandomStringUtils.random(DataItem.PROVENANCE_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testDuplicate() {
        DataItemValidator_3_6_0 validator = new DataItemValidator_3_6_0();
        validator.setDataItemService(mockDataItemService);
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
            .thenReturn(new Metadata());
        when(mockDataItemService.isDataItemUniqueByPath(any(DataItem.class)))
            .thenReturn(true);
        when(mockDataItemService.isUnique(any(DataItem.class)))
            .thenReturn(false);

        BindException errorsBad = new BindException(bad, "bad");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
        assertTrue("Object should have global errors", errorsBad.hasGlobalErrors());
    }

}
