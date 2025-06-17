package com.maciu19.autobidder.api.scraper.service;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.time.Duration;
import java.util.Optional;

public class ScraperUtils {

    private static final Logger log = LoggerFactory.getLogger(ScraperUtils.class);

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36";

    @Value("${scraper.web-driver.timeout-seconds:15}")
    private static int webdriverTimeoutSeconds;

    private static final ThreadLocal<WebDriver> webDriverThreadLocal = ThreadLocal.withInitial(() -> {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        return new FirefoxDriver(options);
    });

    /**
     * Private constructor to prevent instantiation.
     */
    private ScraperUtils() { }

    public static WebDriver getDriver() {
        return webDriverThreadLocal.get();
    }

    /**
     * Safely quits the WebDriver and removes it from the ThreadLocal storage.
     * This MUST be called in a 'finally' block to prevent resource leaks.
     */
    public static void cleanupDriver() {
        WebDriver driver = webDriverThreadLocal.get();
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            log.error("Error while quitting WebDriver for thread: {}", Thread.currentThread().getName(), e);
        } finally {
            webDriverThreadLocal.remove();
        }
    }

    /**
     * A generic utility method to fetch a page's source using the current thread's WebDriver.
     *
     * @param targetUri       The URL to fetch.
     * @param waitForSelector A CSS selector for an element to wait for on the page.
     * @return An Optional containing the page source, or empty if an error occurs.
     */
    public static Optional<String> fetchResponseBody(String targetUri, String waitForSelector) {
        WebDriver driver = getDriver();
        try {
            driver.get(targetUri);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(webdriverTimeoutSeconds));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(waitForSelector)));

            String pageSource = driver.getPageSource();
            if (pageSource == null || pageSource.isEmpty()) {
                log.warn("Fetched empty page source for URI: {}", targetUri);
                return Optional.empty();
            } else {
                return Optional.of(pageSource);
            }

        } catch (org.openqa.selenium.TimeoutException e) {
            log.error("Timeout waiting for element '{}' on URI: {}", waitForSelector, targetUri);
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error while fetching: {}", targetUri, e);
            return Optional.empty();
        }
    }
}
