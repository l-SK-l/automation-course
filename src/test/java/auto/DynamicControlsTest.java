package auto;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class DynamicControlsTest {
    Playwright playwright;
    Browser browser;
    Page page;

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        page = browser.newPage();
    }

    @Test
    void testDynamicCheckbox() {
        page.navigate("https://the-internet.herokuapp.com/dynamic_controls");

        // Находим чекбокс с атрибутом type="checkbox"
        var checkbox = page.locator("input[type='checkbox']");

        // Проверяем, что чекбокс изначально видим
        assertThat(checkbox).isVisible();

        // Кликаем на кнопку "Remove"
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Remove")).click();

        // Ожидаем исчезновения чекбокса и проверяем
        assertThat(checkbox).isHidden();

        // Проверяем, что появляется текст "It's gone!"
        var message = page.locator("#message");
        assertThat(message).hasText("It's gone!");

        // Кликаем на кнопку "Add"
        page.getByRole(com.microsoft.playwright.options.AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName("Add")).click();

        // Проверяем, что чекбокс снова отображается
        assertThat(checkbox).isVisible();
    }

    @AfterEach
    void tearDown() {
        page.close();
        browser.close();
        playwright.close();
    }
}