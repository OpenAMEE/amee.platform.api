package com.amee.platform.resource.dataitem;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.IMetadataService;
import com.amee.domain.Metadata;
import com.amee.domain.data.DataItem;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataItemValidatorTest {

    private IMetadataService mockService;

    @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        mockService = mock(IMetadataService.class);
        ThreadBeanHolder.set(IMetadataService.class, mockService);
    }

    @Test
    public void testValid() {
        DataItemValidator validator = new DataItemValidator();
        DataItem good = new DataItem();
        when(mockService.getMetadataForEntity(good, "*"))
                .thenReturn(new Metadata());

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
        DataItemValidator validator = new DataItemValidator();
        DataItem bad = new DataItem();
        when(mockService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setName(RandomStringUtils.random(DataItem.NAME_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathGreaterThanMax() {
        DataItemValidator validator = new DataItemValidator();
        DataItem bad = new DataItem();
        when(mockService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setPath(RandomStringUtils.random(DataItem.PATH_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testPathBadChars() {
        DataItemValidator validator = new DataItemValidator();
        DataItem bad = new DataItem();
        when(mockService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setPath("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testWikiDocGreaterThanMax() {
        DataItemValidator validator = new DataItemValidator();
        DataItem bad = new DataItem();
        when(mockService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setWikiDoc(RandomStringUtils.random(DataItem.WIKI_DOC_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testProvenanceGreaterThanMax() {
        DataItemValidator validator = new DataItemValidator();
        DataItem bad = new DataItem();
        when(mockService.getMetadataForEntity(bad, "*"))
                .thenReturn(new Metadata());

        BindException errorsBad = new BindException(bad, "bad");

        bad.setProvenance(RandomStringUtils.random(DataItem.PROVENANCE_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

}
