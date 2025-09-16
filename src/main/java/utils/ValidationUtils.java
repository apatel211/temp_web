package utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class ValidationUtils {

    private final WebDriver driver;

    public ValidationUtils(WebDriver driver) {
        this.driver = driver;
    }

    public static String normalize(String text) {
        if (text == null) return "";
        return text
                .replace('\u00A0', ' ')     // replace non-breaking space with normal space
                .replaceAll("\\s+", " ")    // collapse multiple whitespace chars
                .replaceAll("[\\u200E\\u200F\\u202A-\\u202E]", "")
                .trim();
    }

    public static void scrollToBottom(WebDriver driver) {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public static void scrollIntoView(WebDriver driver, WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }



    public static void validateLink(WebDriver driver, By selector, JsonNode node, int timeout) {
        WaitUtils waitUtils = new WaitUtils(driver);

        // Wait and click link
        waitUtils.waitForClickable(selector, timeout);
        WebElement link = driver.findElement(selector);
        String mainWindow = driver.getWindowHandle();
        link.click();

        // Wait for new tab
        waitUtils.waitForNewTabAndLoad(mainWindow, timeout);

        // Expected values from JSON
        String expectedUrl = node.get("expected_url").asText();
        String expectedTitle = node.get("expected_title").asText();

        // Actual values
        String actualUrl = driver.getCurrentUrl();
        String actualTitle = driver.getTitle();

        // Normalize
        String normExpectedTitle = normalize(expectedTitle);
        String normActualTitle = normalize(actualTitle);

        if (!actualUrl.startsWith(expectedUrl)) {
            throw new AssertionError(
                    "URL validation failed for '" + selector + "'\n" +
                            "Expected URL (startsWith): " + expectedUrl + "\n" +
                            "Actual URL: " + actualUrl
            );
        }

        if (!normActualTitle.contains(normExpectedTitle)) {
            throw new AssertionError(
                    "Title validation failed for '" + selector + "'\n" +
                            "Expected Title (contains): " + normExpectedTitle + "\n" +
                            "Actual Title: " + normActualTitle
            );
        }

        driver.close();
        driver.switchTo().window(mainWindow);
    }

    public boolean validatePairSymbol(String symbol) {
        // e.g., BTC/USDT, ADA-USDT, AAVE/USDT
        return symbol.matches("^[A-Z0-9]{2,10}[-/][A-Z0-9]{2,10}$");
    }

    public List<String> validatePageFromJson(JsonNode pageJson) {
        List<String> differences = new ArrayList<>();

        pageJson.fieldNames().forEachRemaining(key -> {
            JsonNode node = pageJson.get(key);
            String selector = node.get("selector").asText();
            String expectedText = normalize(node.get("expected").asText());

            List<WebElement> elements = driver.findElements(By.cssSelector(selector));
            boolean matchFound = false;
            String actualText = "";

            for (WebElement element : elements) {
                if (element.isDisplayed()) {
                    actualText = normalize(element.getText());
                    if (actualText.equals(expectedText)) {
                        matchFound = true;
                        break;
                    }
                }
            }

            if (!matchFound) {
                differences.add("Mismatch for '" + key + "': expected '" + expectedText + "', but found '" + actualText + "'");
            }
        });

        return differences;
    }

}

