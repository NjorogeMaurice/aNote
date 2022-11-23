package com.android.player.anote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.player.anote.adapters.FavoritesAdapter;
import com.android.player.anote.adapters.MostPlayedAdapter;
import com.android.player.anote.adapters.PlaylistSongs;
import com.android.player.anote.sqllite.DatabaseHelper;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistActivity extends AppCompatActivity {

    ExoPlayer exoPlayer;
    List<Song> allsongs = new ArrayList<>();
    List<Song> playlistSongs = new ArrayList<>();
    DatabaseHelper helper;
    String name = null;
    TextView songCount, playlist_title;
    RecyclerView recyclerView;
    List<Integer> nos = new ArrayList<>();
    boolean isBound = false;
    FavoritesAdapter favoritesAdapter;
    MostPlayedAdapter mostPlayedAdapter;
    RelativeLayout playlistBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        songCount = findViewById(R.id.playlist_song_count);
        playlist_title = findViewById(R.id.playlist_name);
        recyclerView = findViewById(R.id.playlist_rv);
        helper = new DatabaseHelper(this);
        name = getIntent().getStringExtra("playlistName");
        nos = getIntent().getExtras().getIntegerArrayList("nos");
        playlist_title.setText(name);
        allsongs = getIntent().getExtras().getParcelableArrayList("allsongs");
        playlistSongs = getSongs();
        songCount.setText(playlistSongs.size()+" songs");
        playlistBack = findViewById(R.id.playlist_back);
        playlistBack.setOnClickListener(view -> backClicked());
        doBindingService();


    }

    private void backClicked() {
        finish();
    }


    private void doBindingService() {
        Intent playerServiceIntent = new Intent(getApplicationContext(),PlayerService.class);
        bindService(playerServiceIntent,playerServiceConnection, Context.BIND_AUTO_CREATE);
    }
    ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.ServiceBinder binder = (PlayerService.ServiceBinder) iBinder;
            exoPlayer = binder.getPlayerService().player;
            isBound = true;
            showSongs();


        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void showSongs() {

        if(Objects.equals(name, "Favorites")){
            favoritesAdapter = new FavoritesAdapter(playlistSongs,PlaylistActivity.this,exoPlayer,nos);
            Toast.makeText(PlaylistActivity.this, String.valueOf(playlistSongs.size()), Toast.LENGTH_SHORT).show();
            recyclerView.setAdapter(favoritesAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL,false));
            favoritesAdapter.notifyDataSetChanged();
        }else if(Objects.equals(name, "Most Played")){
            mostPlayedAdapter = new MostPlayedAdapter(playlistSongs,PlaylistActivity.this,exoPlayer,nos);
            Toast.makeText(PlaylistActivity.this, String.valueOf(playlistSongs.size()), Toast.LENGTH_SHORT).show();
            recyclerView.setAdapter(mostPlayedAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL,false));
            mostPlayedAdapter.notifyDataSetChanged();
        }
        else {
            PlaylistSongs playlistSongs = new PlaylistSongs(getSongs(),exoPlayer,PlaylistActivity.this,nos);
            recyclerView.setAdapter(playlistSongs);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL,false));
            playlistSongs.notifyDataSetChanged();
        }
    }

    private List<Song> getSongs() {
        List<Song> playlistSongs = new ArrayList<>();
        playlistSongs = getPlaylistSongs(name);
        return  playlistSongs;
    }

    private List<Song> getPlaylistSongs(String playlistname) {
        List<Song> thisplaylist = new ArrayList<>();
        helper = new DatabaseHelper(PlaylistActivity.this);
        for(String s: helper.getSongs("'"+playlistname+"'")){
            for(Song song: allsongs ){
                if(!Objects.equals(song.getTitle(), s)){

                }
                else {
                    thisplaylist.add(song);
                }
            }
        }
        return thisplaylist;
    }
}