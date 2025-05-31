package com.maciu19.autobidder.api.service.scraper;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.enums.VehicleModelSegment;
import com.maciu19.autobidder.api.repository.ManufacturerRepository;
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

    public VehicleScraperServiceImpl(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
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
