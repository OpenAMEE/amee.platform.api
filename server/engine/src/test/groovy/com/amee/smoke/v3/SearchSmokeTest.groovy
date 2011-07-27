package com.amee.smoke.v3

import org.junit.Test
import static org.junit.Assert.*
import com.amee.smoke.BaseSmokeTest

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
