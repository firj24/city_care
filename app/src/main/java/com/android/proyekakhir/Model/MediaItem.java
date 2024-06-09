package com.android.proyekakhir.Model;

import android.net.Uri;

public class MediaItem {
    private String mediaPath;
    private String mediaType;

    public MediaItem(Uri mediaPath, String mediaType) {
        this.mediaPath = mediaPath.toString();
        this.mediaType = mediaType;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    public enum MediaType {
        IMAGE,
        VIDEO
    }
}

