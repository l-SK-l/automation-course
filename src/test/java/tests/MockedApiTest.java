package tests;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class MockedApiTest {
    // Мок-сервис для имитации API
    private static ApiService apiService;

    @BeforeAll
    static void setUpClass() {
        // Создаем мок ApiService
        apiService = mock(ApiService.class);

        // Настраиваем поведение мока - возвращаем тестовые данные
        when(apiService.fetchUserData()).thenReturn("{\"name\": \"Test User\", \"email\": \"test@example.com\"}");
    }

    @Test
    void testUserProfileWithMockedApi() {
        // Используем мок вместо реального API
        String userData = apiService.fetchUserData();

        // Проверяем, что мок вернул тестовые данные, а не реальные
        assertNotNull(userData);
        assertTrue(userData.contains("Test User"));
        assertTrue(userData.contains("test@example.com"));

        // Убеждаемся, что мок был вызван ровно один раз
        verify(apiService, times(1)).fetchUserData();
    }


    // Тестовый класс-заглушка для API сервиса
    static class ApiService {
        public String fetchUserData() {
            // Имитация медленного API-запроса
            try {
                Thread.sleep(3000); // 3 секунды задержки
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "{\"name\": \"Real User\", \"email\": \"real@example.com\"}";
        }
    }

}