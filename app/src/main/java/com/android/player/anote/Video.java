package com.android.player.anote;

import android.os.Parcel;
import android.os.Parcelable;

public class Video implements Parcelable {
    private String id;
    private String title;
    private String displayName;
    private String size;
    private String duration;
    private String path;
    private String dateAdded;

    public Video(String id, String title, String displayName, String size, String duration, String path, String dateAdded) {
        this.id = id;
        this.title = title;
        this.displayName = displayName;
        this.size = size;
        this.duration = duration;
        this.path = path;
        this.dateAdded = dateAdded;
    }

    protected Video(Parcel in) {
        id = in.readString();
        title = in.readString();
        displayName = in.readString();
        size = in.readString();
        duration = in.readString();
        path = in.readString();
        dateAdded = in.readString();
    }

    public static final Creator<Video> CREATOR = new Creator<Video>() {
        @Override
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        @Override
        public Video[] newArray(int size) {
            return new Video[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSize() {
        return size;
    }

    public String getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(displayName);
        parcel.writeString(size);
        parcel.writeString(duration);
        parcel.writeString(path);
        parcel.writeString(dateAdded);
    }
}
