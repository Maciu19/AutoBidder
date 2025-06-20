package com.maciu19.autobidder.api.bid.service;

import com.maciu19.autobidder.api.bid.dto.BidRequestDto;
import com.maciu19.autobidder.api.bid.model.Bid;
import com.maciu19.autobidder.api.user.model.User;

import java.util.List;

public interface BiddingService {

    void placeBid(BidRequestDto bidRequest, User bidder);

    List<Bid> getBidsForUser(User currentUser);
}
