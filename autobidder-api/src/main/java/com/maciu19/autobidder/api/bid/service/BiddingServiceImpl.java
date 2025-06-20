package com.maciu19.autobidder.api.bid.service;

import com.maciu19.autobidder.api.auction.dto.AuctionPriceUpdateDto;
import com.maciu19.autobidder.api.auction.model.Auction;
import com.maciu19.autobidder.api.auction.model.AuctionStatus;
import com.maciu19.autobidder.api.auction.repository.AuctionRepository;
import com.maciu19.autobidder.api.bid.dto.BidRequestDto;
import com.maciu19.autobidder.api.bid.model.Bid;
import com.maciu19.autobidder.api.bid.repository.BidRepository;
import com.maciu19.autobidder.api.exception.exceptions.ForbiddenResourceException;
import com.maciu19.autobidder.api.exception.exceptions.ResourceConflictException;
import com.maciu19.autobidder.api.exception.exceptions.ResourceNotFoundException;
import com.maciu19.autobidder.api.user.mapper.UserMapper;
import com.maciu19.autobidder.api.user.model.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BiddingServiceImpl implements BiddingService {

    private final AuctionRepository auctionRepository;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BidRepository bidRepository;

    public BiddingServiceImpl(
            AuctionRepository auctionRepository,
            UserMapper userMapper,
            SimpMessagingTemplate simpMessagingTemplate,
            BidRepository bidRepository) {
        this.auctionRepository = auctionRepository;
        this.userMapper = userMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.bidRepository = bidRepository;
    }

    @Override
    public void placeBid(BidRequestDto bidRequest, User bidder) {
        Auction auction = auctionRepository.findByIdWithDetails(bidRequest.auctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found with id: " + bidRequest.auctionId()));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
                throw new ResourceConflictException("Bids can only be placed on active auctions.");
        }

        if (auction.getSeller().getId().equals(bidder.getId())) {
            throw new ForbiddenResourceException("Sellers cannot bid on their own auctions.");
        }

        if (bidRequest.amount().compareTo(auction.getCurrentPrice()) <= 0) {
            throw new ResourceConflictException("Bid amount must be greater than the current price.");
        }

        if (auction.getWinningUser() != null && auction.getWinningUser().getId().equals(bidder.getId())) {
            throw new ResourceConflictException("You are already the highest bidder.");
        }

        auction.setCurrentPrice(bidRequest.amount());
        auction.setWinningUser(bidder);

        Bid newBid = Bid.builder()
                .auction(auction)
                .user(bidder)
                .bidAmount(bidRequest.amount())
                .build();

        auction.getBids().add(newBid);
        auctionRepository.save(auction);

        String destination = "/topic/auctions/" + auction.getId();
        AuctionPriceUpdateDto updateDto = new AuctionPriceUpdateDto(
                auction.getId(),
                auction.getCurrentPrice(),
                userMapper.toDto(auction.getWinningUser()),
                auction.getBids().size()
        );
        simpMessagingTemplate.convertAndSend(destination, updateDto);
    }

    @Override
    public List<Bid> getBidsForUser(User currentUser) {
        return bidRepository.findByUser(currentUser);
    }
}
