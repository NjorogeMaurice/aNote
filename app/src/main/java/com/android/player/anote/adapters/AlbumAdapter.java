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

public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    List<Song> song;
    Context context;
    RecyclerView recyclerView;
    List<String> albumNames;
    List<Integer> albumNo;
    List<Integer> nos;
    List<Song> albumSongs = new ArrayList<>();
    ExoPlayer exoPlayer;
    List<String> uniqueNames = new ArrayList<>();
    List<Integer> number = new ArrayList<>();

    public AlbumAdapter(List<Song> song, Context context, RecyclerView recyclerView, List<String> albumNames, List<Integer> albumNo,
                         ExoPlayer exoPlayer,List<Integer> nos) {
        this.song = song;
        this.context = context;
        this.recyclerView = recyclerView;
        this.albumNames = albumNames;
        this.albumNo = albumNo;
        this.exoPlayer=exoPlayer;
        this.nos= nos;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_adapter,parent,false);
        return new AlbumAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        String names = albumNames.get(position);
        int number = albumNo.get(position);
        AlbumAdapterViewHolder viewHolder = (AlbumAdapterViewHolder) holder;
        viewHolder.foldername.setText(names);
        if(number > 1){
            viewHolder.songcount.setText(number+" songs");
        }
        else{
            viewHolder.songcount.setText(number+" song");
        }


        viewHolder.folder.setOnClickListener(view -> {
            ArtistSongs artistSongs = new ArtistSongs(groupByAlbum(names),context,exoPlayer,nos);
            recyclerView.setAdapter(artistSongs);
        });

    }




    private List<Song> groupByAlbum(String namess) {
        for(Song song: song){
            if(Objects.equals(song.getAlbumname(),namess)){
                albumSongs.add(song);
            }
        }
        return  albumSongs;

    }

    @Override
    public int getItemCount() {
        return albumNames.size();
    }

    public static  class  AlbumAdapterViewHolder extends  RecyclerView.ViewHolder{
        TextView foldername,songcount;
        ImageView folder;

        public AlbumAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            folder= itemView.findViewById(R.id.albumfolder);
            foldername = itemView.findViewById(R.id.albumfolderNameView);
            songcount = itemView.findViewById(R.id.albumsongcountView);
        }
    }
}
