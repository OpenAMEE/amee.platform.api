package com.amee.platform.science;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class ReturnValueTest {
    
    @Test
    public void testGetCompoundUnit() throws Exception {
        
        ReturnValue returnValue = new ReturnValue("CO2", "kg", "year", 1.23);
        assertEquals("kg/year", returnValue.getCompoundUnit());

        returnValue = new ReturnValue("CO2", "kg", null, 1.23);
        assertEquals("kg", returnValue.getCompoundUnit());

        returnValue = new ReturnValue("CO2", null, null, 1.23);
        assertEquals("", returnValue.getCompoundUnit());
    }
}
