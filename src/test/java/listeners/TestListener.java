package listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import utils.DriverFactory;
import utils.ReportManager;
import utils.ScreenshotUtils;

public class TestListener implements ITestListener {

    private static final ExtentReports extent = ReportManager.getInstance();
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    @Override
    public void onTestStart(ITestResult result) {
        String browser = result.getTestContext().getCurrentXmlTest().getParameter("browser");
        if (browser == null || browser.isBlank()) {
            browser = System.getProperty("browser", "chrome"); // fallback to system property
        }

        String methodName = result.getMethod().getMethodName();


        ExtentTest testInstance = extent.createTest(methodName + " [" + browser.toUpperCase() + "]");
        test.set(testInstance);

        System.out.println(">>> Starting test: " + methodName + " on browser: " + browser.toUpperCase());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (test.get() != null) {
            test.get().log(Status.PASS, "Test Passed ✅ on " + getBrowser(result));
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (test.get() != null) {
            test.get().log(Status.FAIL, result.getThrowable());
            test.get().log(Status.FAIL, "❌ Failed on " + getBrowser(result));
            String screenshotPath = ScreenshotUtils.takeScreenshot(
                    DriverFactory.getDriver(),
                    result.getMethod().getMethodName()
            );
            try {
                test.get().addScreenCaptureFromPath(screenshotPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (test.get() != null) {
            test.get().log(Status.SKIP, "Test Skipped ⚠️ on " + getBrowser(result));
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    private String getBrowser(ITestResult result) {
        String browser = result.getTestContext().getCurrentXmlTest().getParameter("browser");
        if (browser == null || browser.isBlank()) {
            browser = System.getProperty("browser", "chrome");
        }
        return browser.toUpperCase();
    }
}

