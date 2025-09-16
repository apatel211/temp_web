package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import utils.JsonUtils;
import utils.LoggerUtil;
import utils.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertTrue;

public class HomeTests extends BaseTest {
    private HomePage home;

    private JsonNode navJson;
    private JsonNode footerJson;

    @BeforeMethod(alwaysRun = true)
    public void initPageObjects() {
        home = new HomePage(driver, timeout);
        navJson = JsonUtils.readPageJson("src/test/resources/testdata.json", "navItems");
        footerJson = JsonUtils.readPageJson("src/test/resources/testdata.json", "footerBanners");

        Assert.assertTrue(home.isLoaded(), "Home page did not load properly");
    }

    @Test(description = "NAV-001: Verify all nav items from JSON are present")
    public void nav001_validateNavItemsPresence() {
        List<String> actualTexts = home.getNavTexts();
        System.out.println("=== Actual Nav Texts from UI ===");
        for (String txt : actualTexts) {
            System.out.println(" - " + txt);
        }

        for (JsonNode node : navJson) {
            String expectedText = node.get("text").asText().trim();
            assertTrue(actualTexts.contains(expectedText),
                    "Expected nav item not found: " + expectedText);
        }
        LoggerUtil.info(this.getClass(), "Navigation items are present ");
    }

    @Test(description = "NAV-002: Verify each nav item has correct href from JSON")
    public void nav002_validateNavItemsHrefs() {
        List<String> actualTexts = home.getNavTexts();
        List<String> actualHrefs = home.getNavHrefs();

        System.out.println("=== Actual Nav Items from UI ===");
        for (int i = 0; i < actualTexts.size(); i++) {
            String txt = actualTexts.get(i);
            String href = (i < actualHrefs.size()) ? actualHrefs.get(i) : "N/A";
            System.out.println(" - " + txt + " --> " + href);
        }

        for (int i = 0; i < navJson.size(); i++) {
            String expectedText = navJson.get(i).get("text").asText();
            String expectedHref = navJson.get(i).get("hrefContains").asText();

            int index = actualTexts.indexOf(expectedText);
            assertTrue(index >= 0, "Text not found in UI: " + expectedText);

            String actualHref = actualHrefs.get(index);
            assertTrue(actualHref.contains(expectedHref),
                    "Href mismatch for [" + expectedText + "] Expected: " + expectedHref + " but got: " + actualHref);
        }

        LoggerUtil.info(this.getClass(), "Navigation hrfe been verified ");
    }

    @Test(description = "FOOTER-001: Verify marketing banners in footer")
    public void footer001_validateFooterBanners() {
        List<String> actual = home.getAllBannerContents();
        System.out.println("=== Actual Footer/Marketing Banners ===");
        actual.forEach(System.out::println);

        Assert.assertFalse(actual.isEmpty(), "No footer banners found!");

        List<String> expected = new ArrayList<>();
        for (JsonNode node : footerJson) {
            expected.add(node.asText().trim());
        }

        for (String exp : expected) {
            String normExp = ValidationUtils.normalize(exp).toLowerCase();

            boolean found = actual.stream()
                    .map(ValidationUtils::normalize)
                    .map(String::toLowerCase)
                    .anyMatch(act -> act.contains(normExp));

            Assert.assertTrue(found, "Expected banner text not found: " + exp);
        }

        LoggerUtil.info(this.getClass(), "Banners are present and have been verified");
    }

}
