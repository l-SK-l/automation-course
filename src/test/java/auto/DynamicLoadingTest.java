package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DynamicLoadingTest {
    Playwright playwright;
    Browser browser;
    Page page;
    BrowserContext context;
    boolean requestSuccessful = false;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testDynamicLoading() {
        // Запуск трассировки
        context.tracing().start(new Tracing.StartOptions()
            .setScreenshots(true)
            .setSnapshots(true));

        // Перехват запросов и проверка статуса ДО клика
        page.onResponse(response -> {
            System.out.println("Response URL: " + response.url() + ", Status: " + response.status());
            if (response.url().contains("/dynamic_loading")) {
                System.out.println("Found dynamic_loading response with status: " + response.status());
                if (response.status() == 200) {
                    requestSuccessful = true;
                }
            }
        });

        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");

        // Клик по кнопке для запуска загрузки
        page.click("button");

        // Ожидание появления текста "Hello World!"
        Locator finishText = page.locator("#finish");
        finishText.waitFor(new Locator.WaitForOptions().setTimeout(10000));

        String text = finishText.textContent();
        assertTrue(text.trim().equals("Hello World!") || text.contains("Hello World!"),
            "Text should contain 'Hello World!', but got: '" + text + "'");

        // Проверка, что запрос был успешным
        assertTrue(requestSuccessful, "Request should return status 200");

        // Сохранение трассировки
        context.tracing().stop(new Tracing.StopOptions()
            .setPath(Paths.get("trace/trace-success.zip")));
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}