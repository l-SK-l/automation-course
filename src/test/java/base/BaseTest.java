package base;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(TestWatcherExtension.class)
public class BaseTest {
    Playwright playwright;
    Browser browser;
    public BrowserContext context;
    public Page page;
    private long testStartTime;
    protected TestResult currentTestResult;

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!TestResultCollector.getResults().isEmpty()) {
                String reportPath = "target/test-report.html";
                HtmlReportGenerator.generateReport(TestResultCollector.getResults(), reportPath);
                System.out.println("\n✓ HTML Report generated at: " + reportPath);
            }
        }));
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
        testStartTime = System.currentTimeMillis();
        currentTestResult = new TestResult(testInfo.getDisplayName());
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void tearDown(TestInfo testInfo) {
        long duration = System.currentTimeMillis() - testStartTime;
        currentTestResult.duration = duration;

        TestResultCollector.addResult(currentTestResult);
        playwright.close();
    }

    protected void captureScreenshot(String testName) {
        try {
            String screenshotsDir = "target/screenshots";
            Files.createDirectories(Paths.get(screenshotsDir));

            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
            String timestamp = now.format(formatter);
            String screenshotPath = screenshotsDir + "/" + testName + "_" + timestamp + ".png";

            page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(screenshotPath)));
            currentTestResult.screenshot = screenshotPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}