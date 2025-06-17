package com.maciu19.autobidder.api.vehicle.mapper;

import com.maciu19.autobidder.api.vehicle.dto.VehicleInfo;
import com.maciu19.autobidder.api.vehicle.model.Manufacturer;
import com.maciu19.autobidder.api.vehicle.model.VehicleEngineOption;
import com.maciu19.autobidder.api.vehicle.model.VehicleModel;
import com.maciu19.autobidder.api.vehicle.model.VehicleModelGeneration;
import org.springframework.stereotype.Component;

@Component
public class VehicleInfoMapper {

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
