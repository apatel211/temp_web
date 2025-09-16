package tests;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.ConfigReader;
import utils.DriverFactory;
import utils.LoggerUtil;

public abstract class BaseTest {

    protected WebDriver driver;
    protected int timeout;

    @BeforeMethod
    @Parameters("browser")
    public void setup(@Optional("chrome") String browser) {
        driver = DriverFactory.getDriver(browser);
        driver.manage().window().maximize();
        driver.get(ConfigReader.get("baseUrl", "https://trade.multibank.io/"));

        timeout = Integer.parseInt(ConfigReader.get("timeout", "15"));
        LoggerUtil.info(this.getClass(),
                "Starting test in browser: " + browser + " | Base URL: " + ConfigReader.get("baseUrl", ""));

        if (Boolean.parseBoolean(ConfigReader.get("useSessionInjection", "false"))) {
            injectSession();
        }
    }

    private void injectSession() {
        LoggerUtil.info(this.getClass(), "Injecting session cookie...");

        // Example: inject session cookie
        Cookie sessionCookie = new Cookie("sessionId", "abcdef123456789");
        driver.manage().addCookie(sessionCookie);

        // Refresh to apply cookie session
        driver.navigate().refresh();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
    }
}

