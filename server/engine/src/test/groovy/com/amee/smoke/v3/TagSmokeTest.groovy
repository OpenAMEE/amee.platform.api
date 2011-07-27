package com.amee.smoke.v3

import org.junit.Test
import static org.junit.Assert.*
import com.amee.smoke.BaseSmokeTest

/**
 * Smoke tests for the /tags resources.
 */
class TagSmokeTest extends BaseSmokeTest {

    @Test
    void listTags() {
        def response = client.get(path: "/3/tags")
        assertResponseOk response
        assertTrue response.data.tags.size() > 1
    }

    @Test
    void getTag() {
        def response = client.get(path: "/3/tags/airplane")
        assertResponseOk response
        assertEquals "airplane", response.data.tag.tag
    }

}
