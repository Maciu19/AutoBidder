package com.maciu19.autobidder.api.auction.service;

import com.maciu19.autobidder.api.auction.dto.AuctionResponseDto;
import com.maciu19.autobidder.api.auction.dto.AuctionSummaryDto;
import com.maciu19.autobidder.api.auction.dto.CreateAuctionRequest;
import com.maciu19.autobidder.api.auction.model.FileType;
import com.maciu19.autobidder.api.auction.model.MediaAsset;
import com.maciu19.autobidder.api.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface AuctionService {

    List<AuctionSummaryDto> getUserAuctions(User currentUser);

    List<AuctionSummaryDto> getActiveAuctions();

    AuctionResponseDto getAuctionById(UUID auctionId);

    AuctionResponseDto createAuction(CreateAuctionRequest request, User seller);

    MediaAsset addMediaToAuction(UUID auctionId,
                                 User currentUser,
                                 MultipartFile file,
                                 String title,
                                 FileType fileType,
                                 Integer displayOrder) throws IOException;

    void removeMediaAssetFromAuction(UUID auctionId, Long mediaAssetId, User currentUser) throws IOException;
}
