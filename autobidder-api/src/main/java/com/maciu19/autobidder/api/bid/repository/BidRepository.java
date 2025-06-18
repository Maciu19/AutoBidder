package com.maciu19.autobidder.api.bid.repository;

import com.maciu19.autobidder.api.bid.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
}
