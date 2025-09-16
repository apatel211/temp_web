package utils;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Reusable Selenium wait utilities with FluentWait.
 */
public class WaitUtils {

    private final WebDriver driver;

    public WaitUtils(WebDriver driver) {
        this.driver = driver;
    }

    public <T> T fluentWait(Function<WebDriver, T> condition, int timeoutSeconds, int pollingMillis) {
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .pollingEvery(Duration.ofMillis(pollingMillis))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(TimeoutException.class);
        return wait.until(condition);
    }

    public boolean isElementVisible(By locator, int timeoutSeconds) {
        try {
            fluentWait(ExpectedConditions.visibilityOfElementLocated(locator), timeoutSeconds, 500);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void waitForClickable(By locator, int timeoutSeconds) {
        fluentWait(ExpectedConditions.elementToBeClickable(locator), timeoutSeconds, 500);
    }

    public void waitForVisible(By locator, int timeoutSeconds) {
        fluentWait(ExpectedConditions.visibilityOfElementLocated(locator), timeoutSeconds, 500);
    }

    public void waitForNewTabAndLoad(String mainWindow, int timeoutSeconds) {
        // Wait for second window
        fluentWait(d -> d.getWindowHandles().size() > 1, timeoutSeconds, 500);

        // Switch to the new one
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        // Wait for new tab to load
        fluentWait(d -> !d.getCurrentUrl().equals("about:blank") && !d.getTitle().isEmpty(),
                timeoutSeconds, 500);
    }
}
