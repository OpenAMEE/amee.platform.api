import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * This is a dummy file so that the groovy eclipse compiler can find the groovy tests.
 * We need at least one file (doesn't actually need to be a test) in src/test/java.
 * This is a workaround for http://jira.codehaus.org/browse/GRECLIPSE-1221.
 * See: http://groovy.codehaus.org/Groovy-Eclipse+compiler+plugin+for+Maven.
 */
public class DummyTest {

    @Test
    public void dummy() {
        assertTrue(true);
    }
}
