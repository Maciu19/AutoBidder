package com.maciu19.autobidder.api.auction.service;

import com.maciu19.autobidder.api.auction.dto.AuctionResponseDto;
import com.maciu19.autobidder.api.auction.dto.CreateAuctionRequest;
import com.maciu19.autobidder.api.user.model.User;

public interface AuctionService {

    AuctionResponseDto createAuction(CreateAuctionRequest request, User seller);
}
