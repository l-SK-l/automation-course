package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MobileDynamicControlsTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();

        // Настройка параметров iPad Pro 11
        Browser.NewContextOptions deviceOptions = new Browser.NewContextOptions()
                .setUserAgent("Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko)")
                .setViewportSize(834, 1194)
                .setDeviceScaleFactor(2)
                .setIsMobile(true)
                .setHasTouch(true);

        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)); // Установите headless на false для визуализации
        context = browser.newContext(deviceOptions);
        page = context.newPage();
    }

    @Test
    void testInputEnabling() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        // Находим поле ввода и проверяем, что оно неактивно
        Locator inputField = page.locator("input[type='text']");
        assert inputField.isDisabled() : "Input field should be disabled initially";

        // Нажимаем на кнопку "Enable" с помощью tap (касание)
        page.locator("button:has-text('Enable')").tap();

        // Ждем появления сообщения о том, что поле включено
        page.locator("#message").waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

        // Проверяем, что поле ввода стало активным
        assert inputField.isEnabled() : "Input field should be enabled after clicking Enable button";

        // Дополнительная проверка: проверяем текст сообщения
        String message = page.locator("#message").textContent();
        assert message.equals("It's enabled!") : "Expected message 'It's enabled!' but got: " + message;
    }

    @AfterEach
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
