package com.maciu19.autobidder.api.controller;

import com.maciu19.autobidder.api.controller.dtos.AuctionResponseDto;
import com.maciu19.autobidder.api.controller.dtos.CreateAuctionRequest;
import com.maciu19.autobidder.api.model.Auction;
import com.maciu19.autobidder.api.model.User;
import com.maciu19.autobidder.api.repository.AuctionRepository;
import com.maciu19.autobidder.api.service.AuctionService;
import com.maciu19.autobidder.api.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final UserService userService;
    private final AuctionService auctionService;

    public AuctionController(
            UserService userService,
            AuctionService auctionService) {
        this.userService = userService;
        this.auctionService = auctionService;
    }

    @PostMapping
    public ResponseEntity<AuctionResponseDto> createAuction(
            @Valid @RequestBody CreateAuctionRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        User seller = userService.getCurrentUser(jwt);

        AuctionResponseDto createdAuction = auctionService.createAuction(request, seller);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdAuction);
    }
}
