package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.Paths;

public class DynamicLoadingTraceTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @Test
    void testDynamicLoadingWithTrace() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext();

        // Настройка трассировки
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        page = context.newPage();

        // Шаги теста
        page.navigate("https://the-internet.herokuapp.com/dynamic_loading/1");
        page.click("button"); // Клик на "Start"

        // Ожидание появления текста
        page.locator("#finish").waitFor();

        // Сохранение трассировки
        context.tracing().stop(new Tracing.StopOptions()
                .setPath(Paths.get("trace-dynamic-loading.zip")));
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}