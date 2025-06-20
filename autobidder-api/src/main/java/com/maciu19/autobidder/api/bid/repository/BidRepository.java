package com.maciu19.autobidder.api.bid.repository;

import com.maciu19.autobidder.api.bid.model.Bid;
import com.maciu19.autobidder.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {

    @Query("select b from Bid b where b.user = ?1")
    List<Bid> findByUser(User user);
}
