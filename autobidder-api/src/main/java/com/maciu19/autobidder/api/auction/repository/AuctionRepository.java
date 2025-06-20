package com.maciu19.autobidder.api.auction.repository;

import com.maciu19.autobidder.api.auction.model.Auction;
import com.maciu19.autobidder.api.auction.model.AuctionStatus;
import com.maciu19.autobidder.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    Optional<Auction> findByVin(String vin);

    @Query("SELECT a FROM Auction a " +
            "JOIN FETCH a.seller " +
            "LEFT JOIN FETCH a.winningUser " +
            "JOIN FETCH a.vehicleEngineOption veo " +
            "JOIN FETCH veo.vehicleModelGeneration vmg " +
            "JOIN FETCH vmg.vehicleModel vm " +
            "JOIN FETCH vm.manufacturer m " +
            "WHERE a.id = :id")
    Optional<Auction> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT a FROM Auction a " +
            "JOIN FETCH a.seller s " +
            "LEFT JOIN FETCH a.bids " +
            "WHERE a.status = :status")
    List<Auction> findActiveAuctionsForList(@Param("status") AuctionStatus status);

    @Query("SELECT a FROM Auction a " +
            "JOIN FETCH a.seller s " +
            "LEFT JOIN FETCH a.bids " +
            "WHERE s = :user")
    List<Auction> findAuctionByUser(@Param("user") User user);

    List<Auction> findAllByStatusAndEndTimeBefore(AuctionStatus status, LocalDateTime now);
}
