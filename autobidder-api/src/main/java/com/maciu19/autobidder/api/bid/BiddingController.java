package com.maciu19.autobidder.api.bid;

import com.maciu19.autobidder.api.bid.dto.BidRequestDto;
import com.maciu19.autobidder.api.bid.service.BiddingService;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.user.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class BiddingController {

    private final UserService userService;
    private final BiddingService biddingService;

    public BiddingController(
            UserService userService,
            BiddingService biddingService) {
        this.userService = userService;
        this.biddingService = biddingService;
    }

    @MessageMapping("/placeBid")
    public void placeBid(@Payload BidRequestDto bidRequest, Principal principal) {
        if (principal == null) {
            return;
        }

        User bidder = userService.getCurrentUser();
        biddingService.placeBid(bidRequest, bidder);
    }
}
