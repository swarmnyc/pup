package com.swarmnyc.pup.module.models;

public interface PicturedModel {
    String getName();

    void setName(String name);

    String getPictureUrl();

    void setPictureUrl(String pictureUrl);

    String getThumbnailPictureUrl();

    void setThumbnailPictureUrl(String thumbnailPictureUrl);
}
