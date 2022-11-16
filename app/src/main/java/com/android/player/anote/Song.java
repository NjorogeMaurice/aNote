package com.android.player.anote;

import android.net.Uri;

public class Song {
    //Members

    String title;
    Uri uri;
    Uri artworkUri;
    int size;
    int duration;
    String albumname;
    String artistname;
    int artistid,albumid;
    int magnitude;
    boolean addedToFavourites;
   //Constructor
    public Song(String title, Uri uri, Uri artworkUri, int size, int duration,String albumname,String artistname, int albumid,int artistid,int magnitude,boolean addedToFavourites ) {
        this.title = title;
        this.uri = uri;
        this.size = size;
        this.artworkUri = artworkUri;
        this.duration = duration;
        this.albumname = albumname;
        this.artistname = artistname;
        this.artistid = artistid;
        this.albumid = albumid;
        this.magnitude = magnitude;
        this.addedToFavourites =addedToFavourites;
    }



    //getter

    public void increment(){
        magnitude++;
    }

    public int getMagnitude() {
        return magnitude;
    }

    public void addedOrRemovedFromFavorites(){
        addedToFavourites=!addedToFavourites;
    }

    public boolean isAddedToFavourites() {
        return addedToFavourites;
    }

    public String getTitle() {
        return title;
    }

    public Uri getUri() {
        return uri;
    }

    public Uri getArtworkUri() {
        return artworkUri;
    }

    public int getSize() {
        return size;
    }

    public int getDuration() {
        return duration;
    }

    public String getAlbumname() {
        return albumname;
    }

    public String getArtistname() {
        return artistname;
    }

    public int getArtistid() {
        return artistid;
    }

    public int getAlbumid() {
        return albumid;
    }



}
