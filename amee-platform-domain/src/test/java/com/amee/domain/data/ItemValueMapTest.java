package com.amee.domain.data;

import com.amee.domain.item.data.DataItemTextValue;
import com.amee.domain.item.data.DataItemTextValueHistory;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ItemValueMapTest {

    private ItemValueMap map;

    private DataItemTextValue tv1;
    private DataItemTextValue tv2;
    private DataItemTextValueHistory tvh1;
    private DataItemTextValueHistory tvh2;

    @Before
    public void setup() {
        map = new ItemValueMap();
        tv1 = new DataItemTextValue();
        tv2 = new DataItemTextValue();

        tvh1 = new DataItemTextValueHistory();
        tvh1.setStartDate(dt("12:20"));
        tvh2 = new DataItemTextValueHistory();
        tvh2.setStartDate(dt("11:20"));
        map.put("/foo", tvh1);
        map.put("/foo", tvh2);
        map.put("/foo", tv1);

        // Put an identical non-historical value. This will not be added to the set.
        map.put("/foo", tv2);
    }

    @Test
    public void pathEmpty() {
        assertEquals(0, map.getAll("/bar").size());
    }

    /**
     * Should be sorted in startDate order descending (most recent first).
     */
    @Test
    public void sortOrder() {
        List expected = Arrays.asList(tvh1, tvh2, tv1);
        assertEquals(expected, map.getAll("/foo"));
    }

    @Test
    public void getEarliestValue() {
        assertEquals(tv1, map.get("/foo"));
    }
    
    @Test
    public void getAtStartDate() {
        assertEquals(tvh2, map.get("/foo", dt("12:00")));
        assertEquals(tv1, map.get("/foo", dt("01:00")));
    }

    private Date dt(String s) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
        return fmt.parseDateTime(s).toDate();
    }

}
