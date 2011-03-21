package com.amee.platform.resource.dataitem;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.Metadata;
import com.amee.domain.MetadataService;
import com.amee.domain.data.DataCategory;
import com.amee.domain.data.ItemDefinition;
import com.amee.domain.item.data.DataItem;
import com.amee.platform.resource.dataitem.v_3_4.DataItemValidator_3_4_0;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataItemValidatorTest {

    private MetadataService mockMetadataService;


    @Test
    public void x() {

    }

    // @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        mockMetadataService = mock(MetadataService.class);
        ThreadBeanHolder.set(MetadataService.class, mockMetadataService);
    }

    // @Test
    public void testValid() {
        DataItemValidator_3_4_0 validator = new DataItemValidator_3_4_0();
        DataItem good = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(good);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(good, "*"))
                .thenReturn(new Metadata());

        BindException errorsGood = new BindException(good, "good");

        good.setName(RandomStringUtils.random(10));
        good.setPath(RandomStringUtils.randomAlphanumeric(10));
        good.setWikiDoc(RandomStringUtils.random(10));
        good.setProvenance(RandomStringUtils.random(10));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    // @Test
    public void testNameGreaterThanMax() {
        DataItemValidator_3_4_0 validator = new DataItemValidator_3_4_0();
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(RandomStringUtils.random(DataItem.NAME_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    // @Test
    public void testPathGreaterThanMax() {
        DataItemValidator_3_4_0 validator = new DataItemValidator_3_4_0();
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setPath(RandomStringUtils.random(DataItem.PATH_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    // @Test
    public void testPathBadChars() {
        DataItemValidator_3_4_0 validator = new DataItemValidator_3_4_0();
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setPath("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    // @Test
    public void testWikiDocGreaterThanMax() {
        DataItemValidator_3_4_0 validator = new DataItemValidator_3_4_0();
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setWikiDoc(RandomStringUtils.random(DataItem.WIKI_DOC_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    // @Test
    public void testProvenanceGreaterThanMax() {
        DataItemValidator_3_4_0 validator = new DataItemValidator_3_4_0();
        DataItem bad = new DataItem(new DataCategory(), new ItemDefinition());
        validator.setObject(bad);
        validator.initialise();
        when(mockMetadataService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setProvenance(RandomStringUtils.random(DataItem.PROVENANCE_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

}
