package com.amee.domain.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ItemDefinitionTest {

    public final static String MOCK_USAGES_STRING =
            "usage_1,usage_2,usage_3";

    public final static List<String> MOCK_USAGES_LIST =
            new ArrayList<String>(Arrays.asList("usage_1", "usage_2", "usage_3"));

    @Test
    public void canUseUsagesFromString() {
        ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setUsages(MOCK_USAGES_STRING);
        assertTrue("ItemDefinition should contain 3 usages. ",
                itemDefinition.getUsages().size() == 3);
        assertTrue("ItemDefinition should contain expected usage named 'usage_1' at position 0.",
                itemDefinition.getUsages().get(0).equals("usage_1"));
        assertTrue("ItemDefinition should contain expected usage named 'usage_2' at position 1.",
                itemDefinition.getUsages().get(1).equals("usage_2"));
        assertTrue("ItemDefinition should contain expected usage named 'usage_3' at position 2.",
                itemDefinition.getUsages().get(2).equals("usage_3"));
        assertEquals("ItemDefinition usages String should look right.", MOCK_USAGES_STRING, itemDefinition.getUsagesString());
    }

    @Test
    public void canUseUsagesFromList() {
        ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setUsages(MOCK_USAGES_LIST);
        assertTrue("ItemDefinition should contain 3 usages. ",
                itemDefinition.getUsages().size() == 3);
        assertTrue("ItemDefinition should contain expected usage named 'usage_1' at position 0.",
                itemDefinition.getUsages().get(0).equals("usage_1"));
        assertTrue("ItemDefinition should contain expected usage named 'usage_2' at position 1.",
                itemDefinition.getUsages().get(1).equals("usage_2"));
        assertTrue("ItemDefinition should contain expected usage named 'usage_3' at position 2.",
                itemDefinition.getUsages().get(2).equals("usage_3"));
        assertEquals("ItemDefinition usages String should look right.", MOCK_USAGES_STRING, itemDefinition.getUsagesString());
    }
}
