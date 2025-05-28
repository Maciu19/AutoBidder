package com.maciu19.autobidder.api.service.scraper;

import com.maciu19.autobidder.api.model.Manufacturer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleScraperServiceImpl implements VehicleScraperService {

    private static final Logger log = LoggerFactory.getLogger(VehicleScraperServiceImpl.class);
    private static final String AUTOEVOLUTION_CARS_URL = "https://www.autoevolution.com/cars/";
    private static final HttpClient client = HttpClient.newHttpClient();

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
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(targetUri))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                            + "(KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .header("Accept", "text/html,application/xhtml+xml")
                    .header("Accept-Language", "en-US,en;q=0.9")
                    .header("Referer", "https://www.google.com/")
                    .header("Cookie", "aelastts=1748193018")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                return Optional.of(response.body());
            } else {
                log.warn("Request to {} failed with status code: {}", targetUri, statusCode);
                return Optional.empty();
            }
        } catch(IOException e) {
            log.error("I/O error during request to {}: {}", targetUri, e.getMessage(), e);
            return Optional.empty();
        } catch(InterruptedException e) {
            log.error("Request to {} was interrupted: {}", targetUri, e.getMessage(), e);
            return Optional.empty();
        } catch (URISyntaxException e) {
            log.error("Invalid URI {}: {}", targetUri, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
