package com.amee.domain.data;

import com.amee.domain.ValueUsageType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class ItemValueUsageTest {

    @Test
    public void hasCorrectEquality() {
        ItemValueUsage itemValueUsage = new ItemValueUsage("a_name");
        assertEquals("Two identical ItemValueUsages should be equal.",
                itemValueUsage, itemValueUsage);
        assertEquals("Two default ItemValueUsages should be equal.",
                new ItemValueUsage(), new ItemValueUsage());
        assertEquals("Two identically named ItemValueUsages should be equal.",
                new ItemValueUsage("a_name"), new ItemValueUsage("a_name"));
        assertEquals("Two identically named & typed ItemValueUsages should be equal.",
                new ItemValueUsage("a_name", ValueUsageType.REQUIRED), new ItemValueUsage("a_name", ValueUsageType.REQUIRED));
        assertEquals("Two identically named but differently typed ItemValueUsages should be equal.",
                new ItemValueUsage("a_name", ValueUsageType.REQUIRED), new ItemValueUsage("a_name", ValueUsageType.OPTIONAL));
        assertFalse("Two differently named ItemValueUsages should not be equal.",
                new ItemValueUsage("a_name").equals(new ItemValueUsage("a_different_name")));
        assertFalse("Two differently named & typed ItemValueUsages should not be equal.",
                new ItemValueUsage("a_name", ValueUsageType.REQUIRED).equals(new ItemValueUsage("a_different_name", ValueUsageType.OPTIONAL)));
        assertFalse("Two differently named but identically typed ItemValueUsages should not be equal.",
                new ItemValueUsage("a_name", ValueUsageType.REQUIRED).equals(new ItemValueUsage("a_different_name", ValueUsageType.REQUIRED)));
    }

    @Test
    public void canParseCorrectItemValueUsageJSON() {
        try {
            JSONObject itemValueUsageObj = new JSONObject().put("name", "usage").put("type", ValueUsageType.REQUIRED.toString());
            ItemValueUsage itemValueUsage = new ItemValueUsage(itemValueUsageObj);
            assertTrue("New ItemValueUsage should have correct name.", itemValueUsage.getName().equalsIgnoreCase("usage"));
            assertTrue("New ItemValueUsage should have correct type.", itemValueUsage.getType().equals(ValueUsageType.REQUIRED));
        } catch (IllegalArgumentException e) {
            fail("Should not throw IllegalArgumentException: " + e.getMessage());
        } catch (JSONException e) {
            fail("Should not throw JSONException: " + e.getMessage());
        }
    }

    @Test
    public void canParseCorrectItemValueUsagesJSON() {
        try {
            JSONArray arr = new JSONArray();
            arr.put(new JSONObject().put("name", "usage1").put("type", ValueUsageType.REQUIRED.toString()));
            arr.put(new JSONObject().put("name", "usage2").put("type", ValueUsageType.FORBIDDEN.toString()));
            arr.put(new JSONObject().put("name", "usage3").put("type", ValueUsageType.OPTIONAL.toString()));
            Set<ItemValueUsage> itemValueUsages = ItemValueUsage.deserialize(arr);
            assertTrue("There should be 3 ItemValueUsages.", itemValueUsages.size() == 3);
        } catch (IllegalArgumentException e) {
            fail("Should not throw IllegalArgumentException: " + e.getMessage());
        } catch (JSONException e) {
            fail("Should not throw JSONException: " + e.getMessage());
        }
    }

    @Test
    public void canParseIncorrectItemValueUsagesJSON() {
        try {
            JSONArray arr = new JSONArray();
            arr.put(new JSONObject().put("name", "usage1").put("type", ValueUsageType.REQUIRED.toString()));
            arr.put(new JSONObject().put("name", "usage1").put("type", ValueUsageType.FORBIDDEN.toString()));
            arr.put(new JSONObject().put("name", "usage1").put("type", ValueUsageType.OPTIONAL.toString()));
            ItemValueUsage.deserialize(arr);
        } catch (IllegalArgumentException e) {
            // Swallow.
            return;
        } catch (JSONException e) {
            fail("Should not throw JSONException: " + e.getMessage());
        }
        fail("Should have caught an IllegalArgumentException.");
    }

    @Test
    public void canParseEmptyItemValueUsagesJSON() {
        assertTrue("There should be 0 ItemValueUsages.", ItemValueUsage.deserialize(null).isEmpty());
        assertTrue("There should be 0 ItemValueUsages.", ItemValueUsage.deserialize(new JSONArray()).isEmpty());
    }
}
