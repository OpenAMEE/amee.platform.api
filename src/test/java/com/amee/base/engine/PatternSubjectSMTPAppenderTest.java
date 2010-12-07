package com.amee.base.engine;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PatternSubjectSMTPAppenderTest {

    private LoggingEvent mockEvent;
    private final PatternSubjectSMTPAppender.EmailEvaluator emailEvaluator = new PatternSubjectSMTPAppender.EmailEvaluator();

    @Before
    public void setUp() {
        // mock the logging event
        mockEvent = mock(LoggingEvent.class);
    }

    @Test
    public void testTriggeringEvent() {
        when(mockEvent.getLevel()).thenReturn(Level.ERROR);

        System.setProperty("amee.maillog", "false");
        assertFalse(emailEvaluator.isTriggeringEvent(mockEvent));

        System.setProperty("amee.maillog", "true");
        assertTrue(emailEvaluator.isTriggeringEvent(mockEvent));

        System.clearProperty("amee.maillog");
        assertFalse(emailEvaluator.isTriggeringEvent(mockEvent));
    }
}
