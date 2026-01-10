package base;

import java.util.ArrayList;
import java.util.List;

public class TestResultCollector {
    private static final List<TestResult> results = new ArrayList<>();

    public static void addResult(TestResult result) {
        results.add(result);
    }

    public static List<TestResult> getResults() {
        return new ArrayList<>(results);
    }

    public static void clear() {
        results.clear();
    }
}
