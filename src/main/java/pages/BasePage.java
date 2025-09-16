package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.LoggerUtil;

import java.time.Duration;

public abstract class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected int timeout;

    public BasePage(WebDriver driver, int timeout) {
        this.driver = driver;
        this.timeout = timeout;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        LoggerUtil.info(this.getClass(), "Initialized page with timeout: " + timeout + " seconds");
    }

    public abstract boolean isLoaded();
}
