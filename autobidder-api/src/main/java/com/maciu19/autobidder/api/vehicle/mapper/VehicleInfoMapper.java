package com.maciu19.autobidder.api.vehicle.mapper;

import com.maciu19.autobidder.api.vehicle.dto.*;
import com.maciu19.autobidder.api.vehicle.model.Manufacturer;
import com.maciu19.autobidder.api.vehicle.model.VehicleEngineOption;
import com.maciu19.autobidder.api.vehicle.model.VehicleModel;
import com.maciu19.autobidder.api.vehicle.model.VehicleModelGeneration;
import org.springframework.stereotype.Component;

@Component
public class VehicleInfoMapper {

    public ManufacturerDto toDto(Manufacturer manufacturer) {
        if (manufacturer == null) {
            return null;
        }

        return new ManufacturerDto(
                manufacturer.getId(),
                manufacturer.getName(),
                manufacturer.getCreatedDate(),
                manufacturer.getLastModifiedDate()
        );
    }

    public VehicleEngineOptionDto toDto(VehicleEngineOption engineOption) {
        if (engineOption == null) {
            return null;
        }

        return new VehicleEngineOptionDto(
                engineOption.getId(),
                engineOption.getUrl(),
                engineOption.getName(),
                engineOption.getFuelType(),
                engineOption.getCreatedDate(),
                engineOption.getLastModifiedDate()
        );
    }

    public VehicleModelDto toDto(VehicleModel vehicleModel) {
        if (vehicleModel == null) {
            return null;
        }

        return new VehicleModelDto(
                vehicleModel.getId(),
                vehicleModel.getName(),
                vehicleModel.getVehicleModelSegment(),
                vehicleModel.getCreatedDate(),
                vehicleModel.getLastModifiedDate()
        );
    }

    public VehicleModelGenerationDto toDto(VehicleModelGeneration vehicleModelGeneration) {
        if (vehicleModelGeneration == null) {
            return null;
        }

        return new VehicleModelGenerationDto(
                vehicleModelGeneration.getId(),
                vehicleModelGeneration.getStartYear(),
                vehicleModelGeneration.getEndYear(),
                vehicleModelGeneration.getCreatedDate(),
                vehicleModelGeneration.getLastModifiedDate()
        );
    }

    public VehicleInfo toVehicleInfo(VehicleEngineOption engineOption) {
        if (engineOption == null) {
            return null;
        }

        VehicleModelGeneration generation = engineOption.getVehicleModelGeneration();
        VehicleModel model = generation.getVehicleModel();
        Manufacturer manufacturer = model.getManufacturer();

        return new VehicleInfo(
                manufacturer.getId(),
                model.getId(),
                generation.getId(),
                engineOption.getId(),
                manufacturer.getName(),
                model.getName(),
                model.getVehicleModelSegment(),
                generation.getName(),
                generation.getStartYear(),
                generation.getEndYear(),
                engineOption.getName(),
                engineOption.getFuelType()
        );
    }
}
