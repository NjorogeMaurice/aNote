package com.android.player.anote.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.player.anote.R;
import com.android.player.anote.Song;
import com.android.player.anote.sqllite.DatabaseHelper;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Song> favoriteSongs;
    Context context;
    ExoPlayer exoPlayer;
    List<Integer> nos;
    int pos;

    public FavoritesAdapter(List<Song> favoriteSongs, Context context, ExoPlayer exoPlayer, List<Integer> nos) {
        this.favoriteSongs = favoriteSongs;
        this.context = context;
        this.exoPlayer = exoPlayer;
        this.nos = nos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_layout,parent,false);
        return new FavoritesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Song song = favoriteSongs.get(position);
        Integer no = nos.get(position);
        FavoritesViewHolder viewHolder = (FavoritesViewHolder) holder;

        viewHolder.titleHolder.setText(song.getTitle());
        viewHolder.durationHolder.setText(getDuration(song.getDuration()));
        viewHolder.sizeHolder.setText(getSize(song.getSize()));
        viewHolder.numberHolder.setText(String.valueOf(no));

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



        viewHolder.options.setOnClickListener( view -> {
            pos =position;
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.favorites_bottomsheet);

            ConstraintLayout removefromFavorites = bottomSheetDialog.findViewById(R.id.remove_favourite_layout);
            bottomSheetDialog.show();
            removefromFavorites.setOnClickListener(view1 -> {
                DatabaseHelper db = new DatabaseHelper(context);
                db.removeFromFav(favoriteSongs.get(pos).getTitle());
                db.removeSong(favoriteSongs.get(pos).getTitle());
                Toast.makeText(context,"removed "+song.getTitle(),Toast.LENGTH_SHORT).show();
                favoriteSongs.remove(song);
                notifyItemRemoved(pos);
            });

        });


    }

    @Override
    public int getItemCount() {
        return favoriteSongs.size();
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder{
        TextView titleHolder, durationHolder, sizeHolder, numberHolder,options;
        public FavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            titleHolder = itemView.findViewById(R.id.titleView2);
            durationHolder = itemView.findViewById(R.id.durationView2);
            sizeHolder = itemView.findViewById(R.id.sizeView2);
            numberHolder = itemView.findViewById(R.id.songNumber2);
            options = itemView.findViewById(R.id.optionsMenu2);
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
        for (Song song: favoriteSongs
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
