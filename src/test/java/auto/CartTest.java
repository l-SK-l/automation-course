package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void testHomePageVisual() throws IOException {
        page.navigate("https://the-internet.herokuapp.com");
        Path actual = Paths.get("actual.png");
        page.screenshot(new Page.ScreenshotOptions().setPath(actual));

        Path expected = Paths.get("expected.png");

        if (!Files.exists(expected)) {
            Files.copy(actual, expected);
            return;
        }

        long mismatch = Files.mismatch(actual, expected);
        assertEquals(-1, mismatch);
    }

    @Test
    void testCartActions() {
        try {
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
        } catch (Exception e) {
            // При ошибке прикрепляем скриншот к Allure
            byte[] screenshot = page.screenshot();
            Allure.addAttachment(
                    "Screenshot on Failure",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
            throw e;
        }
    }

    private Path getTimestampPath(String filename) {
        return Paths.get(timestamp + "/screenshots/" + filename);
    }

    @AfterEach
    void cleanup() {
        context.close();
        browser.close();
        playwright.close();
    }
}