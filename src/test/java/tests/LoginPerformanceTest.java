package tests;

import base.BaseTest;
import com.microsoft.playwright.Tracing;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginPerformanceTest extends BaseTest {

    @Test
    void loginPerformanceTest() {
        // Start tracing to capture performance metrics
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true));

        long start = System.currentTimeMillis();

        // Perform login flow
        page.navigate("https://the-internet.herokuapp.com/login");
        page.fill("input[name='username']", "tomsmith");
        page.fill("input[name='password']", "SuperSecretPassword!");
        page.click("button[type='submit']");

        // Wait for navigation to secure area
        page.waitForURL("**/secure");

        // Verify successful login
        assertTrue(page.url().contains("/secure"),
                "Should be redirected to secure area after successful login");

        long duration = System.currentTimeMillis() - start;

        // Save trace file for slow executions
        if (duration > 3000) {
            context.tracing().stop(new Tracing.StopOptions()
                    .setPath(Paths.get("slow-login-trace.zip")));
        } else {
            context.tracing().stop();
        }

        // Assert performance requirement
        assertTrue(duration < 3000,
                "Login took " + duration + "ms (exceeds 3000ms limit)");
    }
}