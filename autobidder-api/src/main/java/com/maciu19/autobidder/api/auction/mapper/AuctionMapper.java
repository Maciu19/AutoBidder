package com.maciu19.autobidder.api.auction.mapper;

import com.maciu19.autobidder.api.auction.dto.AuctionResponseDto;
import com.maciu19.autobidder.api.auction.dto.AuctionSummaryDto;
import com.maciu19.autobidder.api.auction.dto.MediaAssetDto;
import com.maciu19.autobidder.api.auction.model.Auction;
import com.maciu19.autobidder.api.bid.dto.BidDto;
import com.maciu19.autobidder.api.bid.mapper.BidMapper;
import com.maciu19.autobidder.api.user.dto.UserDto;
import com.maciu19.autobidder.api.user.mapper.UserMapper;
import com.maciu19.autobidder.api.vehicle.dto.VehicleInfo;
import com.maciu19.autobidder.api.vehicle.mapper.VehicleInfoMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuctionMapper {

    private final VehicleInfoMapper vehicleInfoMapper;
    private final MediaAssetMapper mediaAssetMapper;
    private final UserMapper userMapper;
    private final BidMapper bidMapper;

    public AuctionMapper(
            VehicleInfoMapper vehicleInfoMapper,
            MediaAssetMapper mediaAssetMapper,
            UserMapper userMapper,
            BidMapper bidMapper) {
        this.vehicleInfoMapper = vehicleInfoMapper;
        this.mediaAssetMapper = mediaAssetMapper;
        this.userMapper = userMapper;
        this.bidMapper = bidMapper;
    }

    public AuctionSummaryDto toSummaryDto(Auction auction) {
        if (auction == null) {
            return null;
        }

        String thumbnailUrl = auction.getMediaAssets().isEmpty() ? null : auction.getMediaAssets().getFirst().getFileUrl();

        return new AuctionSummaryDto(
                auction.getId(),
                auction.getTitle(),
                auction.getStatus(),
                auction.getStartingPrice(),
                auction.getCurrentPrice(),
                auction.getEndTime(),
                thumbnailUrl,
                auction.getBids().size()
        );
    }

    public List<AuctionSummaryDto> toListSummaryDto(List<Auction> auctions) {
        return auctions.stream().map(this::toSummaryDto).toList();
    }

    public AuctionResponseDto toDto(Auction auction) {
        if (auction == null) {
            return null;
        }

        List<MediaAssetDto> mediaAssetDtoList = mediaAssetMapper.toDtoList(auction.getMediaAssets());
        List<BidDto> bids = bidMapper.toListDto(auction.getBids());

        VehicleInfo vehicleInfoDto = vehicleInfoMapper.toVehicleInfo(auction.getVehicleEngineOption());
        UserDto seller = userMapper.toDto(auction.getSeller());
        UserDto winner = userMapper.toDto(auction.getWinningUser());

        return new AuctionResponseDto(
                auction.getId(),
                seller,
                vehicleInfoDto,
                auction.getVin(),
                auction.getLocation(),
                auction.getStartingPrice(),
                auction.getStartTime(),
                auction.getEndTime(),
                auction.getStatus(),
                auction.getCurrentPrice(),
                winner,
                bids,
                auction.getSteeringWheelSide(),
                auction.isHasWarranty(),
                auction.isNoCrashRegistered(),
                auction.getMakeYear(),
                auction.getMileage(),
                auction.getExteriorColor(),
                auction.getInteriorColor(),
                mediaAssetDtoList,
                auction.getFeatures(),
                auction.getTitle(),
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

    public List<AuctionResponseDto> toDtoList(List<Auction> auctions) {
        return auctions.stream().map(this::toDto).toList();
    }
}
