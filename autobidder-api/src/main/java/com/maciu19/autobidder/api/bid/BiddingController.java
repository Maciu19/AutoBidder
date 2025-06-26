package com.maciu19.autobidder.api.bid;

import com.maciu19.autobidder.api.bid.dto.BidDto;
import com.maciu19.autobidder.api.bid.dto.BidRequestDto;
import com.maciu19.autobidder.api.bid.service.BiddingService;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/bid")
public class BiddingController {

    private final UserService userService;
    private final BiddingService biddingService;

    public BiddingController(
            UserService userService,
            BiddingService biddingService) {
        this.userService = userService;
        this.biddingService = biddingService;
    }

    @PostMapping("/placeBid")
    public ResponseEntity<BidDto> placeBid(@RequestBody BidRequestDto bidRequest) {
        User bidder = userService.getCurrentUser();

        BidDto result = biddingService.placeBid(bidRequest, bidder);
        return ResponseEntity.ok(result);
    }
}
