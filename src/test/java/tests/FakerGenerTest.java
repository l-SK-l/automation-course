package tests;

import base.BaseTest;
import com.github.javafaker.Faker;
import com.microsoft.playwright.Route;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class FakerGenerTest extends BaseTest {

    @Test
    public void testDynamicContentWithFaker() {
        // Генерация случайного имени пользователя с помощью Faker
        Faker faker = new Faker();
        String randomUsername = faker.name().fullName();

        System.out.println("Сгенерированное имя: " + randomUsername);

        // Мокирование API - перехват запроса и замена ответа
        page.route("**/dynamic_content", route -> {
            String mockedHtml = """
                    <!DOCTYPE html>
                    <html>
                    <head><title>Dynamic Content</title></head>
                    <body>
                        <div class="large-10 columns">
                            <div class="row">
                                <div class="large-2 columns"><img src="/img/avatars/Original-Facebook-Geek-Profile-Avatar-1.jpg"></div>
                                <div class="large-10 columns">%s</div>
                            </div>
                        </div>
                    </body>
                    </html>
                    """.formatted(randomUsername);

            route.fulfill(new Route.FulfillOptions()
                    .setStatus(200)
                    .setContentType("text/html")
                    .setBody(mockedHtml)
            );
        });

        // Запуск теста - переход на страницу
        page.navigate("https://the-internet.herokuapp.com/dynamic_content");

        // Проверка, что сгенерированное имя отображается на странице
        String pageContent = page.content();
        assertTrue(pageContent.contains(randomUsername),
                "Страница должна содержать сгенерированное имя: " + randomUsername);
    }
}