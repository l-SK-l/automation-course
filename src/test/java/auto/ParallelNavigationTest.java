package auto;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {
    private static final String BASE_URL = "https://the-internet.herokuapp.com";
    static Playwright chromiumPlaywright;
    static Playwright firefoxPlaywright;
    static volatile Browser chromiumBrowser;
    static volatile Browser firefoxBrowser;

    @BeforeAll
    static void setup() {
        chromiumPlaywright = Playwright.create();
        chromiumBrowser = chromiumPlaywright.chromium().launch();
        firefoxPlaywright = Playwright.create();
        firefoxBrowser = firefoxPlaywright.firefox().launch();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/login", "/dropdown", "/javascript_alerts", "/checkboxes", "/hover", "/status_codes"})
    void testChromium(String path) {
        synchronized (chromiumBrowser) {
            BrowserContext context = chromiumBrowser.newContext();
            Page page = context.newPage();
            page.navigate(BASE_URL + path);
            assertTrue(page.url().contains(BASE_URL + path));
            context.close();
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/login", "/dropdown", "/javascript_alerts", "/checkboxes", "/hover", "/status_codes"})
    void testFirefox(String path) {
        synchronized (firefoxBrowser) {
            BrowserContext context = firefoxBrowser.newContext();
            Page page = context.newPage();
            page.navigate(BASE_URL + path);
            assertTrue(page.url().contains(BASE_URL + path));
            context.close();
        }
    }

    @AfterAll
    static void tearDown() {
        chromiumBrowser.close();
        chromiumPlaywright.close();
        firefoxBrowser.close();
        firefoxPlaywright.close();
    }
}