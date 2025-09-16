package utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScreenshotUtils {

    // Capture screenshot using the WebDriver provided by the test/base page
    public static String takeScreenshot(WebDriver driver, String testName) {
        String path = System.getProperty("user.dir") + "/screenshots/"
                + testName + "_" + System.currentTimeMillis() + ".png";

        try {
            Files.createDirectories(Paths.get(System.getProperty("user.dir") + "/screenshots"));
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }
}
