package auto;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

public class FileUploadTest {
    private Playwright playwright;
    private APIRequestContext request;
    private Path tempFile;
    private byte[] testFileBytes;

    @BeforeEach
    void setup() throws IOException {
        playwright = Playwright.create();
        request = playwright.request().newContext();

        testFileBytes = generateTestPNG();
        tempFile = Files.createTempFile("test-image-", ".png");
        Files.write(tempFile, testFileBytes);
    }

    @AfterEach
    void cleanup() {
        try {
            if (tempFile != null && Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
        } catch (IOException e) {
            System.err.println("Ошибка при удалении временного файла: " + e.getMessage());
        }

        if (request != null) {
            request.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * Генерирует тестовый PNG-файл в памяти
     */
    private byte[] generateTestPNG() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();

        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.setColor(Color.WHITE);
        graphics.fillOval(25, 25, 50, 50);
        graphics.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    /**
     * Проверяет PNG-сигнатуру файла
     */
    private void validatePNGSignature(byte[] content) {
        assertTrue(content.length >= 8, "Файл слишком маленький для PNG");

        assertEquals(0x89, content[0] & 0xFF, "Неверный первый байт PNG сигнатуры");
        assertEquals(0x50, content[1] & 0xFF, "Неверный второй байт PNG сигнатуры (P)");
        assertEquals(0x4E, content[2] & 0xFF, "Неверный третий байт PNG сигнатуры (N)");
        assertEquals(0x47, content[3] & 0xFF, "Неверный четвертый байт PNG сигнатуры (G)");
        assertEquals(0x0D, content[4] & 0xFF, "Неверный пятый байт PNG сигнатуры (CR)");
        assertEquals(0x0A, content[5] & 0xFF, "Неверный шестой байт PNG сигнатуры (LF)");
        assertEquals(0x1A, content[6] & 0xFF, "Неверный седьмой байт PNG сигнатуры");
        assertEquals(0x0A, content[7] & 0xFF, "Неверный восьмой байт PNG сигнатуры (LF)");
    }

    @Test
    @DisplayName("Полный цикл загрузки и скачивания файла с проверкой целостности")
    void testFileUploadAndDownload() {
        // Шаг 1: Загрузка файла через multipart/form-data
        APIResponse uploadResponse = null;
        APIResponse downloadResponse = null;

        try {
            uploadResponse = request.post(
                    "https://httpbin.org/post",
                    RequestOptions.create().setMultipart(
                            FormData.create().set("file", tempFile)
                    )
            );

            assertEquals(200, uploadResponse.status(), "Ошибка при загрузке файла");

            // Шаг 2: Проверка получения файла сервером (наличие base64-данных в ответе)
            String responseBody = uploadResponse.text();
            assertTrue(responseBody.contains("data:image/png;base64"),
                    "Ответ не содержит base64-данные файла");

            // Шаг 3: Извлечение и декодирование загруженного файла
            String fileData = extractBase64Data(responseBody);
            assertNotNull(fileData, "Не удалось извлечь base64-данные из ответа");

            byte[] receivedBytes = Base64.getDecoder().decode(fileData);

            // Шаг 4: Проверка точного соответствия содержимого
            assertArrayEquals(testFileBytes, receivedBytes,
                    "Содержимое загруженного файла не совпадает с исходным");

            System.out.println("✓ Целостность данных подтверждена: " +
                    testFileBytes.length + " байт");

            // Шаг 5: Скачивание эталонного PNG-файла
            downloadResponse = request.get("https://httpbin.org/image/png");

            assertEquals(200, downloadResponse.status(),
                    "Ошибка при скачивании эталонного файла");

            // Шаг 6: Проверка корректности MIME-типа
            String contentType = downloadResponse.headers().get("content-type");
            assertNotNull(contentType, "MIME-тип отсутствует в заголовках");
            assertTrue(contentType.toLowerCase().contains("image/png"),
                    "Неверный MIME-тип: " + contentType);

            System.out.println("✓ MIME-тип корректен: " + contentType);

            // Шаг 7: Проверка валидности формата через сигнатуру файла
            byte[] downloadedContent = downloadResponse.body();
            validatePNGSignature(downloadedContent);

            System.out.println("✓ PNG-сигнатура валидна");
            System.out.println("✓ Размер скачанного файла: " + downloadedContent.length + " байт");

        } catch (Exception e) {
            fail("Ошибка при выполнении теста: " + e.getMessage(), e);
        } finally {
            if (uploadResponse != null) {
                uploadResponse.dispose();
            }
            if (downloadResponse != null) {
                downloadResponse.dispose();
            }
        }
    }

    /**
     * Извлекает base64-данные из JSON-ответа httpbin.org
     */
    private String extractBase64Data(String responseBody) {
        try {
            int startIndex = responseBody.indexOf("\"file\": \"") + 9;
            if (startIndex < 9) {
                return null;
            }

            int endIndex = responseBody.indexOf("\"", startIndex);
            if (endIndex < 0) {
                return null;
            }

            String dataUri = responseBody.substring(startIndex, endIndex);

            if (dataUri.contains("base64,")) {
                return dataUri.split("base64,")[1];
            }

            return null;
        } catch (Exception e) {
            System.err.println("Ошибка при извлечении base64-данных: " + e.getMessage());
            return null;
        }
    }

}