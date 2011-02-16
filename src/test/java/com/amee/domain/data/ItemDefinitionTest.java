package com.amee.domain.data;

import com.amee.base.utils.ThreadBeanHolder;
import com.amee.domain.ILocaleService;
import com.amee.domain.IMetadataService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemDefinitionTest {

    public final static String MOCK_USAGES_STRING =
            "usage_1,usage_2,usage_3";

    public final static List<String> MOCK_USAGES_LIST =
            new ArrayList<String>(Arrays.asList("usage_1", "usage_2", "usage_3"));

    private IMetadataService mockMetadataService;
    private ILocaleService localeService;

    @Before
    public void setUp() {
        ThreadBeanHolder.clear();
        mockMetadataService = mock(IMetadataService.class);
        ThreadBeanHolder.set(IMetadataService.class, mockMetadataService);
        localeService = mock(ILocaleService.class);
        ThreadBeanHolder.set(ILocaleService.class, localeService);
    }

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

    @Test
    public void canHandleEmptyUsages() {
        ItemDefinition itemDefinition = new ItemDefinition();
        assertTrue("ItemDefinition should contain 0 usages. ",
                itemDefinition.getUsages().isEmpty());
        itemDefinition.setUsages(new ArrayList<String>());
        assertTrue("ItemDefinition should contain 0 usages. ",
                itemDefinition.getUsages().isEmpty());
        itemDefinition.setUsages("");
        assertTrue("ItemDefinition should contain 0 usages. ",
                itemDefinition.getUsages().isEmpty());
        itemDefinition.setUsages(" , , , sss, xxx, , ");
        assertTrue("ItemDefinition should contain 2 usages. ",
                itemDefinition.getUsages().size() == 2);
    }

    @Test
    public void canGetModifiedDateFromIVD() {
        // Create ItemDefinition.
        ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setModified(new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate());
        // Create ItemDefinition One.
        ItemValueDefinition itemValueDefinition1 = new ItemValueDefinition(itemDefinition);
        itemValueDefinition1.setModified(new DateTime(2011, 1, 2, 0, 0, 0, 0).toDate());
        itemDefinition.add(itemValueDefinition1);
        mockLocaleName(itemValueDefinition1);
        // Create ItemDefinition Two.
        ItemValueDefinition itemValueDefinition2 = new ItemValueDefinition(itemDefinition);
        itemValueDefinition2.setModified(new DateTime(2011, 1, 3, 0, 0, 0, 0).toDate());
        itemDefinition.add(itemValueDefinition2);
        mockLocaleName(itemValueDefinition2);
        // Should get the date of ItemValueDefinition Two.
        assertTrue(
                "ItemValueDefinition Two should define the most recent modified date.",
                itemDefinition.getModifiedDeep().equals(new DateTime(2011, 1, 3, 0, 0, 0, 0).toDate()));
    }

    @Test
    public void canGetModifiedDateFromID() {
        // Create ItemDefinition.
        ItemDefinition itemDefinition = new ItemDefinition();
        itemDefinition.setModified(new DateTime(2011, 1, 3, 0, 0, 0, 0).toDate());
        // Create ItemDefinition One.
        ItemValueDefinition itemValueDefinition1 = new ItemValueDefinition(itemDefinition);
        itemValueDefinition1.setModified(new DateTime(2011, 1, 2, 0, 0, 0, 0).toDate());
        itemDefinition.add(itemValueDefinition1);
        mockLocaleName(itemValueDefinition1);
        // Create ItemDefinition Two.
        ItemValueDefinition itemValueDefinition2 = new ItemValueDefinition(itemDefinition);
        itemValueDefinition2.setModified(new DateTime(2011, 1, 1, 0, 0, 0, 0).toDate());
        itemDefinition.add(itemValueDefinition2);
        mockLocaleName(itemValueDefinition2);
        // Should get the date of ItemDefinition.
        assertTrue(
                "The ItemDefinition should define the most recent modified date.",
                itemDefinition.getModifiedDeep().equals(new DateTime(2011, 1, 3, 0, 0, 0, 0).toDate()));
    }

    private void mockLocaleName(ItemValueDefinition itemValueDefinition2) {
        when(localeService.getLocaleNameValue(itemValueDefinition2, "")).thenAnswer(new Answer<String>() {
            public String answer(InvocationOnMock invocation) throws Throwable {
                return (String) invocation.getArguments()[1];
            }
        });
    }
}
