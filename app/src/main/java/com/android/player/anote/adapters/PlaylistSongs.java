package com.android.player.anote.adapters;

import android.content.Context;
import android.content.DialogInterface;
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

import com.android.player.anote.adapters.AddToPlaylist;
import com.android.player.anote.R;
import com.android.player.anote.Song;
import com.android.player.anote.sqllite.DatabaseHelper;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PlaylistSongs extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Song> playlistSongs,allmysongs;
    ExoPlayer exoPlayer;
    Context context;
    ConstraintLayout playerView;
    List<Integer> nos;
    String namee;
    androidx.appcompat.widget.Toolbar search;
    AppBarLayout menubar;
    AppBarLayout sortingbar;
    ImageView closeView;
    List<Song> songs = new ArrayList<>();
    DatabaseHelper helper;
    TextView addtoplaylist,songtotal,sorticon,playlisttotal,playlisticon,remove,deleteplaylist;
    RecyclerView recyclerView;
    BottomSheetBehavior bottomSheetBehavior;
    ConstraintLayout bottomsheet,holder1;
    String songtittle;
    int pos;

    public PlaylistSongs(List<Song> playlistSongs, ExoPlayer exoPlayer, Context context, List<Integer> nos, String namee,
                         Toolbar search, AppBarLayout menubar, AppBarLayout sortingbar, ImageView closeView,
                         TextView addtoplaylist,TextView songtotal,TextView sorticon,TextView playlisttotal,TextView playlisticon,
                         RecyclerView recyclerView,List<Song> allmysongs, ConstraintLayout bottomsheet,ConstraintLayout holder,
                         TextView remove,TextView deleteplaylist) {
        this.playlistSongs = playlistSongs;
        this.exoPlayer = exoPlayer;
        this.context = context;
        this.nos = nos;
        this.namee = namee;
        this.search = search;
        this.menubar=menubar;
        this.sortingbar = sortingbar;
        this.closeView = closeView;
        this.addtoplaylist = addtoplaylist;
        this.songtotal = songtotal;
        this.sorticon=sorticon;
        this.playlisticon = playlisticon;
        this.playlisttotal = playlisttotal;
        this.recyclerView = recyclerView;
        this.allmysongs = allmysongs;
        this.bottomsheet = bottomsheet;
        this.holder1 = holder;
        this.remove = remove;
        this.deleteplaylist= deleteplaylist;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_songs,parent,false);
        return new PlaylistSongsViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Song song =playlistSongs.get(position);
        Integer no = nos.get(position);
        PlaylistSongsViewholder viewHolder = (PlaylistSongsViewholder) holder;
        songtotal.setVisibility(View.GONE);
        sorticon.setVisibility(View.GONE);
        playlisttotal.setVisibility(View.GONE);
        playlisticon.setVisibility(View.GONE);
        addtoplaylist.setVisibility(View.VISIBLE);
        deleteplaylist.setVisibility(View.GONE);


        if(playlistSongs.size()==0){
            songtotal.setVisibility(View.GONE);
            sorticon.setVisibility(View.GONE);
            playlisttotal.setVisibility(View.GONE);
            playlisticon.setVisibility(View.GONE);
            addtoplaylist.setVisibility(View.VISIBLE);
        }




        addtoplaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.setVisibility(View.GONE);
                sortingbar.setVisibility(View.GONE);
                closeView.setVisibility(View.VISIBLE);
                menubar.setVisibility(View.GONE);



                AddToPlaylist addToPlaylist = new AddToPlaylist(allmysongs,context,playlistSongs,namee);
                recyclerView.setAdapter(addToPlaylist);

            }
        });



        //set Values to views
        viewHolder.titleHolder.setText(song.getTitle());
        viewHolder.durationHolder.setText(getDuration(song.getDuration()));
        viewHolder.sizeHolder.setText(getSize(song.getSize()));
        viewHolder.numberHolder.setText(String.valueOf(no));
        Toast.makeText(context,playlistSongs.size()+" Songs",Toast.LENGTH_SHORT).show();



        viewHolder.itemView.setOnClickListener(view ->{


            //playing
            if(!exoPlayer.isPlaying()){
                exoPlayer.setMediaItems(getMediaItems(),position,0);

            }else{
                exoPlayer.pause();
                exoPlayer.setMediaItems(getMediaItems(),position,0);
//              player.seekTo(position,0);
            }

            //prepare and play song
            exoPlayer.prepare();
            exoPlayer.play();
            Toast.makeText(context,song.getTitle(),Toast.LENGTH_SHORT).show();});

        //show player view
//             playerView.setVisibility(View.VISIBLE);

        viewHolder.options.setOnClickListener( view -> {
            pos =position;
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.bottomsheet);

            ConstraintLayout removefromplaylist = bottomSheetDialog.findViewById(R.id.removeSong);
            bottomSheetDialog.show();
            removefromplaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new AlertDialog.Builder(context)
                            .setTitle("Remove from playlist")
                            .setMessage("Do you want to remove the song from the playlist?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    DatabaseHelper db = new DatabaseHelper(context);
                                    db.removeSong(song.getTitle());
                                    Toast.makeText(context,"removed "+song.getTitle(),Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                    playlistSongs.remove(song);
                                    notifyItemRemoved(pos);
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();
                }
            });

        });




    }



    @Override
    public int getItemCount() {
        return playlistSongs.size();
    }

    public String getplaylistname(){
        return namee;
    }

    public static class PlaylistSongsViewholder extends RecyclerView.ViewHolder{
        TextView titleHolder, durationHolder, sizeHolder, numberHolder,options;

        public PlaylistSongsViewholder(@NonNull View itemView) {
            super(itemView);
            titleHolder = itemView.findViewById(R.id.titleView1);
            durationHolder = itemView.findViewById(R.id.durationView1);
            sizeHolder = itemView.findViewById(R.id.sizeView1);
            numberHolder = itemView.findViewById(R.id.songNumber1);
            options = itemView.findViewById(R.id.optionsMenu1);
        }
    }

    private String getDuration(int totalDuration){
        String totalDurationtext;

        int hrs = totalDuration/(1000*60*60);
        int min = (totalDuration%(1000*60*60))/(1000*60);
        int secs = (((totalDuration%(1000*60*60))%(1000*60*60))%(1000*60))/1000;

        if(hrs<1){
            totalDurationtext = String.format("%02d:%02d",min,secs);

        }else{
            totalDurationtext = String.format("%1d:%02d:%02d",hrs,min,secs);

        }

        return totalDurationtext;
    }

    private String getSize(long bytes){
        String hrsize;

        double k = bytes/1024.0;
        double m = ((bytes/1024.0)/1024.0);
        double g = (((bytes/1024.0)/1024.0)/1024.0);
        double t = ((((bytes/1024.0)/1024.0)/1024.0)/1024.0);

        //the format
        DecimalFormat dec = new DecimalFormat("0.00");

        if(t>1){
            hrsize = dec.format(t).concat(" TB");

        }else if(g>1){
            hrsize = dec.format(g).concat(" GB");
        }else if(m>1) {
            hrsize = dec.format(m).concat(" MB");
        }else if(k>1){
            hrsize = dec.format(k).concat(" KB");
        }
        else{
            hrsize = dec.format(g).concat("Bytes");
        }

        return  hrsize;


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
