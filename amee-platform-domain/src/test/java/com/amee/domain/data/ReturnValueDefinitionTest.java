package com.amee.domain.data;

import com.amee.domain.AMEEStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ReturnValueDefinitionTest {
    private ReturnValueDefinition returnValueDef;
    private ItemDefinition mockItemDef;

    @Before
    public void setUp(){
        returnValueDef = new ReturnValueDefinition();
        mockItemDef = mock(ItemDefinition.class);
        returnValueDef.setItemDefinition(mockItemDef);
    }

    @Test
    public void noneTrashed() {

        // A ReturnValueDefinition should be considered trashed if:
        // itself is trashed or its ItemDefinition is trashed.

        when(mockItemDef.isTrash()).thenReturn(false);
        assertFalse("ItemValueDefinition should not be trashed", returnValueDef.isTrash());
        verify(mockItemDef).isTrash();
    }

    @Test
    public void itselfTrashed() {
        returnValueDef.setStatus(AMEEStatus.TRASH);
        assertTrue("ItemValueDefinition should be trashed", returnValueDef.isTrash());
    }

    @Test
    public void itemDefTrashed() {
        when(mockItemDef.isTrash()).thenReturn(true);
        assertTrue("ItemValueDefinition should be trashed", returnValueDef.isTrash());
        verify(mockItemDef).isTrash();
    }
}
