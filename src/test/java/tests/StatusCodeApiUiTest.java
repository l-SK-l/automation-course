package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatusCodeApiUiTest {
    private Playwright playwright;
    private APIRequestContext apiRequest;
    private Browser browser;
    private Page page;

    @BeforeEach
    void setup() {
        playwright = Playwright.create();

        // Настройка API контекста
        apiRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://the-internet.herokuapp.com")
        );

        // Настройка браузера
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

        page = browser.newPage();

        // Навигация на страницу статус кодов один раз
        page.navigate("https://the-internet.herokuapp.com/status_codes");
        page.waitForSelector("div.example");
    }

    @Test
    void testStatusCodesCombined() {
        // Тестируем статус коды 200 и 404
        int[] statusCodesToTest = {200, 404};

        for (int code : statusCodesToTest) {
            int apiStatusCode = getApiStatusCode(code);
            int uiStatusCode = getUiStatusCode(code);

            assertEquals(apiStatusCode, code, "API должен вернуть статус код " + code);
            assertEquals(uiStatusCode, code, "UI должен получить статус код " + code);
            assertEquals(apiStatusCode, uiStatusCode, "API и UI статус коды должны совпадать для кода " + code);

            // Возвращаемся на страницу статус кодов для следующей итерации
            if (code != statusCodesToTest[statusCodesToTest.length - 1]) {
                page.navigate("https://the-internet.herokuapp.com/status_codes");
                page.waitForSelector("div.example");
            }
        }
    }

    private int getApiStatusCode(int code) {
        APIResponse response = apiRequest.get("/status_codes/" + code);
        return response.status();
    }

    private int getUiStatusCode(int code) {
        try {
            // Используем селектор по тексту ссылки
            String selector = "a:text-matches('^" + code + "$')";
            Locator link = page.locator(selector);

            Response response = page.waitForResponse(
                    res -> res.url().endsWith("/status_codes/" + code),
                    () -> link.click()
            );
            return response.status();
        } catch (PlaywrightException e) {
            throw new RuntimeException("Failed to get UI status code for: " + code, e);
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