package com.maciu19.autobidder.api.auction;

import com.maciu19.autobidder.api.auction.dto.AuctionResponseDto;
import com.maciu19.autobidder.api.auction.dto.AuctionSummaryDto;
import com.maciu19.autobidder.api.auction.dto.CreateAuctionRequest;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.auction.service.AuctionService;
import com.maciu19.autobidder.api.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping
    public ResponseEntity<List<AuctionSummaryDto>> getActiveAuctions() {
        List<AuctionSummaryDto> activeAuctions = auctionService.getActiveAuctions();
        return ResponseEntity.ok(activeAuctions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponseDto> getById(@PathVariable UUID id) {
        AuctionResponseDto auctionDto = auctionService.getAuctionById(id);
        return ResponseEntity.ok(auctionDto);
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
