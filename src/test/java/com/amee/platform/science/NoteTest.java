package com.amee.platform.science;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import static org.junit.Assert.*;

public class NoteTest {

    @Test(expected = IllegalArgumentException.class)
    public void typeExceedsMaxLength() {
        new Note(StringUtils.repeat("a", Note.MAX_TYPE_LENGTH + 1), "foo");
    }

    @Test
    public void typeIsMaxLength() {
        assertNotNull("Failed to create note", new Note(StringUtils.repeat("a", Note.MAX_TYPE_LENGTH), "foo"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueExceedsMaxLength() {
        new Note("foo", StringUtils.repeat("a", Note.MAX_VALUE_LENGTH + 1));
    }

    @Test
    public void valueIsMaxLength() {
        assertNotNull("Failed to create note", new Note("foo", StringUtils.repeat("a", Note.MAX_VALUE_LENGTH)));
    }
}
