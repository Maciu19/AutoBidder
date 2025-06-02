package com.maciu19.autobidder.api.service.scraper;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleEngineOption;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.VehicleModelGeneration;
import com.maciu19.autobidder.api.model.enums.VehicleModelSegment;
import com.maciu19.autobidder.api.repository.ManufacturerRepository;
import com.maciu19.autobidder.api.repository.VehicleEngineOptionRepository;
import com.maciu19.autobidder.api.repository.VehicleModelGenerationRepository;
import com.maciu19.autobidder.api.repository.VehicleModelRepository;
import com.maciu19.autobidder.api.utils.EnumUtils;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
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

@Service
public class VehicleScraperServiceImpl implements VehicleScraperService {

    private static final Logger log = LoggerFactory.getLogger(VehicleScraperServiceImpl.class);

    @Value("${scraper.base-url:https://www.autoevolution.com}")
    private String baseUrl;

    @Value("${scraper.web-driver.timeout-seconds:15}")
    private int webdriverTimeoutSeconds;

    private final ManufacturerRepository manufacturerRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final VehicleModelGenerationRepository modelGenerationRepository;
    private final VehicleEngineOptionRepository vehicleEngineOptionRepository;

    public VehicleScraperServiceImpl(ManufacturerRepository manufacturerRepository,
                                     VehicleModelRepository vehicleModelRepository,
                                     VehicleModelGenerationRepository modelGenerationRepository,
                                     VehicleEngineOptionRepository vehicleEngineOptionRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.vehicleModelRepository = vehicleModelRepository;
        this.modelGenerationRepository = modelGenerationRepository;
        this.vehicleEngineOptionRepository = vehicleEngineOptionRepository;
    }

    private WebDriver driver;
    private WebDriverWait wait;

    @PostConstruct
    public void init() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        this.driver = new FirefoxDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(webdriverTimeoutSeconds));
    }

    @PreDestroy
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Orchestrates the full scraping process.
     * This method will fetch manufacturers, then for each manufacturer,
     * fetch models, and for each model, fetch generations and engines.
     */
    @Override
    @Transactional
    public void performFullScrape() {
        log.info("Starting full vehicle data scrape from {}", baseUrl);
        try {
            List<Manufacturer> manufacturers = scrapeAndSaveManufacturers();
            log.info("Scraped and saved {} manufacturers.", manufacturers.size());

            for (Manufacturer manufacturer : manufacturers) {
                log.info("Scraping models for manufacturer: {}", manufacturer.getName());
                List<VehicleModel> models = scrapeAndSaveVehicleModelsForManufacturer(manufacturer);
                log.info("Scraped and saved {} models for {}.", models.size(), manufacturer.getName());

                for (VehicleModel model : models) {
                    log.info("Scraping generations and engines for model: {} ({})", model.getName(), model.getManufacturer().getName());
                    scrapeAndSaveModelGenerationsAndEngines(model);
                }
            }
            log.info("Full vehicle data scrape completed successfully.");
        } catch (Exception e) {
            log.error("Full vehicle data scrape failed: {}", e.getMessage(), e);
        }
    }

    @Transactional
    public List<Manufacturer> scrapeAndSaveManufacturers() {
        String manufacturersPageUrl = baseUrl + "/cars/";
        Optional<String> responseBodyOptional = fetchResponseBody(manufacturersPageUrl, "h5 > a > span");

        if (responseBodyOptional.isEmpty()) {
            log.warn("Failed to fetch response body for manufacturers page: {}", manufacturersPageUrl);
            return Collections.emptyList();
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements manufacturerLinks = doc.select("h5 > a");

        List<Manufacturer> scrapedManufacturers = new ArrayList<>();
        for (Element link : manufacturerLinks) {
            String name = link.text().trim();
            String manufacturerAbsoluteLink = link.attr("href").trim();

            if (name.isEmpty() || manufacturerAbsoluteLink.isEmpty()) {
                log.warn("Skipping manufacturer: Name = '{}', URL='{}'", name, manufacturerAbsoluteLink);
                continue;
            }

            Optional<Manufacturer> existingManufacturer = manufacturerRepository.findByUrl(manufacturerAbsoluteLink);

            if (existingManufacturer.isEmpty()) {
                Manufacturer newManufacturer = new Manufacturer();
                newManufacturer.setName(name);
                newManufacturer.setUrl(manufacturerAbsoluteLink);

                scrapedManufacturers.add(newManufacturer);
            }
        }

        if (!scrapedManufacturers.isEmpty()) {
            manufacturerRepository.saveAll(scrapedManufacturers);
            log.info("Processed and saved/updated {} manufacturers.", scrapedManufacturers.size());
        } else {
            log.info("No new manufacturers found or updates required during this scrape run.");
        }

        return manufacturerRepository.findAll();
    }

    @Transactional
    public List<VehicleModel> scrapeAndSaveVehicleModelsForManufacturer(Manufacturer manufacturer) {
        String manufacturerPageUrl = manufacturer.getUrl();

        Optional<String> responseBodyOptional = fetchResponseBody(manufacturerPageUrl, "div.padcol2");

        if (responseBodyOptional.isEmpty()) {
            log.warn("Failed to fetch response body for manufacturer models page: {}", manufacturerPageUrl);
            return Collections.emptyList();
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements carInfoDivElements = doc.select("div.padcol2");

        List<VehicleModel> scrapedVehicleModels = new ArrayList<>();
        for (Element carInfo : carInfoDivElements) {
            Element modelLink = carInfo.selectFirst("a");
            Element modelWithManufacturerName = carInfo.selectFirst("a > h4");
            Element modelBodyType = carInfo.selectFirst("p.body");

            if (modelWithManufacturerName == null || modelWithManufacturerName.text().isEmpty() ||
                    modelBodyType == null || modelBodyType.text().isEmpty() ||
                    modelLink == null || modelLink.attr("href").isEmpty()) {

                log.warn("Skipping vehicle model entry for {}: Model Name: '{}', URL: '{}'", manufacturer.getName(), modelWithManufacturerName != null ? modelWithManufacturerName.text() : "N/A", modelLink != null ? modelLink.attr("href") : "N/A");

                continue;
            }

            String modelURL = modelLink.attr("href").trim();
            String modelName = modelWithManufacturerName.text().substring(
                            modelWithManufacturerName.text().indexOf(" ") + 1).trim();
            String modelBodyTypeSingular = modelBodyType.text().substring(0, modelBodyType.text().length() - 1).toLowerCase().trim();

            VehicleModelSegment modelBody = EnumUtils.fromId(VehicleModelSegment.class, modelBodyTypeSingular);

            if (modelBody == null) {
                log.warn("Unknown VehicleModelSegment '{}' for model {}. Skipping.", modelBodyTypeSingular, modelName);
                continue;
            }

            Optional<VehicleModel> existingModel = vehicleModelRepository.findByUrl(modelURL);

            if (existingModel.isEmpty()) {
                VehicleModel newModel = new VehicleModel();
                newModel.setName(modelName);
                newModel.setUrl(modelURL);
                newModel.setManufacturer(manufacturer);
                newModel.setVehicleModelSegment(modelBody);

                scrapedVehicleModels.add(newModel);
            }
        }

        if (!scrapedVehicleModels.isEmpty()) {
            log.info("Processed and saved/updated {} vehicle models for {}.", scrapedVehicleModels.size(), manufacturer.getName());
            scrapedVehicleModels = vehicleModelRepository.saveAll(scrapedVehicleModels);
        } else {
            log.info("No new vehicle models found or updates required for {}.", manufacturer.getName());
        }

        return scrapedVehicleModels;
    }

    @Transactional
    public void scrapeAndSaveModelGenerationsAndEngines(VehicleModel vehicleModel) {
        String modelGenerationsUrl = vehicleModel.getUrl();

        Optional<String> responseBodyOptional = fetchResponseBody(modelGenerationsUrl, "div.col23width.fl.bcol-white");

        if (responseBodyOptional.isEmpty()) {
            log.warn("Failed to fetch response body for model generations page: {}", modelGenerationsUrl);
            return;
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements carInfoBlocks = doc.select("div.col23width.fl.bcol-white");

        if (carInfoBlocks.isEmpty()) {
            log.warn("No generation blocks found for model: {} on page {}", vehicleModel.getName(), modelGenerationsUrl);
            return;
        }

        for (Element carInfoBlock : carInfoBlocks) {
            Element yearsElement = carInfoBlock.select("div.years.padcol2 h2 a").first();

            if (yearsElement == null || yearsElement.ownText().trim().isEmpty()) {
                log.warn("Skipping generation block due to missing years element for model: {}", vehicleModel.getName());
                continue;
            }

            String yearsPart = yearsElement.ownText().replaceAll("[()]", "").trim();
            String[] years = yearsPart.split(" - ");

            Integer startYear = null;
            Integer endYear = null;
            if (years.length == 2) {
                try {
                    startYear = Integer.parseInt(years[0]);
                } catch (NumberFormatException e) {
                    log.error("Invalid start year format '{}' for model {}", years[0], vehicleModel.getName());
                    continue;
                }

                try {
                    endYear = Integer.parseInt(years[1]);
                } catch (NumberFormatException e) {
                    log.debug("End year is not a number for {}. Assuming 'Present'.", yearsPart);
                }
            } else {
                log.warn("Unexpected year format '{}' for model {}. Skipping.", yearsPart, vehicleModel.getName());
                continue;
            }

            Optional<VehicleModelGeneration> existingGeneration =
                    modelGenerationRepository.findByVehicleModelAndStartYearAndEndYear(vehicleModel.getId(), startYear, endYear);

            if (existingGeneration.isEmpty()) {
                VehicleModelGeneration generation = new VehicleModelGeneration();

                generation.setVehicleModel(vehicleModel);
                generation.setStartYear(startYear);
                generation.setEndYear(endYear);

                modelGenerationRepository.save(generation);
            }

            // TODO: We still need to include Vehicle Engines
        }
    }

    /**
     * Fetches the page source using Selenium and waits for a specific element.
     * This method now uses the shared WebDriver instance.
     * @param targetUri The URL to fetch.
     * @param waitForSelector A CSS selector for an element to wait for on the page.
     * @return An Optional containing the page source, or empty if an error occurs.
     */
    private Optional<String> fetchResponseBody(String targetUri, String waitForSelector) {
        try {
            driver.get(targetUri);
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
