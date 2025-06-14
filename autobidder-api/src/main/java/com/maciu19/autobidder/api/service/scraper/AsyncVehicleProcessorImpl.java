package com.maciu19.autobidder.api.service.scraper;

import com.maciu19.autobidder.api.config.AsyncConfig;
import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleEngineOption;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.VehicleModelGeneration;
import com.maciu19.autobidder.api.model.enums.FuelType;
import com.maciu19.autobidder.api.model.enums.VehicleModelSegment;
import com.maciu19.autobidder.api.repository.ManufacturerRepository;
import com.maciu19.autobidder.api.repository.VehicleModelRepository;
import com.maciu19.autobidder.api.utils.EnumUtils;

import jakarta.transaction.Transactional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AsyncVehicleProcessorImpl implements AsyncVehicleProcessor {

    private static final Logger log = LoggerFactory.getLogger(AsyncVehicleProcessorImpl.class);

    @Value("${scraper.manufacturers.min-model-year:}")
    private Integer minModelYear;

    private final ManufacturerRepository manufacturerRepository;
    private final VehicleModelRepository vehicleModelRepository;

    public AsyncVehicleProcessorImpl(ManufacturerRepository manufacturerRepository,
                                     VehicleModelRepository vehicleModelRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.vehicleModelRepository = vehicleModelRepository;
    }

    /**
     * Asynchronous method to process a single manufacturer.
     * It handles scraping models, generations, and engines for that one manufacturer.
     * Running in a separate transaction ensures data consistency for the manufacturer.
     */
    @Override
    @Async(AsyncConfig.TASK_EXECUTOR_SCRAPER)
    @Transactional
    public CompletableFuture<Void> processManufacturer(String name, String url) {
        log.info("[Thread: {}] Starting to process manufacturer: {}", Thread.currentThread().getName(), name);
        try {
            Manufacturer manufacturer = manufacturerRepository.findByUrl(url)
                    .orElseGet(() -> {
                        Manufacturer newManufacturer = new Manufacturer();
                        newManufacturer.setName(name);
                        newManufacturer.setUrl(url);
                        return newManufacturer;
                    });

            scrapeAndSaveVehicleModelsForManufacturer(manufacturer);

            if (!manufacturer.getVehicleModels().isEmpty()) {
                manufacturerRepository.save(manufacturer);
                log.info("[Thread: {}] Scraped and saved {} models for {}.", Thread.currentThread().getName(), manufacturer.getVehicleModels().size(), name);
            }  else {
                log.info("[Thread: {}] No new models found for {}.", Thread.currentThread().getName(), name);
            }

        } catch (Exception e) {
            log.error("[Thread: {}] Failed to process manufacturer {}: {}", Thread.currentThread().getName(), name, e.getMessage(), e);
        } finally {
            ScraperUtils.cleanupDriver();
            log.info("[Thread: {}] Finished processing manufacturer: {}", Thread.currentThread().getName(), name);
        }

        return CompletableFuture.completedFuture(null);
    }

    private void scrapeAndSaveVehicleModelsForManufacturer(Manufacturer manufacturer) {
        String manufacturerPageUrl = manufacturer.getUrl();
        Optional<String> responseBodyOptional = ScraperUtils.fetchResponseBody(manufacturerPageUrl, "div.col2width.bcol-white.fl div.padcol2");

        if (responseBodyOptional.isEmpty()) {
            log.warn("Failed to fetch response body for manufacturer models page: {}", manufacturerPageUrl);
            return;
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements carInfoDivElements = doc.select("div.col2width.bcol-white.fl div.padcol2");

        for (Element carInfo : carInfoDivElements) {
            Element modelLink = carInfo.selectFirst("a");
            Element modelWithManufacturerName = carInfo.selectFirst("a > h4");
            Element modelBodyType = carInfo.selectFirst("p.body");

            if (modelWithManufacturerName == null || modelWithManufacturerName.text().isEmpty() ||
                    modelLink == null || modelLink.attr("href").isEmpty()) {

                log.warn("Skipping vehicle model entry for {}: Model Name: '{}', URL: '{}'", manufacturer.getName(), modelWithManufacturerName != null ? modelWithManufacturerName.text() : "N/A", modelLink != null ? modelLink.attr("href") : "N/A");

                continue;
            }

            String modelURL = modelLink.attr("href").trim();
            String modelName = modelWithManufacturerName.text().substring(
                    modelWithManufacturerName.text().indexOf(" ") + 1).trim();

            VehicleModelSegment modelBody = null;
            if (modelBodyType != null && !modelBodyType.text().isEmpty()) {
                String modelBodyTypeSingular = modelBodyType.text().substring(0, modelBodyType.text().length() - 1).toLowerCase().trim();
                modelBody = EnumUtils.fromId(VehicleModelSegment.class, modelBodyTypeSingular);
            }

            if (vehicleModelRepository.findByUrl(modelURL).isPresent()) {
                continue;
            }

            VehicleModel newModel = new VehicleModel();
            newModel.setName(modelName);
            newModel.setUrl(modelURL);
            newModel.setManufacturer(manufacturer);
            newModel.setVehicleModelSegment(modelBody);

            scrapeAndSaveModelGenerationsAndEngines(newModel);

            manufacturer.addVehicleModel(newModel);
        }
    }

    private void scrapeAndSaveModelGenerationsAndEngines(VehicleModel vehicleModel) {
        String modelGenerationsUrl = vehicleModel.getUrl();
        Optional<String> responseBodyOptional = ScraperUtils.fetchResponseBody(modelGenerationsUrl, "div.col23width.fl.bcol-white");

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
            Element nameElement = carInfoBlock.select("div.years.padcol2 h2 a span.col-red").first();
            Integer[] years = parseProductionYears(carInfoBlock);
            Integer startYear = years[0];
            Integer endYear = years[1];

            if (startYear < minModelYear || nameElement == null || !nameElement.hasText()) {
                continue;
            }

            VehicleModelGeneration newGeneration = new VehicleModelGeneration();
            newGeneration.setName(nameElement.text().trim());
            newGeneration.setVehicleModel(vehicleModel);
            newGeneration.setStartYear(startYear);
            newGeneration.setEndYear(endYear);

            scrapeAndBuildEnginesFor(newGeneration, carInfoBlock);

            vehicleModel.addGeneration(newGeneration);
        }
    }

    /**
     * @param newGeneration The generation object to add engines to.
     * @param carInfoBlock The HTML block for the generation, which contains the "full specs" link.
     */
    private void scrapeAndBuildEnginesFor(VehicleModelGeneration newGeneration, Element carInfoBlock) {
        Elements fuelTypeSections = carInfoBlock.select("div.mot.padcol2.clearfix");
        if (fuelTypeSections.isEmpty()) {
            log.warn("Could not find any engine fuel sections for generation: {}", newGeneration.getName());
            return;
        }

        for (Element fuelSection : fuelTypeSections) {
            Element fuelTypeElement = fuelSection.selectFirst("strong.upcase.dispblock.mgbot_10");
            if (fuelTypeElement == null) {
                log.debug("Skipping a fuel section with no title for generation '{}'.", newGeneration.getName());
                continue;
            }

            String rawFuelText = fuelTypeElement.text();
            FuelType fuelType = getFuelType(rawFuelText);
            if (fuelType == FuelType.UNKNOWN) {
                log.warn("Could not map fuel type '{}' to an enum for generation '{}'.", rawFuelText, newGeneration.getName());
            }

            Elements engineLinks = fuelSection.select("p.engitm a");
            for (Element engineLink : engineLinks) {
                Element engineNameElement = engineLink.selectFirst("span.col-green2");
                String engineUrl = engineLink.attr("href");

                if (engineNameElement == null || !engineNameElement.hasText() || engineUrl.isEmpty()) {
                    log.debug("Skipping an engine link with missing name or URL for generation '{}'.", newGeneration.getName());
                    continue;
                }

                VehicleEngineOption newEngine = new VehicleEngineOption();
                newEngine.setName(engineNameElement.text());
                newEngine.setUrl(engineUrl);
                newEngine.setFuelType(fuelType);

                newGeneration.addEngine(newEngine);
            }
        }
    }

    private FuelType getFuelType(String rawFuelText) {
        return parseFuelType(rawFuelText);
    }

    /**
     * Parses a raw string from the website (e.g., "GASOLINE ENGINES") into a FuelType enum.
     * @param rawText The text scraped from the page.
     * @return The matching FuelType enum, or null if no match is found.
     */
    private FuelType parseFuelType(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return null;
        }

        String cleanedText = rawText.toLowerCase();

        if (cleanedText.contains("hybrid")) {
            if (cleanedText.contains("mild")) {
                return FuelType.MILD_HYBRID;
            }
            else if (cleanedText.contains("plug")) {
                return FuelType.PLUG_IN_HYBRID;
            }
            else {
                return FuelType.HYBRID;
            }
        }
        if (cleanedText.contains("gasoline") || cleanedText.contains("%s")) return FuelType.GASOLINE;
        if (cleanedText.contains("diesel")) return FuelType.DIESEL;
        if (cleanedText.contains("electric")) return FuelType.ELECTRIC;
        if ((cleanedText.contains("natural") && cleanedText.contains("gas")) || cleanedText.contains("cng")) return FuelType.NATURAL_GAS;
        if (cleanedText.contains("lpg")) return FuelType.LPG;
        if (cleanedText.contains("ethanol")) return FuelType.ETHANOL;

        return FuelType.UNKNOWN;
    }

    /**
     * Attempts to parse the production years from a car generation block using a fallback strategy.
     * @param carInfoBlock The Jsoup Element for the specific car generation.
     * @return An Integer array of size 2: [startYear, endYear]. endYear can be null. Returns [null, null] if parsing fails.
     */
    private static Integer[] parseProductionYears(Element carInfoBlock) {
        Integer startYear = null;
        Integer endYear = null;

        // STRATEGY 1: "(2021-Present)"
        Element yearLink = carInfoBlock.select("div.years.padcol2 h2 a").first();
        if (yearLink != null) {
            String yearText = yearLink.ownText().replaceAll("[()]", "").trim(); // "2021 - Present"
            if (!yearText.isEmpty()) {
                String[] years = yearText.split(" - ");
                try {
                    if (years.length >= 1) {
                        startYear = Integer.parseInt(years[0]);
                    }
                    if (years.length == 2 && years[1].matches("\\d{4}")) {
                        endYear = Integer.parseInt(years[1]);
                    }
                    return new Integer[]{startYear, endYear};
                } catch (NumberFormatException e) {
                    // Parsing failed, do nothing and proceed to the next strategy.
                }
            }
        }

        // STRATEGY 2: Fallback to the "Production years: startYear - endYear" paragraph
        Element productionPara = carInfoBlock.select("p:contains(Production years:)").first();
        if (productionPara != null) {
            String yearText = productionPara.text().replace("Production years:", "").trim();
            if (!yearText.isEmpty()) {
                String[] years = yearText.split(" - ");
                try {
                    if (years.length >= 1) {
                        startYear = Integer.parseInt(years[0]);
                    }
                    if (years.length == 2 && years[1].matches("\\d{4}")) {
                        endYear = Integer.parseInt(years[1]);
                    }
                    return new Integer[]{startYear, endYear};
                } catch (NumberFormatException e) {
                    // Both strategies failed.
                }
            }
        }

        return new Integer[]{null, null};
    }
}
