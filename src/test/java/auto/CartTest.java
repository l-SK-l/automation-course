package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class CartTest {
    Playwright playwright;
    Browser browser;
    BrowserContext context;
    Page page;
    String timestamp;

    @BeforeEach
    void setup() {
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));
        context = browser.newContext(new Browser.NewContextOptions()
                .setRecordVideoDir(Paths.get(timestamp + "/videos/")));
        page = context.newPage();
    }

    @Test
    void testCartActions() {
        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");

        // Добавление ЕЛЕМЕНТА
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Element")).click();
        page.locator("#elements").screenshot(new Locator.ScreenshotOptions()
                .setPath(getTimestampPath("after_add_element.png")));

        // Удаление ЕЛЕМЕНТА
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Delete")).click();
        assertThat(page.locator("#elements button")).hasCount(0);
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(getTimestampPath("after_remove_element.png")));

    }

    private Path getTimestampPath(String filename) {
        return Paths.get(timestamp + "/screenshots/" + filename);
    }

    @AfterEach
    void teardown() {
        context.close();
        browser.close();
        playwright.close();
    }
}