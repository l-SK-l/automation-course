package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class HoverTest {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    Page page;

    @BeforeAll
    static void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
    }

    @BeforeEach
    void setupTest() {
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    void testHoverProfiles() {
        page.navigate("https://the-internet.herokuapp.com/hovers",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.LOAD));

        Locator figures = page.locator(".figure");
        int count = figures.count();

        for (int i = 0; i < count; i++) {
            Locator figure = figures.nth(i);
            figure.hover();

            // Проверяем, что появилась ссылка "View profile"
            Locator profileLink = figure.locator("text=View profile");
            assertTrue(profileLink.isVisible(), "Ссылка 'View profile' должна быть видимой после hover");

            // Кликаем
            profileLink.click();

            // Проверяем, что URL соответствует /users/{id}
            String currentUrl = page.url();
            assertTrue(currentUrl.matches(".*/users/\\d+$"),
                "URL должен содержать /users/{id}, но получен: " + currentUrl);

            // Возвращаемся назад
            page.goBack();
        }
    }

    @AfterEach
    void teardownTest() {
        context.close();
    }

    @AfterAll
    static void teardownClass() {
        browser.close();
        playwright.close();
    }
}