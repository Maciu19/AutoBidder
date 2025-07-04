package com.maciu19.autobidder.api.auction;

import com.maciu19.autobidder.api.auction.dto.*;
import com.maciu19.autobidder.api.auction.mapper.MediaAssetMapper;
import com.maciu19.autobidder.api.auction.model.FileType;
import com.maciu19.autobidder.api.auction.model.MediaAsset;
import com.maciu19.autobidder.api.exception.exceptions.FileUploadFailedException;
import com.maciu19.autobidder.api.user.model.User;
import com.maciu19.autobidder.api.auction.service.AuctionService;
import com.maciu19.autobidder.api.user.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final UserService userService;
    private final AuctionService auctionService;
    private final MediaAssetMapper mediaAssetMapper;

    public AuctionController(
            UserService userService,
            AuctionService auctionService,
            MediaAssetMapper mediaAssetMapper) {
        this.userService = userService;
        this.auctionService = auctionService;
        this.mediaAssetMapper = mediaAssetMapper;
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

    @PatchMapping
    public ResponseEntity<?> updateAuction(
            @RequestBody AuctionUpdateDto request,
            @AuthenticationPrincipal Jwt jwt) {
        User currentUser = userService.getCurrentUser();

        auctionService.updateAuction(request, currentUser);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/publish")
    public ResponseEntity<?> publishAuction(
            @RequestBody AuctionPublishDto dto,
            @AuthenticationPrincipal Jwt jwt) {
        User currentUser = userService.getCurrentUser();

        auctionService.publishAction(dto, currentUser);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAuction(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        User currentUser = userService.getCurrentUser();

        auctionService.cancelAuction(id, currentUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("engine/{engineId}/prices/recommendation")
    public ResponseEntity<PriceRecommendationDto> getPriceRecommendationForVehicle(@PathVariable UUID engineId) {
        PriceRecommendationDto result = auctionService.getPriceRecommendationForVehicle(engineId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/similar")
    public ResponseEntity<List<AuctionSummaryDto>> getSimilarCars(@PathVariable UUID id) {
        List<AuctionSummaryDto> similarAuctions = auctionService.findSimilarActiveAuctions(id);
        return ResponseEntity.ok(similarAuctions);
    }

    @PostMapping("/{id}/media")
    public ResponseEntity<MediaAssetDto> addMediaToAuction(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("fileType") FileType fileType,
            @RequestParam("displayOrder") Integer displayOrder) {

        try {
            User currentUser = userService.getCurrentUser();
            MediaAsset newAsset = auctionService.addMediaToAuction(id, currentUser, file, title, fileType, displayOrder);
            MediaAssetDto responseDto = mediaAssetMapper.toDto(newAsset);

            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IOException e) {
            throw new FileUploadFailedException("Failed to upload file to cloud storage.", e);
        }
    }

    @DeleteMapping("/{id}/media/{mediaAssetId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMediaToAuction(
            @PathVariable UUID id,
            @PathVariable Long mediaAssetId) throws IOException {
        User currentUser = userService.getCurrentUser();
        auctionService.removeMediaAssetFromAuction(id, mediaAssetId, currentUser);
    }
}
