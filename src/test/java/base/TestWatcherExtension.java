package base;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.Optional;

public class TestWatcherExtension implements TestWatcher {

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        try {
            BaseTest testInstance = (BaseTest) context.getTestInstance().orElse(null);
            if (testInstance != null) {
                testInstance.currentTestResult.status = "Passed";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        try {
            BaseTest testInstance = (BaseTest) context.getTestInstance().orElse(null);
            if (testInstance != null) {
                testInstance.currentTestResult.status = "Aborted";
                testInstance.currentTestResult.error = cause.getMessage();
                testInstance.captureScreenshot(context.getDisplayName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        try {
            BaseTest testInstance = (BaseTest) context.getTestInstance().orElse(null);
            if (testInstance != null) {
                testInstance.currentTestResult.status = "Failed";
                testInstance.currentTestResult.error = cause.getMessage();
                testInstance.captureScreenshot(context.getDisplayName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}