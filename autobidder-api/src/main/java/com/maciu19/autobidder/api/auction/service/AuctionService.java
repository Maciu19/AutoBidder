package com.maciu19.autobidder.api.auction.service;

import com.maciu19.autobidder.api.auction.dto.AuctionResponseDto;
import com.maciu19.autobidder.api.auction.dto.AuctionSummaryDto;
import com.maciu19.autobidder.api.auction.dto.CreateAuctionRequest;
import com.maciu19.autobidder.api.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface AuctionService {

    List<AuctionSummaryDto> getActiveAuctions();

    AuctionResponseDto getAuctionById(UUID auctionId);

    AuctionResponseDto createAuction(CreateAuctionRequest request, User seller);
}
