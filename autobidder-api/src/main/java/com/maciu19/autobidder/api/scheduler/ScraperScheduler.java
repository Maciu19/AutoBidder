package com.maciu19.autobidder.api.scheduler;

import com.maciu19.autobidder.api.service.scraper.VehicleScraperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScraperScheduler {

    private static final Logger log = LoggerFactory.getLogger(ScraperScheduler.class);

    private final VehicleScraperService vehicleScraperService;

    public ScraperScheduler(VehicleScraperService vehicleScraperService) {
        this.vehicleScraperService = vehicleScraperService;
    }

    @Scheduled(cron = "${scraper.schedule.cron:0 0 3 1 * ?}")
    public void runMonthlyVehicleScrapeJob() {
        log.info("Monthly vehicle data scrape job started.");
        try {
            vehicleScraperService.performFullScrape();
            log.info("Monthly vehicle data scrape job finished successfully.");
        } catch (Exception e) {
            log.error("Monthly vehicle data scrape job failed: {}", e.getMessage(), e);
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runScrapeImmediatelyOnStartup() {
        log.info("Triggering immediate vehicle data scrape on application startup for testing.");
        try {
            vehicleScraperService.performFullScrape();
            log.info("Immediate vehicle data scrape on startup finished successfully.");
        } catch (Exception e) {
            log.error("Immediate vehicle data scrape on startup failed: {}", e.getMessage(), e);
        }
    }
}
