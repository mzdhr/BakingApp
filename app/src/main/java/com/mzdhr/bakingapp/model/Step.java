package com.mzdhr.bakingapp.model;

import org.parceler.Parcel;

/**
 * Created by mohammad on 11/03/2018.
 */
@Parcel
public class Step {
    String id;
    String shortDescription;
    String description;
    String videoURL;
    String thumbnailURL;

    public Step(){}

    public String getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }
}
