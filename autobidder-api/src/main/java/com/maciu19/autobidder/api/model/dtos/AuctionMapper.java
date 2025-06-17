package com.maciu19.autobidder.api.model.dtos;

import com.maciu19.autobidder.api.controller.dtos.AuctionResponseDto;
import com.maciu19.autobidder.api.model.Auction;
import org.springframework.stereotype.Component;

@Component
public class AuctionMapper {

    private final VehicleInfoMapper vehicleInfoMapper;

    public AuctionMapper(VehicleInfoMapper vehicleInfoMapper) {
        this.vehicleInfoMapper = vehicleInfoMapper;
    }

    public AuctionResponseDto toDto(Auction auction) {
        if (auction == null) {
            return null;
        }

        VehicleInfo vehicleInfoDto = vehicleInfoMapper.toVehicleInfo(auction.getVehicleEngineOption());

        return new AuctionResponseDto(
                auction.getId(),
                auction.getSeller(),
                vehicleInfoDto,
                auction.getVin(),
                auction.getLocation(),
                auction.getStartingPrice(),
                auction.getEndTime(),
                auction.getSteeringWheelSide(),
                auction.isHasWarranty(),
                auction.isNoCrashRegistered(),
                auction.getMakeYear(),
                auction.getMileage(),
                auction.getFeatures(),
                auction.getExteriorColor(),
                auction.getInteriorColor(),
                auction.getDescription(),
                auction.getModifications(),
                auction.getKnownFlaws(),
                auction.getRecentServiceHistory(),
                auction.getOtherItemsIncluded(),
                auction.getOwnershipHistory(),
                auction.getCreatedDate(),
                auction.getLastModifiedDate()
        );
    }
}
