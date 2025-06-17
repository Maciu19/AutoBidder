package com.maciu19.autobidder.api.scraper;

import com.maciu19.autobidder.api.scraper.service.VehicleScraperService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/scraper")
public class ScraperController {

    private final VehicleScraperService vehicleScraperService;

    public ScraperController(VehicleScraperService vehicleScraperService) {
        this.vehicleScraperService = vehicleScraperService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startManualScrape() {
        vehicleScraperService.performFullScrape();
        return ResponseEntity.ok("Scraping process started successfully in the background.");
    }
}
