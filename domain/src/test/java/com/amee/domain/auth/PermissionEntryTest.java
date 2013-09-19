package com.amee.domain.auth;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PermissionEntryTest {

    @Test
    public void testEqualsAndHashCode() throws Exception {
        PermissionEntry pe1 = PermissionEntry.OWN;
        PermissionEntry pe2 = PermissionEntry.OWN;
        assertEquals(pe1, pe2);
        assertEquals(pe1.hashCode(), pe2.hashCode());

        PermissionEntry pe3 = PermissionEntry.VIEW_DENY;
        assertFalse(pe1.equals(pe3));
        assertFalse(pe1.hashCode() == pe3.hashCode());

        PermissionEntry pe4 = PermissionEntry.VIEW;
        assertFalse(pe3.equals(pe4));
        assertFalse(pe3.hashCode() == pe4.hashCode());

        PermissionEntry pe5 = PermissionEntry.VIEW_DENY;
        assertEquals(pe3, pe5);
        assertEquals(pe3.hashCode(), pe5.hashCode());

    }

}
