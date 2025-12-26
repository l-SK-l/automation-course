package auto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TodoApiTest {
    Playwright playwright;
    APIRequestContext requestContext;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        playwright = Playwright.create();
        requestContext = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("https://jsonplaceholder.typicode.com")
        );
    }

    @Test
    void testTodoApi() throws Exception {
        // 1. Выполнение GET-запроса напрямую через API
        APIResponse response = requestContext.get("/todos/1");

        // 2. Проверка статуса
        assertEquals(200, response.status());

        // 3. Парсинг JSON
        String responseBody = response.text();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        // 4. Проверка структуры
        assertNotNull(jsonNode.get("userId"), "userId should be present");
        assertNotNull(jsonNode.get("id"), "id should be present");
        assertNotNull(jsonNode.get("title"), "title should be present");
        assertNotNull(jsonNode.get("completed"), "completed should be present");

        // Дополнительные проверки значений
        assertEquals(1, jsonNode.get("userId").asInt());
        assertEquals(1, jsonNode.get("id").asInt());
        assertFalse(jsonNode.get("title").asText().isEmpty());
        assertTrue(jsonNode.has("completed"));
    }

    @AfterEach
    void tearDown() {
        requestContext.dispose();
        playwright.close();
    }
}