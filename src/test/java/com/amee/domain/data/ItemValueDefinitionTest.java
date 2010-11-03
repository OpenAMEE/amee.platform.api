package com.amee.domain.data;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.AMEEStatus;
import com.amee.domain.IMetadataService;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class ItemValueDefinitionTest {

    public final static String MOCK_CONFIGURATION_WITH_USAGES =
            "{\"usages\":[{\"name\":\"usage_1\",\"type\":\"compulsory\"},{\"name\":\"usage_2\",\"type\":\"optional\"}]}";

    private IMetadataService mockMetadataService;
    private ItemValueDefinition itemValueDef;
    private ItemDefinition mockItemDef;

    @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        mockMetadataService = mock(IMetadataService.class);
        ThreadBeanHolder.set("metadataService", mockMetadataService);
        itemValueDef = new ItemValueDefinition();
        mockItemDef = mock(ItemDefinition.class);
        itemValueDef.setItemDefinition(mockItemDef);
    }

    @Test
    public void noneTrashed() {

        // An ItemValueDefinition should be considered trashed if:
        // itself is trashed or its ItemDefinition is trashed.

        when(mockItemDef.isTrash()).thenReturn(false);
        assertFalse("ItemValueDefinition should not be trashed", itemValueDef.isTrash());
        verify(mockItemDef).isTrash();
    }

    @Test
    public void itselfTrashed() {
        itemValueDef.setStatus(AMEEStatus.TRASH);
        assertTrue("ItemValueDefinition should be trashed", itemValueDef.isTrash());
    }

    @Test
    public void itemDefTrashed() {
        when(mockItemDef.isTrash()).thenReturn(true);
        assertTrue("ItemValueDefinition should be trashed", itemValueDef.isTrash());
        verify(mockItemDef).isTrash();
    }

    @Test
    public void canUseUsagesInConfiguration() {
        ItemValueDefinition itemValueDefinition = new ItemValueDefinition();
        assertTrue("ItemValueDefinition should contain 0 ItemValueUsages. ",
                itemValueDefinition.getItemValueUsages().isEmpty());
        itemValueDefinition.setConfiguration(MOCK_CONFIGURATION_WITH_USAGES);
        Set<ItemValueUsage> itemValueUsages = itemValueDefinition.getItemValueUsages();
        assertTrue("ItemValueDefinition should contain 2 ItemValueUsages. ",
                itemValueUsages.size() == 2);
        assertTrue("ItemValueDefinition should contain expected ItemValueUsage named 'usage_1'.",
                itemValueUsages.contains(new ItemValueUsage("usage_1")));
        assertTrue("ItemValueDefinition should contain expected ItemValueUsage named 'usage_2'.",
                itemValueUsages.contains(new ItemValueUsage("usage_2")));
    }
}
