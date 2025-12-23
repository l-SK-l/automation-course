package auto;

import com.microsoft.playwright.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Epic("Веб-интерфейс тестов")
@Feature("Операции с чекбоксами")
public class CheckboxTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private String timestamp;

    @BeforeEach
    @Step("Инициализация браузера и контекста")
    void setUp() {
        timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        context = browser.newContext();
        page = context.newPage();
    }

    @Test
    @Story("Проверка работы чекбоксов")
    @DisplayName("Тестирование выбора/снятия чекбоксов")
    @Severity(SeverityLevel.CRITICAL)
    void testCheckboxes() {
        try {
            navigateToCheckboxesPage();
            verifyInitialState();
            toggleCheckboxes();
            verifyToggledState();
        } catch (Exception e) {
            captureScreenshot("failure-screenshot.png");
            throw e;
        }
    }

    @Step("Переход на страницу /checkboxes")
    private void navigateToCheckboxesPage() {
        page.navigate("https://the-internet.herokuapp.com/checkboxes");
        Allure.step("Страница загружена успешно");
    }

    @Step("Проверка начального состояния чекбоксов")
    private void verifyInitialState() {
        // Получаем все чекбоксы на странице
        Locator checkboxes = page.locator("input[type='checkbox']");

        Allure.step("Количество чекбоксов: " + checkboxes.count());

        // Проверяем, что чекбоксы есть на странице
        assertThat(checkboxes).hasCount(2);

        // Первый чекбокс должен быть не отмечен
        assertFalse(checkboxes.nth(0).isChecked(), "Первый чекбокс должен быть не отмечен");

        // Второй чекбокс должен быть отмечен
        assertTrue(checkboxes.nth(1).isChecked(), "Второй чекбокс должен быть отмечен");

        captureScreenshot("initial-state.png");
    }

    @Step("Изменение состояния чекбоксов")
    private void toggleCheckboxes() {
        Locator checkboxes = page.locator("input[type='checkbox']");

        // Кликаем на первый чекбокс (поставить выделение, т.к. он не отмечен)
        checkboxes.nth(0).click();
        Allure.step("Кликнули на первый чекбокс для его отметки");

        // Кликаем на второй чекбокс (снять выделение, т.к. он отмечен)
        checkboxes.nth(1).click();
        Allure.step("Кликнули на второй чекбокс для снятия отметки");

        captureScreenshot("after-toggle.png");
    }

    @Step("Проверка состояния чекбоксов после переключения")
    private void verifyToggledState() {
        Locator checkboxes = page.locator("input[type='checkbox']");

        // После клика первый чекбокс должен быть отмечен
        assertTrue(checkboxes.nth(0).isChecked(), "Первый чекбокс должен быть отмечен после клика");

        // После клика второй чекбокс должен быть не отмечен
        assertFalse(checkboxes.nth(1).isChecked(), "Второй чекбокс должен быть не отмечен после клика");

        Allure.step("Состояния чекбоксов успешно переключены");
        captureScreenshot("final-state.png");
    }

    @Step("Захват скриншота: {screenshotName}")
    private void captureScreenshot(String screenshotName) {
        try {
            byte[] screenshot = page.screenshot();

            Path screenshotPath = Paths.get(timestamp + "/screenshots/" + screenshotName);
            Files.createDirectories(screenshotPath.getParent());
            Files.write(screenshotPath, screenshot);

            // Добавляем скриншот в Allure отчет
            Allure.addAttachment(
                    screenshotName,
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    ".png"
            );
        } catch (IOException e) {
            Allure.step("Ошибка при захвате скриншота: " + e.getMessage());
        }
    }

    @AfterEach
    @Step("Закрытие ресурсов")
    void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}