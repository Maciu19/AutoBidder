package com.maciu19.autobidder.api.service.scraper;

import com.maciu19.autobidder.api.model.Manufacturer;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleScraperServiceImpl implements VehicleScraperService {

    private static final Logger log = LoggerFactory.getLogger(VehicleScraperServiceImpl.class);
    private static final String AUTOEVOLUTION_CARS_URL = "https://www.autoevolution.com/cars/";

    @Override
    public List<Manufacturer> getAllManufacturers() {
        Optional<String> responseBodyOptional = fetchResponseBody();

        if (responseBodyOptional.isEmpty()) {
            return Collections.emptyList();
        }

        Document doc = Jsoup.parse(responseBodyOptional.get());
        Elements carSpans = doc.select("h5 > a > span");

        List<Manufacturer> manufacturers = new ArrayList<>();
        for (Element span : carSpans) {
            String carName = span.text();
            manufacturers.add(new Manufacturer(carName));
        }

        return manufacturers;
    }

    private Optional<String> fetchResponseBody() {
        return fetchResponseBody(AUTOEVOLUTION_CARS_URL);
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
