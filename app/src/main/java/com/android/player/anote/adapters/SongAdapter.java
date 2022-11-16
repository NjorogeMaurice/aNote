package com.android.player.anote.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.player.anote.MainActivity;
import com.android.player.anote.PlayerService;
import com.android.player.anote.R;
import com.android.player.anote.Song;
import com.android.player.anote.sqllite.DatabaseHelper;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //members
    Context context;
    List<Song> songs;
    ExoPlayer player;
    ConstraintLayout PlayerView;
    ConstraintLayout ls;
    List<Integer> nos;
    int pos;
    List<Song> queue = new ArrayList<>();
    List<Song> playnowlist = new ArrayList<>();
    BottomSheetDialog bottom;
    BottomSheetDialog bot;
    List<Song> allsongs = new ArrayList<>();
    DatabaseHelper databaseHelper;

    //Constructor


    public SongAdapter(Context context, List<Song> songs,ExoPlayer player,ConstraintLayout playerview, List<Integer> nos, BottomSheetDialog bottom,BottomSheetDialog bot) {
        this.context = context;
        this.songs = songs;
        this.player = player;
        this.PlayerView = playerview;
        this.nos = nos;
        this.bottom = bottom;
        this.bot = bot;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_list_view,parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Current song and view holder
        Song song =songs.get(position);
        Integer no = nos.get(position);
        SongViewHolder viewHolder = (SongViewHolder) holder;

        //set Values to views
        viewHolder.titleHolder.setText(song.getTitle());
        viewHolder.durationHolder.setText(getDuration(song.getDuration()));
        viewHolder.sizeHolder.setText(getSize(song.getSize()));
        viewHolder.numberHolder.setText(String.valueOf(no));

        //artwork
        Uri artworkUri=song.getArtworkUri();
//            viewHolder.artworkHolder.setImageURI(artworkUri);

//            if(viewHolder.artworkHolder.getDrawable() == null){
//                viewHolder.artworkHolder.setImageResource(R.drawable.ic_audiotrack);
//
//            }





        //play song on item click

        viewHolder.itemView.setOnClickListener(view ->{
            context.startService(new Intent(context.getApplicationContext(), PlayerService.class));

            //playing
            if(!player.isPlaying()){
                player.setMediaItems(getMediaItems(),position,0);


            }else{
              player.pause();
              player.setMediaItems(getMediaItems(),position,0);
//              player.seekTo(position,0);
            }

            //prepare and play song
            player.prepare();
            player.play();
            databaseHelper = new DatabaseHelper(context);
            int timesPlayed = databaseHelper.getTimesPlayed(song.getTitle());
            int newNo = timesPlayed+1;
            Toast.makeText(context,song.getTitle(),Toast.LENGTH_SHORT).show();
            databaseHelper.addToPlayed(song.getTitle(),newNo);
        });

             //show player view
//             playerView.setVisibility(View.VISIBLE);
        viewHolder.options.setOnClickListener(view -> {
            pos = position;
            showBottomsheetdialog();
        });


    }

    private void showBottomsheetdialog() {

        databaseHelper = new DatabaseHelper(context);
        ConstraintLayout playnow = bottom.findViewById(R.id.play_now_layout);
        ConstraintLayout playnext = bottom.findViewById(R.id.play_next_layout);
        ConstraintLayout setAsRingtone = bottom.findViewById(R.id.set_ringtone_layout);
        ConstraintLayout addToPlaylist = bottom.findViewById(R.id.add_playlist_layout);
        ConstraintLayout addToFavourite = bottom.findViewById(R.id.add_favourite_layout);
        ConstraintLayout deleteSong = bottom.findViewById(R.id.song_delete_layout);
        ConstraintLayout fileInfo = bottom.findViewById(R.id.file_info_layout);
        ConstraintLayout shareSong = bottom.findViewById(R.id.file_share_layout);
        if(databaseHelper.ifAddedtoFav(songs.get(pos).getTitle())){
            TextView imageView = bottom.findViewById(R.id.add_favourite);
            imageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_already_added_to_favorites,0,0,0);
            TextView textView = bottom.findViewById(R.id.add_favourite_text);
            textView.setText("Remove from Favorites");
        }
        else{
            TextView imageView = bottom.findViewById(R.id.add_favourite);
            imageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_to_favorite,0,0,0);
            TextView textView = bottom.findViewById(R.id.add_favourite_text);
            textView.setText("Add to Favorites");
        }

        bottom.show();


        playnow.setOnClickListener(view -> {
            playnowlist.clear();
            playnowlist.add(songs.get(pos));
            if(!player.isPlaying()){
                player.setMediaItems(playnowMediaItems(),0,0);


            }else{
                player.pause();
                player.setMediaItems(playnowMediaItems(),0,0);
//              player.seekTo(position,0);
            }

            //prepare and play song
            player.prepare();
            player.play();
            bottom.dismiss();
        });

        playnext.setOnClickListener(view -> {
            queue.clear();
            queue.add(songs.get(pos));
            if(!player.isPlaying()){
                player.setMediaItems(getMediaItems(),pos,0);

            }else{
                allsongs.clear();
                allsongs.addAll(songs);
                int currentSong = player.getCurrentMediaItemIndex();
                Song song1 = songs.get(pos);
                allsongs.add(player.getCurrentMediaItemIndex()+1,song1);
                player.setMediaItems(songqueueMediaItems(),currentSong,player.getCurrentPosition());
//              player.seekTo(position,0);
            }

            //prepare and play song
            player.prepare();
            player.play();
            bottom.dismiss();
        });

        addToFavourite.setOnClickListener(view -> {
            DatabaseHelper helper = new DatabaseHelper(context);
           if(songs.get(pos).isAddedToFavourites()){
               songs.get(pos).addedOrRemovedFromFavorites();
               helper.removeFromFav(songs.get(pos).getTitle());
               helper.removeSong(songs.get(pos).getTitle());
               TextView imageView = bottom.findViewById(R.id.add_favourite);
               imageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_to_favorite,0,0,0);
               TextView textView = bottom.findViewById(R.id.add_favourite_text);
               textView.setText("Add to Favorites");
               Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
           }else{
               if(helper.addToFavourite(songs.get(pos).getTitle())){
                   songs.get(pos).addedOrRemovedFromFavorites();
                   TextView imageView = bottom.findViewById(R.id.add_favourite);
                   imageView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_already_added_to_favorites,0,0,0);
                   Toast.makeText(context,"Added to favorites",Toast.LENGTH_SHORT).show();
                   TextView textView = bottom.findViewById(R.id.add_favourite_text);
                   textView.setText("Remove from Favorites");
                   helper.addSong(songs.get(pos).getTitle(),"Favorites");
               }else{
                   Toast.makeText(context, "Unsuccessfull", Toast.LENGTH_SHORT).show();
               }
           }
        });
        fileInfo.setOnClickListener(view -> {
            bottom.dismiss();
            TextView title = bot.findViewById(R.id.songTitletext);
            TextView album = bot.findViewById(R.id.songAlbumtext1);
            TextView artist = bot.findViewById(R.id.songArtisttext1);
            TextView size = bot.findViewById(R.id.songSizetext1);
            TextView duration = bot.findViewById(R.id.songDurationText1);

            title.setText(songs.get(pos).getTitle());
            album.setText(songs.get(pos).getAlbumname());
            artist.setText(songs.get(pos).getArtistname());
            size.setText(getSize(songs.get(pos).getSize()));
            duration.setText(getDuration(songs.get(pos).getDuration()));
            bot.show();
        });

        shareSong.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("audio/*");
            intent.putExtra(Intent.EXTRA_STREAM,songs.get(pos).getUri());
            context.startActivity(Intent.createChooser(intent,"Share via"));
        });


    }

    private List<MediaItem> getMediaItems() {
          //define a list of media items
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Song song: songs
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
    private List<MediaItem> songqueueMediaItems() {
        //define a list of media items
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Song song: allsongs
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

    private List<MediaItem> queueMediaItems() {
        //define a list of media items
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Song song: queue
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
    private List<MediaItem> playnowMediaItems() {
        //define a list of media items
        List<MediaItem> mediaItems = new ArrayList<>();
        for (Song song: playnowlist
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

    //ViewHolderClass
    public static class SongViewHolder extends RecyclerView.ViewHolder{

        //members
//        ImageView artworkHolder;
        TextView titleHolder, durationHolder, sizeHolder, numberHolder,songTotal,options;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
//            artworkHolder = itemView.findViewById(R.id.artworkView);
            titleHolder = itemView.findViewById(R.id.titleView);
            durationHolder = itemView.findViewById(R.id.durationView);
            sizeHolder = itemView.findViewById(R.id.sizeView);
            numberHolder = itemView.findViewById(R.id.songNumber);
            options = itemView.findViewById(R.id.optionsMenu);
        }
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    //search results
    public void filterSongs(List<Song> filteredSongs){
        songs = filteredSongs;
        notifyDataSetChanged();
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

    //size
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

}
