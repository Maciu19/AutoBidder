package com.maciu19.autobidder.api.bid.service;

import com.maciu19.autobidder.api.bid.dto.BidRequestDto;
import com.maciu19.autobidder.api.user.model.User;

public interface BiddingService {
    void placeBid(BidRequestDto bidRequest, User bidder);
}
