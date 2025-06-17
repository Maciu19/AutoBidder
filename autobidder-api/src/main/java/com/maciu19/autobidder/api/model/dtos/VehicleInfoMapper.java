package com.maciu19.autobidder.api.model.dtos;

import com.maciu19.autobidder.api.model.Manufacturer;
import com.maciu19.autobidder.api.model.VehicleEngineOption;
import com.maciu19.autobidder.api.model.VehicleModel;
import com.maciu19.autobidder.api.model.VehicleModelGeneration;
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
