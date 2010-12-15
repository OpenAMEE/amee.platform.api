package com.amee.platform.resource.tag;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.tag.Tag;
import com.amee.service.tag.TagService;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class TagValidatorTest {

    private TagService mockTagService;

    @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        mockTagService = mock(TagService.class);
        ThreadBeanHolder.set("tagService", mockTagService);
    }

    @Test
    public void testValid() {

        TagValidator validator = new TagValidator();
        validator.setTagService(mockTagService);

        Tag good = new Tag();
        BindException errorsGood = new BindException(good, "good");

        good.setTag(RandomStringUtils.randomAlphanumeric(12));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testTagMinLength() {

        TagValidator validator = new TagValidator();
        validator.setTagService(mockTagService);

        Tag good = new Tag();
        BindException errorsGood = new BindException(good, "good");

        good.setTag(RandomStringUtils.randomAlphanumeric(Tag.TAG_MIN_SIZE));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testTagMaxLength() {

        TagValidator validator = new TagValidator();
        validator.setTagService(mockTagService);

        Tag good = new Tag();
        BindException errorsGood = new BindException(good, "good");

        good.setTag(RandomStringUtils.randomAlphanumeric(Tag.TAG_MAX_SIZE));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testTagLessThanMinLength() {

        TagValidator validator = new TagValidator();
        validator.setTagService(mockTagService);

        Tag bad = new Tag();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setTag(RandomStringUtils.randomAlphanumeric(Tag.TAG_MIN_SIZE - 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testTagGreaterThanMaxLength() {

        TagValidator validator = new TagValidator();
        validator.setTagService(mockTagService);

        Tag bad = new Tag();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setTag(RandomStringUtils.randomAlphanumeric(Tag.TAG_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testTagBadChars() {

        TagValidator validator = new TagValidator();
        validator.setTagService(mockTagService);

        Tag bad = new Tag();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setTag("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
}
