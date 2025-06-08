package com.maciu19.autobidder.api.service.scraper;

import java.util.concurrent.CompletableFuture;

public interface AsyncVehicleProcessor {

    CompletableFuture<Void> processManufacturer(String name, String url);
}
