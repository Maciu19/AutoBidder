package com.maciu19.autobidder.api.auction.repository;

import com.maciu19.autobidder.api.auction.model.MediaAsset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, Long> {
}
