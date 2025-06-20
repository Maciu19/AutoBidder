package com.maciu19.autobidder.api.user;

import com.maciu19.autobidder.api.auction.dto.AuctionSummaryDto;
import com.maciu19.autobidder.api.auction.service.AuctionService;
import com.maciu19.autobidder.api.bid.dto.BidDto;
import com.maciu19.autobidder.api.bid.mapper.BidMapper;
import com.maciu19.autobidder.api.bid.model.Bid;
import com.maciu19.autobidder.api.bid.service.BiddingService;
import com.maciu19.autobidder.api.user.dto.UserDto;
import com.maciu19.autobidder.api.user.mapper.UserMapper;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/account")
public class AccountController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final BiddingService biddingService;
    private final BidMapper bidMapper;
    private final AuctionService auctionService;

    public AccountController(
            UserService userService,
            UserMapper userMapper,
            BiddingService biddingService,
            BidMapper bidMapper,
            AuctionService auctionService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.biddingService = biddingService;
        this.bidMapper = bidMapper;
        this.auctionService = auctionService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUser() {
        return ResponseEntity.ok(
                userMapper.toDto(userService.getCurrentUser()));
    }

    @GetMapping("/auctions")
    public ResponseEntity<List<AuctionSummaryDto>> getCurrentUserAuctions(@AuthenticationPrincipal Jwt jwt) {
        User seller = userService.getCurrentUser(jwt);
        return ResponseEntity.ok(auctionService.getUserAuctions(seller));
    }

    @GetMapping("/bids")
    public ResponseEntity<List<BidDto>> getCurrentUserBids(@AuthenticationPrincipal Jwt jwt) {
        User user = userService.getCurrentUser();
        List<Bid> bids = biddingService.getBidsForUser(user);

        return ResponseEntity.ok(bidMapper.toListDto(bids));
    }
}
