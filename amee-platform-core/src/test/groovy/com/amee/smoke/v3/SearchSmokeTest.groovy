package com.amee.smoke.v3

import com.amee.smoke.BaseSmokeTest
import org.junit.Test
import static org.junit.Assert.assertTrue

class SearchSmokeTest extends BaseSmokeTest {

    @Test
    void doSearch() {
        def response = client.get(
            path: "/3/search",
            query: ["q": "aircraft"])
        assertResponseOk(response)
        assertTrue response.data.results.size() > 1
    }
}
