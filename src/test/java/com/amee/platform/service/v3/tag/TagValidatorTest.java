package com.amee.platform.service.v3.tag;

import com.amee.domain.tag.Tag;
import com.amee.platform.resource.tag.TagValidator;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TagValidatorTest {

    @Test
    public void testValid() {
        TagValidator validator = new TagValidator();
        Tag good = new Tag();
        BindException errorsGood = new BindException(good, "good");

        good.setTag(RandomStringUtils.randomAlphanumeric(12));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testTagMinLength() {
        TagValidator validator = new TagValidator();
        Tag good = new Tag();
        BindException errorsGood = new BindException(good, "good");

        good.setTag(RandomStringUtils.randomAlphanumeric(Tag.TAG_MIN_SIZE));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testTagMaxLength() {
        TagValidator validator = new TagValidator();
        Tag good = new Tag();
        BindException errorsGood = new BindException(good, "good");

        good.setTag(RandomStringUtils.randomAlphanumeric(Tag.TAG_MAX_SIZE));

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testTagLessThanMinLength() {
        TagValidator validator = new TagValidator();
        Tag bad = new Tag();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setTag(RandomStringUtils.randomAlphanumeric(Tag.TAG_MIN_SIZE - 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testTagGreaterThanMaxLength() {
        TagValidator validator = new TagValidator();
        Tag bad = new Tag();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setTag(RandomStringUtils.randomAlphanumeric(Tag.TAG_MAX_SIZE + 1));

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }

    @Test
    public void testTagBadChars() {
        TagValidator validator = new TagValidator();
        Tag bad = new Tag();
        BindException errorsBad = new BindException(bad, "bad");

        bad.setTag("!!!!!");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
}
