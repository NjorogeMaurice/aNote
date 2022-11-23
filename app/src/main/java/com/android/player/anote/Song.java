package com.android.player.anote;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {
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

    protected Song(Parcel in) {
        title = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
        artworkUri = in.readParcelable(Uri.class.getClassLoader());
        size = in.readInt();
        duration = in.readInt();
        albumname = in.readString();
        artistname = in.readString();
        artistid = in.readInt();
        albumid = in.readInt();
        magnitude = in.readInt();
        addedToFavourites = in.readByte() != 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeParcelable(uri, i);
        parcel.writeParcelable(artworkUri, i);
        parcel.writeInt(size);
        parcel.writeInt(duration);
        parcel.writeString(albumname);
        parcel.writeString(artistname);
        parcel.writeInt(artistid);
        parcel.writeInt(albumid);
        parcel.writeInt(magnitude);
        parcel.writeByte((byte) (addedToFavourites ? 1 : 0));
    }
}
