package auto;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT) // Включаем параллельное выполнение
public class ParallelTests {

    @Test
    void testLoginPage() {

        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        page.navigate("https://the-internet.herokuapp.com/login");
        assertEquals("The Internet", page.title());

        context.close();
        browser.close();
        playwright.close();

    }

    @Test
    void testAddRemoveElements() {

        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch();
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        page.navigate("https://the-internet.herokuapp.com/add_remove_elements/");
        page.click("button:text('Add Element')");
        assertTrue(page.isVisible("button.added-manually"));

        context.close();
        browser.close();
        playwright.close();

    }
}