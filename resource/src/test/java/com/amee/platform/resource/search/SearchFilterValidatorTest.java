package com.amee.platform.resource.search;

import com.amee.platform.search.SearchFilter;
import org.apache.lucene.search.BooleanQuery;
import org.junit.Test;
import org.springframework.validation.BindException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SearchFilterValidatorTest {

    @Test
    public void testValid() {
        SearchFilterValidator validator = new SearchFilterValidator();
        SearchFilter good = new SearchFilter();
        BindException errorsGood = new BindException(good, "good");

        good.setQ(new BooleanQuery());

        validator.validate(good, errorsGood);
        assertFalse("Object should not fail validation: (" + errorsGood.getMessage() + ")", errorsGood.hasErrors());
    }

    @Test
    public void testEmptyQuery() {
        SearchFilterValidator validator = new SearchFilterValidator();
        SearchFilter bad = new SearchFilter();
        BindException errorsBad = new BindException(bad, "bad");

        validator.validate(bad, errorsBad);
        assertTrue("Object should fail validation", errorsBad.hasErrors());
    }
}
