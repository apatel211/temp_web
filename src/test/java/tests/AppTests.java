package tests;

import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.AppPage;
import utils.JsonUtils;
import utils.LoggerUtil;

import static org.testng.Assert.assertFalse;

public class AppTests extends BaseTest {

    private AppPage app;
    private JsonNode appLinksJson;

    @BeforeMethod(alwaysRun = true)
    public void initPageObjects() {
        app = new AppPage(driver, timeout);
        appLinksJson = JsonUtils.readPageJson("src/test/resources/testdata.json", "app_download_links");

        Assert.assertTrue(app.isLoaded(), "App page not loaded");
    }

    @Test(description = "APP-001: Validate Apple App Store link")
    public void app001_validateAppleAppStoreLink() {

        String dashboardScroll = app.getDashboardTextAndScroll();
        assertFalse(dashboardScroll.isEmpty(), "Dashboard page is not scrolled");
        app.validateAppleAppStore(appLinksJson.get("apple_app_store"));
        LoggerUtil.info(this.getClass(), "Apple app store link been verified ");
    }

    @Test(description = "APP-002: Validate Google Play Store link")
    public void app002_validateGooglePlayLink() {

        String dashboardScroll = app.getDashboardTextAndScroll();
        assertFalse(dashboardScroll.isEmpty(), "Dashboard page is not scrolled");
        app.validateGooglePlayStore(appLinksJson.get("google_play_store"));
        LoggerUtil.info(this.getClass(), "Google play store link been verified ");
    }

}


