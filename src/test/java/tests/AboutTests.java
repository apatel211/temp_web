package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AboutPage;
import utils.JsonUtils;
import utils.LoggerUtil;
import utils.ValidationUtils;

import java.util.List;

import static org.testng.Assert.assertTrue;

// Additional test that validates the About page heading is present
public class AboutTests extends BaseTest {

    private AboutPage about;

    private JsonNode pageJson;

    private ValidationUtils uiUtils;

    @BeforeMethod(alwaysRun = true)
    public void initPageObjects() {
        about = new AboutPage(driver, timeout);
        uiUtils = new ValidationUtils(driver);
        pageJson = JsonUtils.readPageJson("src/test/resources/expected_texts.json", "why_multibank");

        Assert.assertTrue(about.isLoaded(), "About page not loaded");
    }

    @Test(description = "ABOUT-001: Validate main components of About us page")
    public void about001_validateComponentsVisibility() {

        String heading = about.getHeadingText();
        assertTrue(heading.length() > 0, "Heading should not be empty");
        assertTrue(about.navigateWhyMultiLink(), "Why multibank page not loaded");
        assertTrue(about.enumerateVisibleComponents(), "No visible components found on the page");

        LoggerUtil.info(this.getClass(), "About As page main component been captured ");
    }

    @Test(description = "ABOUT-002: Validate UI texts from expected JSON")
    public void about002_validateUIText() {

        assertTrue(about.navigateWhyMultiLink(), "Why multibank page not loaded");

        List<String> diffs = uiUtils.validatePageFromJson(pageJson);

        if (!diffs.isEmpty()) {
            Assert.fail("Text mismatches found:\n" + String.join("\n", diffs));
        }

        LoggerUtil.info(this.getClass(), "About As page text been validated ");
    }
}
