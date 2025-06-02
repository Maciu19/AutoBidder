package com.maciu19.autobidder.api.service.scraper;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleEngineOption;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.VehicleModelGeneration;
import com.maciu19.autobidder.api.model.enums.VehicleModelSegment;
import com.maciu19.autobidder.api.repository.ManufacturerRepository;
import com.maciu19.autobidder.api.repository.VehicleModelRepository;
import com.maciu19.autobidder.api.utils.EnumUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VehicleScraperServiceImpl implements VehicleScraperService {

    private static final Logger log = LoggerFactory.getLogger(VehicleScraperServiceImpl.class);
    private static final String AUTOEVOLUTION_CARS_URL = "https://www.autoevolution.com";

    private final ManufacturerRepository manufacturerRepository;
    private final VehicleModelRepository vehicleModelRepository;

    public VehicleScraperServiceImpl(ManufacturerRepository manufacturerRepository,
                                     VehicleModelRepository vehicleModelRepository) {
        this.manufacturerRepository = manufacturerRepository;
        this.vehicleModelRepository = vehicleModelRepository;
    }

    @Override
    public List<Manufacturer> getAllManufacturers() {
        Optional<String> responseBodyOptional = fetchResponseBody(AUTOEVOLUTION_CARS_URL + "/cars/");

        if (responseBodyOptional.isEmpty()) {
            return Collections.emptyList();
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements carSpans = doc.select("h5 > a > span");

        List<Manufacturer> manufacturers = new ArrayList<>();
        for (Element span : carSpans) {
            String carName = preprocessString(span.text());
            manufacturers.add(new Manufacturer(carName));
        }

        return manufacturers;
    }

    @Override
    public List<VehicleModel> getAllVehicleModelsForManufacturer(UUID manufacturerId) {
        Manufacturer manufacturer = manufacturerRepository.getReferenceById(manufacturerId);

        Optional<String> responseBodyOptional = fetchResponseBody(AUTOEVOLUTION_CARS_URL + "/" + manufacturer.getName().toLowerCase() + "/");

        if (responseBodyOptional.isEmpty()) {
            return Collections.emptyList();
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements carInfoDivElements = doc.select("div.padcol2");

        List<VehicleModel> vehicleModels = new ArrayList<>();
        for (Element e : carInfoDivElements) {
            Element modelWithManufacturerName = e.selectFirst("a > h4");
            Element modelBodyType = e.selectFirst("p.body");

            if (modelWithManufacturerName == null || modelWithManufacturerName.text().isEmpty() ||
                    modelBodyType == null || modelBodyType.text().isEmpty()) {
                continue;
            }

            String modelName = preprocessString(
                    modelWithManufacturerName.text().substring(
                    modelWithManufacturerName.text().indexOf(" ") + 1));
            String modelBodyTypeSingular = modelBodyType.text().substring(0, modelBodyType.text().length() - 1);
            VehicleModelSegment modelBody = EnumUtils.fromId(VehicleModelSegment.class, preprocessString(modelBodyTypeSingular));

            vehicleModels.add(new VehicleModel(manufacturer, modelName, modelBody));
        }

        return vehicleModels;
    }

    @Override
    public List<VehicleModelGeneration> getAllModelGenerationForVehicleModel(UUID vehicleModelId) {
        VehicleModel vehicleModel = vehicleModelRepository.getReferenceById(vehicleModelId);

        Optional<String> responseBodyOptional = fetchResponseBody(AUTOEVOLUTION_CARS_URL + "/" +
                vehicleModel.getManufacturer().getName() + "/" +
                vehicleModel.getName() + "/");

        if (responseBodyOptional.isEmpty()) {
            return Collections.emptyList();
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements carInfoBlocks = doc.select("div.col23width.fl.bcol-white");

        List<VehicleModelGeneration> vehicleModelGenerations = new ArrayList<>();
        for (Element carInfoBlock : carInfoBlocks) {
            Element yearsElement = carInfoBlock.select("div.years.padcol2 h2 a").first();

            if (yearsElement == null) {
                continue;
            }

            String yearsPart = yearsElement.ownText().replaceAll("[()]", "").trim();
            String[] years = yearsPart.split(" - ");
            if (years.length == 2) {
                int startYear = Integer.parseInt(years[0]);

                Integer endYear;
                try {
                    endYear = Integer.parseInt(years[1]);
                } catch (NumberFormatException e) {
                    endYear = null;
                }

                vehicleModelGenerations.add(
                        new VehicleModelGeneration(vehicleModel, startYear, endYear));
            }

            Elements carEngines = carInfoBlock.select("p.engitm a");
            List<VehicleEngineOption> engineOptions = new ArrayList<>();
            for(Element carEngine : carEngines) {
                String link = carEngine.
                engineOptions.add(VehicleEngineOption.builder().link());
            }
        }

        return vehicleModelGenerations;
    }

    private String preprocessString(String input) {
        input = input.toLowerCase();
        return String.join("-", input.split(" "));
    }

    private Optional<String> fetchResponseBody(String targetUri) {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");

        WebDriver driver = new FirefoxDriver(options);

        try {
            driver.get(targetUri);
            Thread.sleep(1000);

            String pageSource = driver.getPageSource();
            if (pageSource == null || pageSource.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(pageSource);
            }

        } catch (InterruptedException e) {
            log.error("Scraping interrupted for URI: {}", targetUri, e);
            Thread.currentThread().interrupt();
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error while fetching: {}", targetUri, e);
            return Optional.empty();
        } finally {
            driver.quit();
        }
    }
}
