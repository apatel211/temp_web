package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtil;
import utils.ValidationUtils;
import utils.WaitUtils;

import java.util.List;

public class AboutPage extends BasePage {
    private final WaitUtils waitUtils;

    private final By aboutUsText = By.xpath("//*[contains(text(),'About Us')]");

    private final By aboutUsLink = By.cssSelector("a[href*='why-multibank']");

    private final By componentLocator = By.cssSelector("h1, h2, h3, p, a, button");

    public AboutPage(WebDriver driver, int timeout) {
        super(driver, timeout);
        this.waitUtils = new WaitUtils(driver); // Initialize wait utility
        LoggerUtil.info(this.getClass(), "AboutPage initialized");
    }

    @Override
    public boolean isLoaded() {
        LoggerUtil.info(this.getClass(), "Waiting for About Us heading to be visible");
        return waitUtils.isElementVisible(aboutUsText, timeout);
    }

    public String getHeadingText() {
        LoggerUtil.info(this.getClass(), "Get About Us heading");
        waitUtils.waitForVisible(aboutUsText, timeout);
        return driver.findElement(aboutUsText).getText().trim();
    }

    public boolean navigateWhyMultiLink() {
        try {

            waitUtils.waitForClickable(aboutUsText, timeout);
            driver.findElement(aboutUsText).click();
            LoggerUtil.info(this.getClass(), "click on about AS");

            waitUtils.waitForClickable(aboutUsLink, timeout);
            driver.findElement(aboutUsLink).click();

            return waitUtils.isElementVisible(aboutUsText, timeout);
        } catch (Exception e) {
            System.out.println("Failed to navigate to Why MultiLink: " + e.getMessage());
            return false;
        }
    }

    public boolean enumerateVisibleComponents() {
        LoggerUtil.info(this.getClass(), "Enumerating visible components...");

        List<WebElement> elements = driver.findElements(componentLocator);

        boolean anyVisible = false;

        for (WebElement e : elements) {
            try {
                if (e.isDisplayed()) {
                    String msg = "Visible: " + e.getTagName() + " -> " + ValidationUtils.normalize(e.getText());
                    LoggerUtil.debug(this.getClass(), msg);  // detailed
                    anyVisible = true;
                }
            } catch (Exception ex) {
                LoggerUtil.error(this.getClass(), "Error checking element: " + e, ex);
            }
        }

        if (!anyVisible) {
            LoggerUtil.warn(this.getClass(), "No visible components found!");
        }

        return anyVisible; // true if at least one component is visible
    }

}
