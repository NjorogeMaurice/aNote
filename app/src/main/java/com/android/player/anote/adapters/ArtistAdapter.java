package com.android.player.anote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.player.anote.R;
import com.android.player.anote.Song;
import com.google.android.exoplayer2.ExoPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArtistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<Song> song;
    Context context;
    RecyclerView recyclerView;
    List<String> artistNames;
    List<Integer> artistNo;
    List<Integer> nos;
    List<Song> artistSongs = new ArrayList<>();
    ExoPlayer exoPlayer;

    public ArtistAdapter(List<Song> song, Context context, RecyclerView recyclerView, List<String> artistNames, List<Integer> artistNo,
                         ExoPlayer exoPlayer,List<Integer> nos) {
        this.song = song;
        this.context = context;
        this.recyclerView = recyclerView;
        this.artistNames = artistNames;
        this.artistNo = artistNo;
        this.exoPlayer=exoPlayer;
        this.nos= nos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_adapter, parent, false);
        return new ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        String names = artistNames.get(position);
        int number = artistNo.get(position);
        ArtistViewHolder viewHolder = (ArtistViewHolder) holder;
        viewHolder.foldername.setText(names);
        viewHolder.songcount.setText(number+" songs");

        viewHolder.folder.setOnClickListener(view -> {
            ArtistSongs artistSongs = new ArtistSongs(groupByArtist(names),context,exoPlayer,nos);
            recyclerView.setAdapter(artistSongs);
        });
    }

    private List<Song> groupByArtist(String namess) {
        for(Song song: song){
            if(Objects.equals(song.getArtistname(),namess)){
                artistSongs.add(song);
            }
        }
        return  artistSongs;

    }


    @Override
    public int getItemCount() {
        return artistNames.size();

    }

    public static class ArtistViewHolder extends  RecyclerView.ViewHolder{
         TextView foldername,songcount;
         ImageView folder;
        public ArtistViewHolder(@NonNull View itemView) {
            super(itemView);
            folder= itemView.findViewById(R.id.artistfolder);
            foldername = itemView.findViewById(R.id.artistfolderNameView);
            songcount = itemView.findViewById(R.id.artistsongcountView);
        }
    }
}
