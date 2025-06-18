package com.maciu19.autobidder.api.bid.mapper;

import com.maciu19.autobidder.api.bid.dto.BidDto;
import com.maciu19.autobidder.api.bid.model.Bid;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BidMapper {

    public BidDto toDto(Bid bid) {
        if (bid == null) {
            return null;
        }

        return new BidDto(
                bid.getId(),
                bid.getAuction().getId(),
                bid.getUser().getId(),
                bid.getBidAmount(),
                bid.getCreatedDate(),
                bid.getLastModifiedDate()
        );
    }

    public List<BidDto> toListDto(List<Bid> bids) {
        return bids.stream().map(this::toDto).toList();
    }
}
