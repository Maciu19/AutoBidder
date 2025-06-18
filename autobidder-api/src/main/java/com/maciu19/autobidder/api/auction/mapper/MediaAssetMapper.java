package com.maciu19.autobidder.api.auction.mapper;

import com.maciu19.autobidder.api.auction.dto.MediaAssetDto;
import com.maciu19.autobidder.api.auction.model.MediaAsset;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaAssetMapper {

    public MediaAssetDto toDto(MediaAsset mediaAsset) {
        if (mediaAsset == null) {
            return null;
        }

        return new MediaAssetDto(
                mediaAsset.getId(),
                mediaAsset.getAuction().getId(),
                mediaAsset.getFileUrl(),
                mediaAsset.getFileType(),
                mediaAsset.getTitle(),
                mediaAsset.getDisplayOrder(),
                mediaAsset.getCreatedDate()
        );
    }

    public List<MediaAssetDto> toDtoList(List<MediaAsset> mediaAssetList) {
        return mediaAssetList.stream().map(this::toDto).toList();
    }
}
