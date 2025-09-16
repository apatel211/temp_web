package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.LoggerUtil;
import utils.ValidationUtils;
import utils.WaitUtils;

import java.util.List;
import java.util.stream.Collectors;

public class SpotPage extends BasePage {

    // Locators
    private final By spotTab = By.xpath("//span[contains(text(),'Spot')]");
    private final By categoryTabs = By.xpath("//div[contains(@class,'style_list')]/button");
    private final By tableHeaders = By.cssSelector("table thead th");
    private final By tableRows = By.cssSelector("table tbody tr");
    private final By pairCell = By.cssSelector("td[id$='base-td']");
    private final By priceCell = By.cssSelector("td[id$='price-td']");
    private final By changeCell = By.cssSelector("td[id$='change_in_price-td']");
    private final By highCell = By.cssSelector("td[id$='high_24hr-td']");
    private final By lowCell = By.cssSelector("td[id$='low_24hr-td']");
    private final By volumeCell = By.cssSelector("td[id$='base_volume-td']");
    private final WebDriver driver;
    private final WaitUtils waitUtils;
    private final int timeout;

    public SpotPage(WebDriver driver, int timeout) {
        super(driver, timeout);
        this.driver = driver;
        this.waitUtils = new WaitUtils(driver);
        this.timeout = timeout;
    }

    @Override
    public boolean isLoaded() {
        LoggerUtil.info(this.getClass(), "Checking Spot section is visible");
        return waitUtils.isElementVisible(spotTab, timeout);
    }

    public void goToSpot() {
        LoggerUtil.info(this.getClass(), "Navigating to Spot section...");
        waitUtils.waitForClickable(spotTab, timeout);
        driver.findElement(spotTab).click();
    }

    public List<WebElement> getCategoryTabs() {
        return driver.findElements(categoryTabs);
    }

    public void clickCategory(WebElement tab) {
        String label = tab.getText().trim();
        LoggerUtil.info(this.getClass(), "Switching to category: " + label);
        tab.click();
    }

    public List<String> getHeaders() {
        waitUtils.waitForVisible(tableHeaders, timeout); // ensure headers are visible
        List<String> headers = driver.findElements(tableHeaders).stream()
                .map(el -> ValidationUtils.normalize(el.getText()))
                .filter(txt -> !txt.isEmpty())
                .collect(Collectors.toList());
        LoggerUtil.info(this.getClass(), "Table headers: " + headers);
        return headers;
    }

    public List<WebElement> getRows() {
        waitUtils.isElementVisible(tableRows, timeout); // wait until at least 1 row is visible
        List<WebElement> rows = driver.findElements(tableRows);
        LoggerUtil.info(this.getClass(), "Total trading pairs found: " + rows.size());
        return rows;
    }

    public String getPairSymbol(WebElement row) {
        String pair = row.findElement(pairCell).getText();
        pair = ValidationUtils.normalize(pair);
        LoggerUtil.info(this.getClass(), "Pair symbol: " + pair);
        return pair;
    }

    public String getPrice(WebElement row) {
        String price = row.findElement(priceCell).getText();
        price = ValidationUtils.normalize(price);
        LoggerUtil.info(this.getClass(), "Price: " + price);
        return price;
    }

    public String getChange24h(WebElement row) {
        String change = row.findElement(changeCell).getText();
        change = ValidationUtils.normalize(change);
        LoggerUtil.info(this.getClass(), "24h Change: " + change);
        return change;
    }

    public String getHigh(WebElement row) {
        String high = row.findElement(highCell).getText();
        high = ValidationUtils.normalize(high);
        LoggerUtil.info(this.getClass(), "High: " + high);
        return high;
    }

    public String getLow(WebElement row) {
        String low = row.findElement(lowCell).getText();
        low = ValidationUtils.normalize(low);
        LoggerUtil.info(this.getClass(), "Low: " + low);
        return low;
    }

    public String getVolume(WebElement row) {
        String volume = row.findElement(volumeCell).getText();
        volume = ValidationUtils.normalize(volume);
        LoggerUtil.info(this.getClass(), "Volume: " + volume);
        return volume;
    }

    public List<WebElement> getRowsWithData() {
        waitUtils.fluentWait(d -> {
            List<WebElement> rows = d.findElements(tableRows);
            if (rows.isEmpty()) return false;

            String pair = rows.get(0).findElement(pairCell).getText().trim();
            return !pair.isEmpty();
        }, timeout, 500);
        return driver.findElements(tableRows);
    }

    public boolean hasGraphInVolume(WebElement row) {
        WebElement cell = row.findElement(volumeCell);

        // Scroll into view (important for Firefox/Grid headless)
        ValidationUtils.scrollIntoView(driver, cell);

        try {
            waitUtils.fluentWait(d ->
                            !cell.findElements(By.tagName("svg")).isEmpty() ||
                                    !cell.findElements(By.tagName("canvas")).isEmpty() ||
                                    !cell.findElements(By.tagName("path")).isEmpty(),
                    5,
                    500
            );
        } catch (Exception e) {
            LoggerUtil.warn(this.getClass(), "Graph not found in volume cell after timeout.");
            return false;
        }

        boolean hasGraph = !cell.findElements(By.tagName("svg")).isEmpty()
                || !cell.findElements(By.tagName("canvas")).isEmpty()
                || !cell.findElements(By.tagName("path")).isEmpty();

        LoggerUtil.info(this.getClass(), "Volume graph present: " + hasGraph);
        return hasGraph;
    }

}

