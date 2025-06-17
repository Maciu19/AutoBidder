package com.maciu19.autobidder.api.auction.mapper;

import com.maciu19.autobidder.api.auction.dto.AuctionResponseDto;
import com.maciu19.autobidder.api.auction.dto.MediaAssetDto;
import com.maciu19.autobidder.api.auction.model.Auction;
import com.maciu19.autobidder.api.vehicle.dto.VehicleInfo;
import com.maciu19.autobidder.api.vehicle.mapper.VehicleInfoMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuctionMapper {

    private final VehicleInfoMapper vehicleInfoMapper;
    private final MediaAssetMapper mediaAssetMapper;

    public AuctionMapper(
            VehicleInfoMapper vehicleInfoMapper,
            MediaAssetMapper mediaAssetMapper) {
        this.vehicleInfoMapper = vehicleInfoMapper;
        this.mediaAssetMapper = mediaAssetMapper;
    }

    public AuctionResponseDto toDto(Auction auction) {
        if (auction == null) {
            return null;
        }

        VehicleInfo vehicleInfoDto = vehicleInfoMapper.toVehicleInfo(auction.getVehicleEngineOption());
        List<MediaAssetDto> mediaAssetDtoList = mediaAssetMapper.toDtoList(auction.getMediaAssets());

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
                mediaAssetDtoList,
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
