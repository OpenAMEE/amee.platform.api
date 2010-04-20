package com.amee.base.domain;

import org.junit.Test;

import static junit.framework.Assert.*;

public class VersionTest {

    @Test
    public void shouldBeValid() {
        assertTrue(Version.isValidVersion("1"));
        assertTrue(Version.isValidVersion("01"));
        assertTrue(Version.isValidVersion("1.1"));
        assertTrue(Version.isValidVersion("10"));
        assertTrue(Version.isValidVersion("10.20"));
        assertTrue(Version.isValidVersion("1.2.3"));
        assertTrue(Version.isValidVersion("1.X"));
        assertTrue(Version.isValidVersion("1.1.X"));
        assertTrue(Version.isValidVersion("x"));
    }

    @Test
    public void shouldNotBeValid() {
        assertFalse(Version.isValidVersion(""));
        assertFalse(Version.isValidVersion("!"));
        assertFalse(Version.isValidVersion("y"));
        assertFalse(Version.isValidVersion("x.1"));
        assertFalse(Version.isValidVersion("x.x.1"));
        assertFalse(Version.isValidVersion("1.x.1"));
        assertFalse(Version.isValidVersion("1.2.3.4"));
    }

    @Test
    public void shouldBeEqual() {
        assertEquals(new Version("1"), new Version("1"));
        assertEquals(new Version("01"), new Version("01"));
        assertEquals(new Version("01"), new Version("1"));
        assertEquals(new Version("1.1"), new Version("1.1"));
        assertEquals(new Version("1.01"), new Version("1.01"));
        assertEquals(new Version("10"), new Version("10"));
        assertEquals(new Version("10.20"), new Version("10.20"));
        assertEquals(new Version("1.2.3"), new Version("1.2.3"));
    }

    @Test
    public void shouldBeBefore() {
        assertTrue(new Version("1").before(new Version("2")));
        assertTrue(new Version("01").before(new Version("02")));
        assertTrue(new Version("10").before(new Version("20")));
        assertTrue(new Version("2.2").before(new Version("2.3")));
        assertTrue(new Version("2.2.2").before(new Version("2.2.3")));
        assertTrue(new Version("2.1.1").before(new Version("2.1")));
    }

    @Test
    public void shouldBeAfter() {
        assertTrue(new Version("2").after(new Version("1")));
        assertTrue(new Version("02").after(new Version("01")));
        assertTrue(new Version("20").after(new Version("10")));
        assertTrue(new Version("2.3").after(new Version("2.2")));
        assertTrue(new Version("2.2.3").after(new Version("2.2.2")));
        assertTrue(new Version("2.1").after(new Version("2.1.1")));
    }
}
