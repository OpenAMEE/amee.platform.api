package com.amee.domain.data;

import com.amee.domain.AMEEStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ItemValueTest {

    private DataCategory mockDataCategory;
    private ItemDefinition mockItemDefinition;
    private DataItem dataItem;
    private ItemValueDefinition mockItemValueDefinition;
    private ItemValue itemValue;

    @Before
    public void setUp() {
        mockDataCategory = mock(DataCategory.class);
        mockItemDefinition = mock(ItemDefinition.class);
        dataItem = new DataItem(mockDataCategory, mockItemDefinition);
        mockItemValueDefinition = mock(ItemValueDefinition.class);
        itemValue = new ItemValue();
        itemValue.setItem(dataItem);
        itemValue.setItemValueDefinition(mockItemValueDefinition);
    }

    @Test
    public void noneTrashed() {
        // An ItemValue should be considered trashed if:
        // itself is trashed or its Item is trashed or its ItemValueDefinition is trashed
        dataItem.setStatus(AMEEStatus.ACTIVE);
        when(mockItemValueDefinition.isTrash()).thenReturn(false);
        // ACTIVE IV + ACTIVE ITEM + ACTIVE IVD
        assertFalse("ItemValue should not be trashed", itemValue.isTrash());
        verify(mockItemValueDefinition).isTrash();
    }

    @Test
    public void itselfTrashed() {
        dataItem.setStatus(AMEEStatus.ACTIVE);
        itemValue.setStatus(AMEEStatus.TRASH);
        // TRASHED IV + ACTIVE ITEM + ACTIVE IVD
        assertTrue("Item value should be trashed", itemValue.isTrash());
    }

    @Test
    public void itemTrashed() {
        dataItem.setStatus(AMEEStatus.TRASH);
        // ACTIVE IV + TRASHED ITEM + ACTIVE IVD
        assertTrue("ItemValue should be trashed", itemValue.isTrash());
    }

    @Test
    public void itemValueDefinitionTrashed() {
        dataItem.setStatus(AMEEStatus.ACTIVE);
        when(mockItemValueDefinition.isTrash()).thenReturn(true);
        // ACTIVE IV + ACTIVE ITEM + TRASHED IVD
        assertTrue("ItemValue should be trashed", itemValue.isTrash());
        verify(mockItemValueDefinition).isTrash();
    }
}
