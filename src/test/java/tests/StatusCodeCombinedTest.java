package tests;

import com.microsoft.playwright.*;
import org.aeonbits.owner.Config;
import org.aeonbits.owner.ConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

// Конфигурационный интерфейс с Owner
@Config.Sources({
    "file:src/test/resources/config-${env}.properties",
    "file:src/test/resources/config-dev.properties"
})
interface EnvConfig extends Config {
    @Config.Key("baseUrl")
    String baseUrl();
}

public class StatusCodeCombinedTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;
    private static EnvConfig config;

    @BeforeAll
    static void loadConfig() {
        config = ConfigFactory.create(EnvConfig.class);
    }

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // API контекст с базовым URL из конфига
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(config.baseUrl())
        );

        // Настройка браузера
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

        page = browser.newPage();
        page.setDefaultTimeout(40000);
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 404})
    void testStatusCodeCombined(int statusCode) {
        // API проверка
        int apiStatusCode = getApiStatusCode(statusCode);

        // UI проверка
        int uiStatusCode = getUiStatusCode(statusCode);

        // Сравнение результатов
        assertEquals(apiStatusCode, uiStatusCode,
                "API и UI статус коды должны совпадать для кода " + statusCode);
    }

    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        assertEquals(code, response.status(),
                "API: Неверный статус код для " + code);
        return response.status();
    }

    private int getUiStatusCode(int code) {
        try {
            // Навигация на страницу статус кодов
            page.navigate(config.baseUrl() + "/status_codes");
            page.waitForSelector("div.example");

            // Локатор
            Locator link = page.locator(
                    String.format("a[href*='status_codes/%d']", code)
            ).first();

            // Перехват ответа перед кликом
            Response response = page.waitForResponse(
                    res -> res.url().endsWith("/status_codes/" + code),
                    () -> link.click(new Locator.ClickOptions().setTimeout(10000))
            );

            return response.status();

        } catch (Exception e) {
            // Скриншот с именем, включающим код ошибки
            String screenshotName = String.format("error_status_%d_%d.png", code, System.currentTimeMillis());
            page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Paths.get(screenshotName)));
            throw new RuntimeException("UI проверка упала для кода " + code, e);
        }
    }

    @AfterEach
    void teardown() {
        if (apiRequest != null) {
            apiRequest.dispose();
        }
        if (page != null) {
            page.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
