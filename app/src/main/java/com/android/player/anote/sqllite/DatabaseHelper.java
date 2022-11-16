package com.android.player.anote.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.player.anote.MainActivity;
import com.android.player.anote.Playlist;
import com.android.player.anote.Song;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String PLAYLIST_TABLE = "PLAYLIST_TABLE";
    public static final String PLAYLIST_NAME = "PLAYLIST_NAME";

    public static final String SONG_TABLE = "SONG_TABLE";
    public static final String SONG_NAME = "SONG_NAME";

    public static final String FAVORITE_TABLE = "FAVORITE_TABLE";

    public static final String MOST_PLAYED = "MOST_PLAYED";
    public static final String TIMES_PLAYED = "TIMES_PLAYED";


    public DatabaseHelper(@Nullable Context context) {
        super(context, "playlist.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    //create a table here
     String createTableStatement = "CREATE TABLE "+PLAYLIST_TABLE+"("+PLAYLIST_NAME+" TEXT PRIMARY KEY)";
     db.execSQL(createTableStatement);

     //create second table
        String createTableStatement2 = "CREATE TABLE "+SONG_TABLE+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+SONG_NAME+" TEXT,"+PLAYLIST_NAME+" TEXT, FOREIGN KEY("+PLAYLIST_NAME+") REFERENCES "+PLAYLIST_TABLE+"("+PLAYLIST_NAME+"))";
        db.execSQL(createTableStatement2);

        String createFavoriteTable = "CREATE TABLE "+FAVORITE_TABLE+"(ID INTEGER PRIMARY KEY AUTOINCREMENT,"+SONG_NAME+" TEXT)";
        db.execSQL(createFavoriteTable);

        String createMostPlayedTable = "CREATE TABLE "+MOST_PLAYED+"("+SONG_NAME+" TEXT PRIMARY KEY, "+TIMES_PLAYED+" INTEGER)";
        db.execSQL(createMostPlayedTable);





    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public boolean addOne(String playlist){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(PLAYLIST_NAME,playlist);
        long insert = db.insert(PLAYLIST_TABLE,null,cv);
        if(insert == -1){
            return false;
        }
        else {
            return true;
        }
    }

    public boolean addSong(String name, String playlistname){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SONG_NAME,name);
        cv.put(PLAYLIST_NAME,playlistname);
        long insert = db.insert(SONG_TABLE,null,cv);
        if(insert == -1){
            return  false;
        }
        else{
            return true;
        }
    }

    public boolean addToFavourite(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SONG_NAME,name);
        long insert = db.insert(FAVORITE_TABLE,null,cv);
        if(insert == -1){
            return false;
        }
        else{
            return true;
        }
    }
    public boolean ifAddedtoFav(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+SONG_NAME+" FROM "+FAVORITE_TABLE
                +" WHERE "+SONG_NAME+" = '"+name+"'";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            return true;
        }
        return false;
    }

    public void removeFromFav(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String name2 = "'"+name+"'";
        String query = "DELETE FROM "+FAVORITE_TABLE+" WHERE "+SONG_NAME+" = "+name2;
        db.execSQL(query);
    }

    public void addToPlayed(String name,int no){
        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db2 = this.getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(SONG_NAME,name);
        cv.put(TIMES_PLAYED, no);
        String query = "SELECT "+SONG_NAME+" FROM "+MOST_PLAYED
                +" WHERE "+SONG_NAME+" = '"+name+"'";

        Cursor cursor = db2.rawQuery(query,null);
        if(cursor.moveToFirst()){
            //Do an update
            db.update(MOST_PLAYED,cv,SONG_NAME+"=?",new String[]{name});

        }
        else{
            db.insert(MOST_PLAYED,null,cv);
        }

        cursor.close();
        db.close();
        db2.close();

    }

   public int getTimesPlayed(String name){
        int timesPlayed = 0;
       SQLiteDatabase db = this.getReadableDatabase();
       String query = "SELECT "+TIMES_PLAYED+" FROM "+MOST_PLAYED+" WHERE "+SONG_NAME+"= "+"'"+name+"'";
       Cursor cursor = db.rawQuery(query,null);
       if(cursor.moveToFirst()){
           do{
               timesPlayed = cursor.getInt(0);
           }while (cursor.moveToNext());
       }

       cursor.close();
       db.close();
       return  timesPlayed;
   }

    public List<String> getPlayedSongs(){
        List<String> playedSongs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT "+SONG_NAME+" FROM "+MOST_PLAYED+" ORDER BY "+TIMES_PLAYED+" DESC";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                String song = cursor.getString(0);
                playedSongs.add(song);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return  playedSongs;
    }

    public List<Playlist> getPlaylists(){
          List<Playlist> returnList = new ArrayList<>();

          String query = "SELECT * FROM "+PLAYLIST_TABLE;
          SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do {
                String playlistName = cursor.getString(0);
                Playlist playlist = new Playlist(playlistName);
                returnList.add(playlist);

            }while(cursor.moveToNext());
        }
        else{

        }

        cursor.close();
        db.close();
        return returnList;
    }

    public List<String> getSongs(String playlistname){
        List<String> returnSong = new ArrayList<>();

        String query = "SELECT "+SONG_NAME+" FROM "+SONG_TABLE
                +" WHERE "+PLAYLIST_NAME+" = "+playlistname;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            do{
                String songname = cursor.getString(0);
                returnSong.add(songname);
            }while (cursor.moveToNext());
        }

        return  returnSong;
    }


    public void removeSong(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String name2 = "'"+name+"'";
        String query = "DELETE FROM "+SONG_TABLE+" WHERE "+SONG_NAME+" = "+name2;
        db.execSQL(query);
    }

    public void removePlaylist(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String name2 = "'"+name+"'";
        String query1 = "DELETE FROM "+ PLAYLIST_TABLE+" WHERE "+PLAYLIST_NAME+" = "+name2;
        String query = "DELETE FROM "+SONG_TABLE+" WHERE "+PLAYLIST_NAME+" = "+name2;
        db.execSQL(query);
        db.execSQL(query1);
    }




}
