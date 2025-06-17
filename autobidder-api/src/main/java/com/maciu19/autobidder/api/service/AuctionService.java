package com.maciu19.autobidder.api.service;

import com.maciu19.autobidder.api.controller.dtos.AuctionResponseDto;
import com.maciu19.autobidder.api.controller.dtos.CreateAuctionRequest;
import com.maciu19.autobidder.api.model.User;

public interface AuctionService {

    AuctionResponseDto createAuction(CreateAuctionRequest request, User seller);
}
