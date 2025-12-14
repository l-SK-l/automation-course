package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GitHubSearchInterceptionTest {
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

        // Перехват запроса поиска
        context.route("**/search**", route -> {
            // Получаем оригинальный URL
            String originalUrl = route.request().url();

            // Декодируем и модифицируем параметры
            String modifiedUrl = originalUrl.contains("q=")
                    ? originalUrl.replaceAll("q=[^&]+", "q=stars%3A%3E10000")
                    : originalUrl + (originalUrl.contains("?") ? "&" : "?") + "q=stars%3A%3E10000";

            // Продолжаем запрос с модифицированным URL
            route.resume(new Route.ResumeOptions().setUrl(modifiedUrl));
        });
    }

    @Test
    void testSearchModification() {
        page.navigate("https://github.com/search?q=java");

        // Ожидаем появления результатов
        page.locator("h1 >> text=stars:>10000").waitFor();

        // Проверяем модифицированный запрос в заголовке страницы
        String heading = page.locator("h1 >> text=stars:>10000").textContent();
        assertTrue(heading.contains("stars:>10000"),
            "Заголовок страницы не содержит 'stars:>10000'");
    }

    @AfterEach
    void tearDown() {
        if (page != null) {
            page.close();
        }
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}