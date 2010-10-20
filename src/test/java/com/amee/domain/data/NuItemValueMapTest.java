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

public class NuItemValueMapTest {

    private NuItemValueMap map;

    private DataItemTextValueHistory tvh1, tvh2;
    private DataItemTextValue tv1, tv2;

    @Before
    public void setup() {
        map = new NuItemValueMap();

        tvh1 = new DataItemTextValueHistory();
        tvh2 = new DataItemTextValueHistory();

        tv1 = new DataItemTextValue();
        tv2 = new DataItemTextValue();
    }

    @Test
    public void pathEmpty() {
        assertEquals(0, map.getAll("/foo").size());
    }

    @Test
    public void pathWithExternalHistoryValuesOnly() {
        tvh1.setStartDate(dt("12:20"));
        map.put("/foo", tvh1);

        tvh2.setStartDate(dt("11:20"));
        map.put("/foo", tvh2);

        List expected = Arrays.asList(tvh2, tvh1);
        assertEquals(expected, map.getAll("/foo"));        
    }

    @Test
    public void pathWithMixedItems() {
        tvh1.setStartDate(dt("18:15"));
        map.put("/foo", tvh1);

        map.put("/foo", tv1);

        tvh2.setStartDate(dt("11:11"));
        map.put("/foo", tvh2);

        map.put("/foo", tv2);

        List expected = Arrays.asList(tv1, tvh2, tvh1);
        assertEquals(expected, map.getAll("/foo"));                
    }

    Date dt(String s) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
        return fmt.parseDateTime(s).toDate();
    }

}
