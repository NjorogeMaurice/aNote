package com.android.player.anote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.player.anote.R;
import com.android.player.anote.Song;
import com.android.player.anote.sqllite.DatabaseHelper;
import com.google.android.material.appbar.AppBarLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddToPlaylist extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Song> allmysongs;
    List<Song> addedSongs = new ArrayList<>();
    Context context;
    List<Song> existingSongs;
    String playlistname;

    public AddToPlaylist(List<Song> allmysongs,Context context, List<Song> existingSongs,String playlistname) {
        this.allmysongs = allmysongs;
        this.context = context;
        this.existingSongs=existingSongs;
        this.playlistname = playlistname;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_to_playlist,parent,false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DatabaseHelper helper = new DatabaseHelper(context);
        Song songss = allmysongs.get(position);
        ListViewHolder listViewHolder = (ListViewHolder) holder;
        listViewHolder.songname.setText(songss.getTitle());

        listViewHolder.checkadd.setOnClickListener( view -> {
            if(existingSongs.size() == 0){
                listViewHolder.checkadd.setBackgroundResource(R.drawable.check_add);
                addedSongs.add(songss);
                Toast.makeText(context,addedSongs.size()+" song(s) added",Toast.LENGTH_SHORT).show();

            }else{
                existingSongs.add(songss);
                listViewHolder.checkadd.setBackgroundResource(R.drawable.check_add);
                helper.addSong(songss.getTitle(),playlistname);
                Toast.makeText(context,existingSongs.size()+" song(s) added to "+playlistname,Toast.LENGTH_SHORT).show();
            }
        });

        listViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                for(Song song: addedSongs){
//                    if(song.getTitle().equalsIgnoreCase(songss.getTitle())){
//                        Toast.makeText(context,"Song is already added",Toast.LENGTH_SHORT).show();
//                    }
//                    else{
//                        addedSongs.add(songss);
//                        Toast.makeText(context,addedSongs.size()+" song(s) added",Toast.LENGTH_SHORT).show();
//                    }
//                }





            }
        });

    }

    @Override
    public int getItemCount() {
        return allmysongs.size();
    }

    public List<Song> returnAdded(){
            return addedSongs;
    }


    public static class ListViewHolder extends RecyclerView.ViewHolder{
        TextView songname, checkadd;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            songname = itemView.findViewById(R.id.songView);
            checkadd = itemView.findViewById(R.id.addIcon);

        }
    }
}
