package auto;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Execution(ExecutionMode.CONCURRENT)
public class ParallelNavigationTest {
    private static final String BASE_URL = "https://the-internet.herokuapp.com";

    @ParameterizedTest
    @CsvSource({
            "chromium, /",
            "chromium, /login",
            "chromium, /dropdown",
            "chromium, /javascript_alerts",
            "chromium, /checkboxes",
            "chromium, /hover",
            "chromium, /status_codes",
            "firefox, /",
            "firefox, /login",
            "firefox, /dropdown",
            "firefox, /javascript_alerts",
            "firefox, /checkboxes",
            "firefox, /hover",
            "firefox, /status_codes"
    })
    void testPageNavigation(String browserType, String path) {
        try (Playwright playwright = Playwright.create()) {
            BrowserType type = switch (browserType.toLowerCase()) {
                case "chromium" -> playwright.chromium();
                case "firefox" -> playwright.firefox();
                default -> throw new IllegalArgumentException("Неподдерживаемый браузер: " + browserType);
            };
            try (Browser browser = type.launch()) {
                try (BrowserContext context = browser.newContext()) {
                    Page page = context.newPage();
                    page.navigate(BASE_URL + path);
                    assertTrue(page.url().contains(BASE_URL + path));
                }
            }
        }
    }
}