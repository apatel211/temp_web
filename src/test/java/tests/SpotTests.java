package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.SpotPage;
import utils.JsonUtils;
import utils.LoggerUtil;
import utils.RetryAnalyzer;
import utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

public class SpotTests extends BaseTest {

    private SpotPage spot;
    private JsonNode headersJson;
    private ValidationUtils uiUtils;

    @BeforeMethod(alwaysRun = true)
    public void initPageObjects() {
        spot = new SpotPage(driver, timeout);
        uiUtils = new ValidationUtils(driver);

        headersJson = JsonUtils.readPageJson("src/test/resources/testdata.json", "spot_headers");
        Assert.assertTrue(spot.isLoaded(), "Spot page not loaded");
        spot.goToSpot();
    }

    @Test(description = "SPOT-001: Validate headers & rows for each category", retryAnalyzer = RetryAnalyzer.class)
    public void spot001_validateCategoriesStructure() {

        List<String> expectedHeaders = new ArrayList<>();
        headersJson.forEach(node -> expectedHeaders.add(node.asText().trim()));

        List<WebElement> tabs = spot.getCategoryTabs();
        Assert.assertTrue(tabs.size() > 0, "No Spot categories found");

        for (WebElement tab : tabs) {
            spot.clickCategory(tab);

            // Validate headers
            List<String> headers = spot.getHeaders();
            for (String h : expectedHeaders) {
                Assert.assertTrue(headers.contains(h), "Missing header: " + h);
            }

            // Validate rows visible
            List<WebElement> rows = spot.getRows();
            Assert.assertTrue(rows.size() > 0,
                    "No trading pairs found in category: " + tab.getText());
        }
        LoggerUtil.info(this.getClass(), "Able to see each category list on spot tab");
    }

    @Test(description = "SPOT-002: Validate trading data is present")
    public void spot002_validateTradingDataPresent() {

        List<WebElement> rows = spot.getRowsWithData();
        Assert.assertTrue(rows.size() > 0, "No trading pairs found in Spot section");

        int limit = Math.min(2, rows.size());  //change the row limit
        for (int i = 0; i < limit; i++) {
            WebElement row = rows.get(i);
            LoggerUtil.info(this.getClass(), "Validating row index: " + i);

            // Pair
            String pair = spot.getPairSymbol(row);
            Assert.assertFalse(pair.isEmpty(), "Pair column is empty");
            Assert.assertTrue(uiUtils.validatePairSymbol(pair), "Invalid pair symbol format: " + pair);

            // Price
            String priceText = spot.getPrice(row).replaceAll("[^0-9.,]", "");
            Assert.assertFalse(priceText.isEmpty(), "Price column is empty");
            try {
                double price = Double.parseDouble(priceText.replace(",", ""));
                Assert.assertTrue(price > 0, "Invalid price value: " + price);
            } catch (NumberFormatException e) {
                Assert.fail("Price is not numeric: " + priceText);
            }

            // Change 24h
            String change = spot.getChange24h(row);
            Assert.assertFalse(change.isEmpty(), "24h Change column is empty");

            // High
            String highText = spot.getHigh(row).replaceAll("[^0-9.,]", "");
            Assert.assertFalse(highText.isEmpty(), "High column is empty");

            // Low
            String lowText = spot.getLow(row).replaceAll("[^0-9.,]", "");
            Assert.assertFalse(lowText.isEmpty(), "Low column is empty");

            // Volume (text or graph)
            String volume = spot.getVolume(row);
            if (volume.isEmpty()) {
                Assert.assertTrue(spot.hasGraphInVolume(row), "Volume graph not rendered for: " + pair);
            }

            LoggerUtil.info(this.getClass(),
                    String.format("Row %d validated ✓ -> Pair: %s | Price: %s | Change: %s | High: %s | Low: %s | Volume: %s",
                            i + 1, pair, priceText, change, highText, lowText,
                            volume.isEmpty() ? "[graph]" : volume));
        }

        LoggerUtil.info(this.getClass(), "Able to see pair and value on spot tab ");

    }

}

