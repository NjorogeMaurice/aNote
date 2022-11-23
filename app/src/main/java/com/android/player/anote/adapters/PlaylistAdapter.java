package com.android.player.anote.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.player.anote.Playlist;
import com.android.player.anote.PlaylistActivity;
import com.android.player.anote.R;
import com.android.player.anote.Song;
import com.android.player.anote.sqllite.DatabaseHelper;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaylistAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   List<Playlist> playlists;
   Context context;
   DatabaseHelper helper;
   List<Song> allsongs;
   List<Song> playlistSongs = new ArrayList<>();
   RecyclerView recyclerView;
   String namee,name, fav, mostplayed;
   ExoPlayer exoPlayer;
   List<Integer> nos;
   Toolbar search;
   AppBarLayout menubar,sortingbar;
   ImageView closeview;
   TextView addtoPlaylist,songtotal,sorticon,playlisttotal,playlisticon,remove,deleteplaylist;
   String clickedplaylist;
   ConstraintLayout bottomsheet,bottomsheetholder;
   PlaylistActivity playlistActivity;


    public PlaylistAdapter(Context context, List<Playlist> playlists, List<Song> allsongs, RecyclerView recyclerView,
                           ExoPlayer exoPlayer, List<Integer> nos, Toolbar search, AppBarLayout menubar, AppBarLayout sortingbar, ImageView closeview,
                           TextView addtoplaylist,TextView songtotal,TextView sorticon,TextView playlisticon,TextView playlisttotal,
                           ConstraintLayout bottomsheet,ConstraintLayout bottomsheetholder,TextView remove,TextView deleteplaylist) {
        this.playlists = playlists;
        this.context = context;
        this.allsongs = allsongs;
        this.recyclerView = recyclerView;
        this.exoPlayer=exoPlayer;
        this.nos=nos;
        this.search = search;
        this.menubar = menubar;
        this.sortingbar = sortingbar;
        this.closeview=closeview;
        this.addtoPlaylist = addtoplaylist;
        this.songtotal = songtotal;
        this.sorticon=sorticon;
        this.playlisticon = playlisticon;
        this.playlisttotal = playlisttotal;
        this.bottomsheet = bottomsheet;
        this.bottomsheetholder = bottomsheetholder;
        this.remove = remove;
        this.deleteplaylist=deleteplaylist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist,parent,false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        helper = new DatabaseHelper(context);
        Playlist playlist = playlists.get(position);
        namee = "'"+playlist.getPlaylistname()+"'";
        mostplayed = "Most Played";

        PlaylistViewHolder viewHolder = (PlaylistViewHolder) holder;

        viewHolder.folderNameView.setText(playlist.getPlaylistname());
        viewHolder.songCountView.setText(helper.getSongs(namee).size() +" songs");

        viewHolder.itemView.setOnClickListener(view -> {
            namee = "'"+playlist.getPlaylistname()+"'";
            clickedplaylist = playlist.getPlaylistname();

            Intent intent = new Intent(context, PlaylistActivity.class);
            intent.putExtra("playlistName",playlist.getPlaylistname());
            Bundle bundle = new Bundle();
            bundle.putIntegerArrayList("nos", (ArrayList<Integer>) nos);
            bundle.putParcelableArrayList("allsongs", (ArrayList<? extends Parcelable>) allsongs);
            intent.putExtras(bundle);
            context.startActivity(intent);

//
//            if(Objects.equals(playlist.getPlaylistname(), "Favorites")){
//                FavoritesAdapter favoritesAdapter = new FavoritesAdapter(getPlaylistSongs(),context,exoPlayer,nos);
//                recyclerView.setAdapter(favoritesAdapter);
//                playlisticon.setVisibility(View.GONE);
//                playlisttotal.setVisibility(View.GONE);
//                addtoPlaylist.setVisibility(View.GONE);
//            }
//            else if(Objects.equals(playlist.getPlaylistname(), "Most Played")){
//                MostPlayedAdapter mostPlayedAdapter = new MostPlayedAdapter(getPlaylistSongs(),context,exoPlayer,nos);
//                recyclerView.setAdapter((mostPlayedAdapter));
//                playlisticon.setVisibility(View.GONE);
//                playlisttotal.setVisibility(View.GONE);
//                addtoPlaylist.setVisibility(View.GONE);
//            }
//            else{
//                PlaylistSongs playlistSong = new PlaylistSongs(getPlaylistSongs(),exoPlayer,context,nos,clickedplaylist,search,menubar,sortingbar,closeview,addtoPlaylist,songtotal,sorticon,playlisttotal,playlisticon,recyclerView,allsongs,bottomsheet,bottomsheetholder,remove,deleteplaylist);
//                recyclerView.setAdapter(playlistSong);
//                playlisticon.setVisibility(View.GONE);
//                playlisttotal.setVisibility(View.GONE);
//            }

        });


        viewHolder.itemView.setOnLongClickListener(view -> {

            name = playlist.getPlaylistname();
            if(Objects.equals(name,"Most Played")){
                Toast.makeText(context, "Most Played", Toast.LENGTH_SHORT).show();
            }
            else if(Objects.equals(name,"Favorites")){
                Toast.makeText(context, "Favorites", Toast.LENGTH_SHORT).show();
            }
            else {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.playlist_bottomsheet);
                ConstraintLayout removeplaylist = bottomSheetDialog.findViewById(R.id.removePlaylist);
                ConstraintLayout renameplaylist = bottomSheetDialog.findViewById(R.id.renamingPlaylist);
                bottomSheetDialog.show();
                removeplaylist.setOnClickListener(view1 -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Remove Playlist")
                            .setMessage("Delete the playlist?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHelper helper = new DatabaseHelper(context);
                                    Toast.makeText(context, namee, Toast.LENGTH_SHORT).show();
                                    helper.removePlaylist(name);
                                    for (Playlist playlist1 : playlists) {
                                        if (Objects.equals(playlist1.getPlaylistname(), name)) {
                                            playlists.remove(playlist1);
                                            notifyItemRemoved(position);
                                            playlisttotal.setText("Playlists(" + playlists.size() + ")");
                                        }
                                    }
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();

                });
            }

            return true;
        });

        viewHolder.playfolder.setOnClickListener(view -> {
            namee = "'"+playlist.getPlaylistname()+"'";
            playlistSongs = getPlaylistSongs();
            if(!exoPlayer.isPlaying()){
                exoPlayer.setMediaItems(getMediaItems(),0,0);


            }else{
                exoPlayer.pause();
                exoPlayer.setMediaItems(getMediaItems(),0,0);
//              player.seekTo(position,0);
            }
            //prepare and play song
            exoPlayer.prepare();
            exoPlayer.play();

        });



    }

    @Override
    public int getItemCount() {
        return playlists.size();
    }

    public String getClickedplaylist(){
        return name;
    }

    private List<Song> getPlaylistSongs() {
        List<Song> thisplaylist = new ArrayList<>();
        helper = new DatabaseHelper(context);
        for(String s: helper.getSongs(namee)){
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





    public static class PlaylistViewHolder extends RecyclerView.ViewHolder{

        TextView folderNameView, songCountView,playfolder;
        ImageView folder;

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);

            folderNameView = itemView.findViewById(R.id.folderNameView);
            songCountView = itemView.findViewById(R.id.songcountView);
            folder = itemView.findViewById(R.id.folderImage);
            playfolder = itemView.findViewById(R.id.playfolder);
        }
    }

    private List<MediaItem> getMediaItems() {
        //define a list of media items
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Song song: playlistSongs
        ) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getUri())
                    .setMediaMetadata(getMetaData(song))
                    .build();

            //add the media item to media items list
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    private MediaMetadata getMetaData(Song song) {

        return new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtworkUri(song.getArtworkUri())
                .build();
    }
}
