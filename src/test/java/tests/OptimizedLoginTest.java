package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import org.junit.jupiter.api.*;

import java.util.List;

public class OptimizedLoginTest {
    static private Playwright playwright;
    static private Browser browser;
    private BrowserContext context;
    private Page page;
    static private List<Cookie> authCookies;

    @BeforeAll
    static void setUpClass() {
        // Инициализируем Playwright и браузер один раз для всех тестов
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

        // Выполняем вход один раз и сохраняем cookies
        BrowserContext tempContext = browser.newContext();
        Page tempPage = tempContext.newPage();
        authCookies = performLogin(tempPage);
        tempPage.close();
        tempContext.close();
    }

    @BeforeEach
    void setUp() {
        // Создаём новый контекст с сохранёнными cookies для каждого теста
        context = browser.newContext(new Browser.NewContextOptions()
                .setStorageStatePath(null));

        // Добавляем сохранённые cookies в контекст
        if (authCookies != null && !authCookies.isEmpty()) {
            context.addCookies(authCookies);
        }

        page = context.newPage();
    }

    @Test
    void testSecureArea() {
        page.navigate("https://the-internet.herokuapp.com/secure");
        // Проверяем, что пользователь аутентифицирован
        Assertions.assertTrue(page.locator("h2").textContent().contains("Secure Area"));
    }

    private static List<Cookie> performLogin(Page page) {
        // Переходим на страницу входа
        page.navigate("https://the-internet.herokuapp.com/login");

        // Заполняем форму входа
        page.fill("input#username", "tomsmith");
        page.fill("input#password", "SuperSecretPassword!");

        // Нажимаем кнопку входа
        page.click("button[type='submit']");

        // Ждём, пока загрузится безопасная страница
        page.waitForLoadState(LoadState.NETWORKIDLE);

        // Проверяем успешный вход
        String successMessage = page.locator("div.flash").textContent();
        if (!successMessage.contains("logged into")) {
            throw new RuntimeException("Ошибка входа: " + successMessage);
        }

        // Получаем и возвращаем все cookies
        return page.context().cookies();
    }

    @AfterEach
    void tearDown() {
        if (page != null) page.close();
        if (context != null) context.close();
    }

    @AfterAll
    static void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }
}