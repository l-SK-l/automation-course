package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class StatusCodeInterceptionTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();

        // Перехват запроса к /status_codes/404
        context.route("**/status_codes/404", route -> {
            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setHeaders(Collections.singletonMap("Content-Type", "text/html"))
                    .setBody("<h3>Mocked Success Response</h3>")
            );
        });
    }

    @Test
    void testMockedStatusCode() {
        page.navigate("https://the-internet.herokuapp.com/status_codes");

        // Клик по ссылке "404"
        page.click("a:has-text('404')");

        // Проверка мок-текста
        assertTrue(page.locator("h3").isVisible(), "Элемент h3 не виден на странице");
        assertEquals("Mocked Success Response", page.textContent("h3"));
    }

    @AfterEach
    void tearDown() {
        browser.close();
        playwright.close();
    }
}