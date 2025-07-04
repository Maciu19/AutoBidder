package com.maciu19.autobidder.api.bid.service;

import com.maciu19.autobidder.api.auction.dto.AuctionPriceUpdateDto;
import com.maciu19.autobidder.api.auction.model.Auction;
import com.maciu19.autobidder.api.auction.model.AuctionStatus;
import com.maciu19.autobidder.api.auction.repository.AuctionRepository;
import com.maciu19.autobidder.api.bid.dto.BidDto;
import com.maciu19.autobidder.api.bid.dto.BidPlacedEvent;
import com.maciu19.autobidder.api.bid.dto.BidRequestDto;
import com.maciu19.autobidder.api.bid.mapper.BidMapper;
import com.maciu19.autobidder.api.bid.model.Bid;
import com.maciu19.autobidder.api.bid.repository.BidRepository;
import com.maciu19.autobidder.api.config.RabbitMQConfig;
import com.maciu19.autobidder.api.exception.exceptions.ForbiddenResourceException;
import com.maciu19.autobidder.api.exception.exceptions.ResourceConflictException;
import com.maciu19.autobidder.api.exception.exceptions.ResourceNotFoundException;
import com.maciu19.autobidder.api.notification.service.NotificationService;
import com.maciu19.autobidder.api.user.mapper.UserMapper;
import com.maciu19.autobidder.api.user.model.User;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
public class BiddingServiceImpl implements BiddingService {

    private final AuctionRepository auctionRepository;
    private final UserMapper userMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final BidRepository bidRepository;
    private final BidMapper bidMapper;
    private final RabbitTemplate rabbitTemplate;

    public BiddingServiceImpl(
            AuctionRepository auctionRepository,
            UserMapper userMapper,
            SimpMessagingTemplate simpMessagingTemplate,
            BidRepository bidRepository,
            BidMapper bidMapper,
            RabbitTemplate rabbitTemplate) {
        this.auctionRepository = auctionRepository;
        this.userMapper = userMapper;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.bidRepository = bidRepository;
        this.bidMapper = bidMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional
    public BidDto placeBid(BidRequestDto bidRequest, User bidder) {
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

        User outbidderUser = auction.getWinningUser();

        auction.setCurrentPrice(bidRequest.amount());
        auction.setWinningUser(bidder);

        Bid transientBid = Bid.builder()
                .auction(auction)
                .user(bidder)
                .bidAmount(bidRequest.amount())
                .build();

        Bid savedBid = bidRepository.save(transientBid);

        auction.getBids().add(savedBid);
        auctionRepository.save(auction);

        BidDto bidDto = bidMapper.toDto(savedBid);

        BidPlacedEvent event = new BidPlacedEvent(
                auction.getSeller().getId(),
                (outbidderUser != null) ? outbidderUser.getId() : null,
                bidRequest.amount(),
                auction.getTitle()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                simpMessagingTemplate.convertAndSend("/topic/auctions/bidUpdate", bidDto);

                rabbitTemplate.convertAndSend(
                        RabbitMQConfig.EXCHANGE_NAME,
                        RabbitMQConfig.ROUTING_KEY_BID_PLACED,
                        event
                );
            }
        });

        return bidDto;
    }

    @Override
    public List<Bid> getBidsForUser(User currentUser) {
        return bidRepository.findByUser(currentUser);
    }
}
