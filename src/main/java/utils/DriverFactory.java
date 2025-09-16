package utils;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    public static WebDriver getDriver(String browser) {
        if (tlDriver.get() == null) {
            browser = (browser == null || browser.isEmpty())
                    ? ConfigReader.get("browser", "chrome")
                    : browser.toLowerCase();

            String gridUrl = ConfigReader.get("gridUrl", "");

            try {
                if (!gridUrl.isEmpty()) {
                    // Grid mode
                    if (browser.equals("chrome")) {
                        WebDriverManager.chromedriver().setup();
                        tlDriver.set(new RemoteWebDriver(new URL(gridUrl), new ChromeOptions()));
                    } else if (browser.equals("firefox")) {
                        WebDriverManager.firefoxdriver().setup();
                        tlDriver.set(new RemoteWebDriver(new URL(gridUrl), new FirefoxOptions()));
                    } else {
                        throw new IllegalArgumentException("Unsupported browser: " + browser);
                    }
                } else {
                    // Local mode
                    if (browser.equals("chrome")) {
                        WebDriverManager.chromedriver().setup();
                        tlDriver.set(new ChromeDriver());
                    } else if (browser.equals("firefox")) {
                        WebDriverManager.firefoxdriver().setup();
                        tlDriver.set(new FirefoxDriver());
                    } else {
                        throw new IllegalArgumentException("Unsupported browser: " + browser);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to create driver: " + e.getMessage(), e);
            }
        }
        return tlDriver.get();
    }

    // Overloaded method for already-initialized driver
    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public static void quitDriver() {
        if (tlDriver.get() != null) {
            try {
                tlDriver.get().quit();
            } catch (Exception ignored) {
            }
            tlDriver.remove();
        }
    }
}

