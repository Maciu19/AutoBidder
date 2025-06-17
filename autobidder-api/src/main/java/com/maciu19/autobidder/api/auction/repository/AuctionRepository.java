package com.maciu19.autobidder.api.auction.repository;

import com.maciu19.autobidder.api.auction.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    Optional<Auction> findByVin(String vin);

    @Query("SELECT a FROM Auction a " +
            "JOIN FETCH a.seller " +
            "LEFT JOIN FETCH a.mediaAssets " +
            "JOIN FETCH a.vehicleEngineOption veo " +
            "JOIN FETCH veo.vehicleModelGeneration vmg " +
            "JOIN FETCH vmg.vehicleModel vm " +
            "JOIN FETCH vm.manufacturer m " +
            "WHERE a.id = :id")
    Optional<Auction> findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT a FROM Auction a " +
            "JOIN FETCH a.seller s " +
            "WHERE a.startTime <= :now AND a.endTime >= :now")
    List<Auction> findActiveAuctionsForList(@Param("now") LocalDateTime now);
}
