package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.LoggerUtil;
import utils.ValidationUtils;
import utils.WaitUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class HomePage extends BasePage {

    private final By navLinks = By.cssSelector("header a[href]");
    private final WaitUtils waitUtils;
    private final By activeBanner = By.cssSelector("div.slick-slide.slick-current:not(.slick-cloned), div.slick-slide.slick-active:not(.slick-cloned)");

    private final By carousel = By.cssSelector("div.slick-slider.slick-initialized");
    private final By dotsItem = By.cssSelector("div.slick-slider.slick-initialized ul[class*='dots'] li");
    private final By images = By.cssSelector("img[alt]");

    public HomePage(WebDriver driver, int timeout) {
        super(driver, timeout);
        this.waitUtils = new WaitUtils(driver); // Initialize wait utility
        LoggerUtil.info(this.getClass(), "HomePage initialized");
    }

    @Override
    public boolean isLoaded() {
        LoggerUtil.info(this.getClass(), "Waiting for dashboard heading to be visible");
        return waitUtils.isElementVisible(navLinks, timeout);
    }

    public List<String> getNavTexts() {
        LoggerUtil.info(this.getClass(), "Fetching navigation link texts...");
        List<String> texts = driver.findElements(navLinks).stream()
                .map(el -> el.getText().trim())
                .filter(txt -> !txt.isEmpty())
                .collect(Collectors.toList());
        LoggerUtil.info(this.getClass(), "Navigation texts found: " + texts);
        return texts;
    }

    public List<String> getNavHrefs() {
        LoggerUtil.info(this.getClass(), "Fetching navigation link hrefs...");
        List<String> hrefs = driver.findElements(navLinks).stream()
                .map(el -> el.getAttribute("href"))
                .collect(Collectors.toList());
        LoggerUtil.info(this.getClass(), "Navigation hrefs found: " + hrefs);
        return hrefs;
    }

    public List<String> getAllBannerContents() {
        // 🔹 Ensure carousel is in view
        ValidationUtils.scrollToBottom(driver);
        WebElement carouselEl = wait.until(ExpectedConditions.presenceOfElementLocated(carousel));
        ValidationUtils.scrollIntoView(driver, carouselEl);

        // 🔹 Ensure first banner has content before starting
        wait.until(d -> {
            WebElement first = d.findElement(activeBanner);
            String text = first.getText().trim();
            boolean hasAlt = first.findElements(images).stream()
                    .anyMatch(img -> {
                        String alt = img.getAttribute("alt");
                        return alt != null && !alt.isBlank() && !"Banner image".equalsIgnoreCase(alt);
                    });
            return (!text.isEmpty() || hasAlt);
        });

        Set<String> collected = new LinkedHashSet<>();
        List<WebElement> dots = driver.findElements(dotsItem);
        LoggerUtil.info(this.getClass(), "Total dots found: " + dots.size());

        for (int i = 0; i < dots.size(); i++) {
            WebElement dot = dots.get(i);
            ValidationUtils.scrollIntoView(driver, dot);

            // 🔹 Click via JS (avoids overlay issues)
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", dot);

            int index = i;
            // 🔹 Wait until this dot is active AND banner has content
            WebElement slide = wait.until(d -> {
                List<WebElement> freshDots = d.findElements(dotsItem);
                if (freshDots.size() <= index) return null;

                boolean isActive = freshDots.get(index).getAttribute("class").contains("slick-active");
                if (!isActive) return null;

                WebElement s = d.findElement(activeBanner);
                String text = s.getText().trim();
                boolean hasAlt = s.findElements(images).stream()
                        .anyMatch(img -> {
                            String alt = img.getAttribute("alt");
                            return alt != null && !alt.isBlank() && !"Banner image".equalsIgnoreCase(alt);
                        });

                return (!text.isEmpty() || hasAlt) ? s : null;
            });

            // 🔹 Build banner content
            StringBuilder content = new StringBuilder(slide.getText().trim());

            for (WebElement img : slide.findElements(images)) {
                String alt = img.getAttribute("alt");
                if (alt != null && !alt.isBlank() && !"Banner image".equalsIgnoreCase(alt)) {
                    content.append(" | ").append(alt.trim());
                }
            }
            String aria = slide.getAttribute("aria-label");
            if (aria != null && !aria.isBlank()) {
                content.append(" | ").append(aria.trim());
            }

            String bannerText = content.toString();
            collected.add(bannerText);

            LoggerUtil.info(this.getClass(), "Banner " + (i + 1) + ": " + bannerText);
        }

        return new ArrayList<>(collected);
    }

}
