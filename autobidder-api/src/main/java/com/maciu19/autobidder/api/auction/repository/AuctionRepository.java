package com.maciu19.autobidder.api.auction.repository;

import com.maciu19.autobidder.api.auction.model.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    Optional<Auction> findByVin(String vin);
}
