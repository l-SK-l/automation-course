package base;

public class TestResult {
    public String name;
    public String status;
    public long duration;
    public String error;
    public String screenshot;

    public TestResult(String name) {
        this.name = name;
        this.status = "Passed";
        this.duration = 0;
        this.error = null;
        this.screenshot = null;
    }
}
