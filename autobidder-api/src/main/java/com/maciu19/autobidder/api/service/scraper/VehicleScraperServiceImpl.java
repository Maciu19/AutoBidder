package com.maciu19.autobidder.api.service.scraper;

import jakarta.annotation.PostConstruct;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class VehicleScraperServiceImpl implements VehicleScraperService {

    private static final Logger log = LoggerFactory.getLogger(VehicleScraperServiceImpl.class);

    @Value("${scraper.base-url:https://www.autoevolution.com}")
    private String baseUrl;

    @Value("${scraper.manufacturers.exclude:}")
    private List<String> manufacturerExcludeList;

    private final AsyncVehicleProcessor asyncVehicleProcessor;

    public VehicleScraperServiceImpl(
            AsyncVehicleProcessor asyncVehicleProcessor) {
        this.asyncVehicleProcessor = asyncVehicleProcessor;
    }

    @PostConstruct
    private void normalizeExcludeList() {
        if (this.manufacturerExcludeList != null) {
            this.manufacturerExcludeList.replaceAll(String::toLowerCase);
        }
    }

    /**
     * Orchestrates the full scraping process in parallel.
     * This method fetches manufacturer URLs and then launches an async task for each one.
     */
    @Override
    public void performFullScrape() {
        log.info("Starting full vehicle data scrape from {}", baseUrl);

        try {
            List<Map<String, String>> manufacturerInfos = scrapeManufacturerInfos();
            if (manufacturerInfos.isEmpty()) {
                log.warn("No manufacturers found to scrape. Stopping.");
                return;
            }

            log.info("Found {} manufacturers to process.", manufacturerInfos.size());

            List<CompletableFuture<Void>> futures = manufacturerInfos.stream()
                    .map(info -> asyncVehicleProcessor.processManufacturer(info.get("name"), info.get("url")))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } finally {
            log.info("Cleaning up the orchestrator's WebDriver instance.");
            ScraperUtils.cleanupDriver();
        }

        log.info("Full vehicle data scrape orchestration completed.");
    }

    private List<Map<String, String>> scrapeManufacturerInfos() {
        String manufacturersPageUrl = baseUrl + "/cars/";
        Optional<String> responseBodyOptional = ScraperUtils.fetchResponseBody(manufacturersPageUrl, "h5 > a > span");

        if (responseBodyOptional.isEmpty()) {
            log.warn("Failed to fetch response body for manufacturers page: {}", manufacturersPageUrl);
            return Collections.emptyList();
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements manufacturerLinks = doc.select("h5 > a");

        List<Map<String, String>> manufacturerInfos = new ArrayList<>();
        for (Element link : manufacturerLinks) {
            String name = link.text().trim();
            String url = link.attr("href").trim();

            if (!name.isEmpty() && !url.isEmpty() && !manufacturerExcludeList.contains(name.toLowerCase())) {
                manufacturerInfos.add(Map.of("name", name, "url", url));
            }
        }
        return manufacturerInfos;
    }
}
