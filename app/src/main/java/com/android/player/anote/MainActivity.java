package com.android.player.anote;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.player.anote.adapters.AddToPlaylist;
import com.android.player.anote.adapters.AlbumAdapter;
import com.android.player.anote.adapters.ArtistAdapter;
import com.android.player.anote.adapters.PlaylistAdapter;
import com.android.player.anote.adapters.SongAdapter;
import com.android.player.anote.sqllite.DatabaseHelper;
import com.android.player.anote.adapters.PlaylistSongs;
import com.chibde.visualizer.BarVisualizer;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.jgabrielfreitas.core.BlurImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    // members
    AppBarLayout playlistadd,menubar,sortingbar;
    PlaylistAdapter playlistAdapter;
    RecyclerView recyclerView,recyclerView1;
    SongAdapter songAdapter;
    List<Song> allsongs = new ArrayList<>();
    List<Playlist> allplaylists  = new ArrayList<>();
    ArrayList<String> savedplaylists = new ArrayList<>();
    ActivityResultLauncher<String> storagePermissionLauncher;
    CharSequence playlistName;
    EditText playlistEnter;
    TextView songNameView,skipPrevBtn,skipNextBtn,playPauseBtn,repeatBtn,playListBtn,playerCloseBtn,songTotal,sorticon,playlistIcon,playlisttotal,addtoplaylist;
    SeekBar seekBar;
    TextView progressView,DurationView;
    CircleImageView Artwork;
    ConstraintLayout headWrapper,artworkWrapper,seekBarWrapper,PlayerView,sortDialog;
    Bundle bundle;
    RadioGroup grp1,grp2;
    RadioButton name,date,atoz,ztoa,newtoold,oldtonew;
    String sorting;
    String sortOrder;
    TextView songname;
    AddToPlaylist addToPlaylist;
    Toolbar search;
    String Playlistname = null;
    List<Song> eachplaylistsong = new ArrayList<>();
    PlaylistSongs playlistSongs;
    ConstraintLayout bottomsheet,bottomsheetholder;
    CoordinatorLayout bottomsheet1;
    TextView removefromplaylist,deleteplaylist,currentPlaylistname,addSonglabel;
    List<String> artistnames = new ArrayList<>();
    List<Integer> artistNames = new ArrayList<>();
    ArtistAdapter artistAdapter;
    AlbumAdapter albumAdapter;
    List<String> albumnames = new ArrayList<>();
    List<Integer> albumNames = new ArrayList<>();
    private BottomSheetDialog bottomSheetDialog, bottomSheetDialog1;
    int a = 0;
    private BottomNavigationView bottomNavigationView;


    private static final int TIME_DELAY = 3000;
    private static long back_pressed;


    CharSequence titlename;
    int durationTime;
    String durationtime;


    final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;

    //02/10/22
    ExoPlayer exoPlayer;
    ActivityResultLauncher<String> recordAudioPersmissionLauncher;
    final String recordAudioPermission = Manifest.permission.RECORD_AUDIO;
    ConstraintLayout playerView,playlistView;
    //controls
    TextView homeSongNameView,homeSkipPrevBtn,homeplayPauseBtn,homeSkipNextBtn,playlisttext,playlistname;
    ImageView playlisticon,closeView;
    TextView playlist,allSongs,folder,album,artist;
    //wrappers
    ConstraintLayout homeControlWrapper,controlWrapper,AudioVisulaizationWrapper;
    //artwork
    //audioVisualizer
    BarVisualizer audioVisualizer;
    //blur image view
    BlurImageView blurImageView;
    //Status bar and navigation color
    int defualtStatusColor;
    //repeatMoDE
    int repeatMode = 1 ;// repeat all = 1, repeat one = 2, shuffle all = 3
    boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //install splashscreen
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //save the status color
        defualtStatusColor = getWindow().getStatusBarColor();
        //set the navigation color
        getWindow().setNavigationBarColor(ColorUtils.setAlphaComponent(defualtStatusColor,199));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        //recyclerview
        recyclerView = findViewById(R.id.recyclerview);
        storagePermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),granted->{
            if(granted){
                //fetch songs
                fetchsongs();
            }
            else{
                userResponse();
            }
        });

        //launch storage permisson
        //storagePermissionLauncher.launch(permission);

        //record audio permission
        recordAudioPersmissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(),granted->{
            if(granted && exoPlayer.isPlaying()){
                activateAudioVisualizer();
            }else{
                userResponseOnRecordAudioPerm();
            }
        });

        //views
        //exoPlayer = new ExoPlayer.Builder(this).build();
//        playlisticon = findViewById(R.id.playlistAddIcon);
//        playlisttext = findViewById(R.id.playlistaddtext);
//        playlistadd = findViewById(R.id.playlistView);
        songNameView = findViewById(R.id.songNameView);
        skipPrevBtn = findViewById(R.id.skipprevBtn);
        skipNextBtn = findViewById(R.id.skipnextBtn);
        playPauseBtn = findViewById(R.id.playpauseBtn);
        repeatBtn = findViewById(R.id.repeatModeBtn);
        playListBtn = findViewById(R.id.playlistBtn);
        playerCloseBtn = findViewById(R.id.playerCloseBtn);
        seekBar = findViewById(R.id.seekbar);
        progressView = findViewById(R.id.progressView);
        DurationView = findViewById(R.id.durationView);
        Artwork = findViewById(R.id.artworkView);
        sorticon = findViewById(R.id.sorticon);
        songTotal = findViewById(R.id.songsTotal);
        sortDialog = findViewById(R.id.sortdialog);
        atoz = findViewById(R.id.atoz);
        ztoa = findViewById(R.id.ztoa);
        newtoold = findViewById(R.id.newtoold);
        oldtonew = findViewById(R.id.oldtonew);
        grp2 = findViewById(R.id.group2);
        playlistIcon = findViewById(R.id.playlisticon);
        playlisttotal = findViewById(R.id.playlistTotal);
        recyclerView1 = findViewById(R.id.recyclerview1);
        menubar = findViewById(R.id.menuBar);
        sortingbar = findViewById(R.id.sortingBar);
        closeView = findViewById(R.id.closeView);
        search = findViewById(R.id.toolbar);
        addtoplaylist = findViewById(R.id.addmoresongs);
        bottomsheetholder = findViewById(R.id.bottomsheetholder);
        deleteplaylist = findViewById(R.id.deleteplaylist);
        currentPlaylistname = findViewById(R.id.clickedplaylistname);
        addSonglabel = findViewById(R.id.addsonglabel);
        bottomNavigationView = findViewById(R.id.bottomNavigation);


        //menu items
        playlist = findViewById(R.id.playlist);
        allSongs = findViewById(R.id.allSongs);
        album = findViewById(R.id.album);

        artist = findViewById(R.id.artist);


        homeSongNameView = findViewById(R.id.homeSongNameView);
        homeSkipNextBtn = findViewById(R.id.homeSkipNextBtn);
        homeSkipPrevBtn = findViewById(R.id.homeSkipPreviousBtn);
        homeplayPauseBtn = findViewById(R.id.homePlayPauseBtn);
        PlayerView = findViewById(R.id.PlayerView);

        //wrappers
        homeControlWrapper = findViewById(R.id.homeControlWrapper);
        sorting = "ntoo";

        //player controls
        //playerControls();
        bottomNavigationView.setOnItemSelectedListener(nav);

        //binding the player service and do everything after the binding
        doBindingService();


        playlistIcon.setOnClickListener( view -> playlistCreate());
        sorticon.setOnClickListener(view -> sortdialog());
        addtoplaylist.setVisibility(View.GONE);


        getPlaylists();
        closeView.setOnClickListener(view -> closeViewing());
        deleteplaylist.setVisibility(View.GONE);

    }

    private void doBindingService() {
        Intent playerServiceIntent = new Intent(this,PlayerService.class);
        bindService(playerServiceIntent,playerServiceConnection, Context.BIND_AUTO_CREATE);

    }
    ServiceConnection playerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PlayerService.ServiceBinder binder = (PlayerService.ServiceBinder) iBinder;
            exoPlayer = binder.getPlayerService().player;
            isBound = true;
            //ready to show songs
            storagePermissionLauncher.launch(permission);
            playerControls();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    private void closeViewing() {
        search.setVisibility(View.VISIBLE);
        closeView.setVisibility(View.GONE);
        menubar.setVisibility(View.VISIBLE);
        sortingbar.setVisibility(View.VISIBLE);

        playlisttotal.setVisibility(View.VISIBLE);

        DatabaseHelper db = new DatabaseHelper(MainActivity.this);

        if(Playlistname!=null){

            for(Song song: addToPlaylist.returnAdded()){
                db.addSong(song.getTitle(),Playlistname);
            }
            addToPlaylist.returnAdded().clear();
            Playlistname = null;

        }

        recyclerView.setAdapter(playlistAdapter);


    }

    private void getPlaylists() {
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        databaseHelper.addOne("Most Played");
        databaseHelper.addOne("Favorites");
        allplaylists.addAll(databaseHelper.getPlaylists());
    }

    private void sortdialog() {
        bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(R.layout.sort_dialog);
        RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.group2);
        bottomSheetDialog.show();
        showSort(radioGroup);

    }

    private void showSort(RadioGroup radioGroup) {
        radioGroup.check(a);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.atoz:
                        a = R.id.atoz;
                        sorting = "atoz";
                        radioGroup.check(a);
                        bottomSheetDialog.dismiss();
                        fetchsongs();
                        break;
                    case R.id.ztoa:
                        a = R.id.ztoa;
                        sorting ="ztoa";
                        radioGroup.check(a);
                        bottomSheetDialog.dismiss();
                        fetchsongs();
                        break;
                    case R.id.oldtonew:
                        a = R.id.oldtonew;
                        sorting="oton";
                        radioGroup.check(a);
                        bottomSheetDialog.dismiss();
                        fetchsongs();
                        break;
                    case R.id.newtoold:
                        a = R.id.newtoold;
                        sorting = "ntoo";
                        radioGroup.check(a);
                        bottomSheetDialog.dismiss();
                        fetchsongs();
                        break;
                }
            }
        });
    }

    private void playlistCreate() {
        CharSequence name;
        new AlertDialog.Builder(this)

                .setTitle("Create new playlist")
                .setView(
                        playlistEnter = new EditText(MainActivity.this)
                )
                .setPositiveButton("Commit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(playlistEnter.getText().toString()!=""){
                        Playlist playlist = new Playlist(playlistEnter.getText().toString());
                        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
                        boolean success = databaseHelper.addOne(playlistEnter.getText().toString());
                        if(success){
                            allplaylists.add(playlist);
                            playlisttotal.setText("Playlist("+allplaylists.size()+")");
                            Playlistname = playlistEnter.getText().toString();
                            addToPlaylist();
                        }
                        Toast.makeText(MainActivity.this,"Playlist "+playlistEnter.getText().toString()+" is created "+success,Toast.LENGTH_SHORT).show();}
                        else{
                            Toast.makeText(MainActivity.this,"Playlist name cannot be empty",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
    }

    private void addToPlaylist() {
          search.setVisibility(View.GONE);
          closeView.setVisibility(View.VISIBLE);
          menubar.setVisibility(View.GONE);
          sortingbar.setVisibility(View.GONE);
          recyclerView.setAdapter(addToPlaylist);
    }

    private void playerControls() {
        //song name marquee

        songNameView.setSelected(true);
        homeSongNameView.setSelected(true);


        //open player view on home control wrapper click
        homeSongNameView.setOnClickListener(view -> showPlayerView());
        exoPlayer.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                //show the playing song title
                assert  mediaItem != null;

                titlename = mediaItem.mediaMetadata.title;
                durationtime = getReadableTime((int) exoPlayer.getDuration());
                durationTime = (int) exoPlayer.getDuration();

               ontransition(
                       mediaItem.mediaMetadata.title,
                       getReadableTime2((int) exoPlayer.getCurrentPosition()),
                       (int) exoPlayer.getCurrentPosition(),
                       (int) exoPlayer.getDuration(),
                       getReadableTime((int) exoPlayer.getDuration())
        );

                homeSongNameView.setText(mediaItem.mediaMetadata.title);
                homeplayPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause,0,0,0);
                //show current artwork
                showCurrentArtwork();

                //upade progress
                updatePlayerPosProgress();

                PlayerView.setVisibility(View.VISIBLE);

                //set audio visulaizer
                activateAudioVisulaizer();
                //update player view colors;
                updatePlayerViewColors();

                if(!exoPlayer.isPlaying()){
                    exoPlayer.play();
                }

            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if(playbackState == ExoPlayer.STATE_READY){
                    //set values to player view
                    titlename = Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.title;
                    durationtime = getReadableTime((int) exoPlayer.getDuration());
                    durationTime = (int) exoPlayer.getDuration();
                    ontransition(
                            Objects.requireNonNull(exoPlayer.getCurrentMediaItem()).mediaMetadata.title,
                            getReadableTime2((int) exoPlayer.getCurrentPosition()),
                            (int) exoPlayer.getCurrentPosition(),
                            (int) exoPlayer.getDuration(),
                            getReadableTime((int) exoPlayer.getDuration())
                    );
                    homeSongNameView.setText(exoPlayer.getCurrentMediaItem().mediaMetadata.title);
                    homeplayPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause,0,0,0);


                    //upade progress
                    updatePlayerPosProgress();



                    //set audio visulaizer
                    activateAudioVisulaizer();
                    //update player view colors;
                    updatePlayerViewColors();

                }
                else{
                    playPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_outline,0,0,0);
                    homeplayPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow,0,0,0);
                }
            }
        });


        skipNextBtn.setOnClickListener(view -> skipToNextTrack());
        homeSkipNextBtn.setOnClickListener(view -> skipToNextTrack());
        skipPrevBtn.setOnClickListener(view -> skipToPreviousTrack());
        homeSkipPrevBtn.setOnClickListener(view -> skipToPreviousTrack());
        playPauseBtn.setOnClickListener(view -> playOrPausePlayer());
        homeplayPauseBtn.setOnClickListener(view -> playOrPausePlayer());
        playListBtn.setOnClickListener(view -> exitPlayer());
        playerCloseBtn.setOnClickListener(view -> exitPlayer());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressValue = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressValue = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(exoPlayer.getPlaybackState() == ExoPlayer.STATE_READY){
                    seekBar.setProgress(progressValue);
                    progressView.setText(getReadableTime2(progressValue));
                    exoPlayer.seekTo(progressValue);



                }

            }
        });

        repeatBtn.setOnClickListener(view -> {
            if(repeatMode == 1){
                exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
                repeatMode = 2;
                repeatBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat_one,0,0,0);

            }
            else if(repeatMode == 2){
                exoPlayer.setShuffleModeEnabled(true);
                exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
                repeatMode = 3;
                repeatBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_shuffle,0,0,0);

            }
            else if(repeatMode == 3){
                exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ALL);
                exoPlayer.setShuffleModeEnabled(false);
                repeatMode=1;
                repeatBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_repeat,0,0,0);
            }
        });
    }

    private void exitPlayer() {
        PlayerView.setVisibility(View.GONE);
    }

    private void ontransition(CharSequence title, String readableTime, int currentPosition, int duration, String readableTime1) {

        songNameView.setText(title);
        progressView.setText(readableTime);
        seekBar.setProgress(currentPosition);
        seekBar.setMax(duration);
        DurationView.setText(readableTime1);
        playPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_outline,0,0,0);
        Artwork.setAnimation(loadingRotation());
    }

    private Animation loadingRotation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0,360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        return rotateAnimation;
    }


    public void playOrPausePlayer() {
        if(exoPlayer.isPlaying()){
            exoPlayer.pause();
            playPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_outline,0,0,0);
            Artwork.clearAnimation();
//
            homeplayPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow,0,0,0);
//
        }
        else{
            exoPlayer.play();
            playPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_outline,0,0,0);
            Artwork.startAnimation(loadingRotation());
//
            homeplayPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause,0,0,0);
//
        }

        //upadting player colors
        updatePlayerColors();
    }

    public void skipToNextTrack() {
        if(exoPlayer.hasNextMediaItem()){
            exoPlayer.seekToNext();
        }
    }
    public void skipToPreviousTrack() {
        if(exoPlayer.hasPreviousMediaItem()){
            exoPlayer.seekToPrevious();
        }
    }

    private void updatePlayerViewColors() {
    }

    private void activateAudioVisulaizer() {
    }

    private void updatePlayerPosProgress() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(exoPlayer.isPlaying()){
                    progressView.setText(getReadableTime2((int) exoPlayer.getCurrentPosition()));
                    seekBar.setProgress((int) exoPlayer.getCurrentPosition());
//

                }
                //repeat calling the method
                updatePlayerPosProgress();
            }
        },1000);
    }

    private void showCurrentArtwork() {

//
    }

    private String getReadableTime(int currentPosition) {
        String time;

        int hrs = currentPosition/(1000*60*60);
        int min = (currentPosition%(1000*60*60))/(1000*60);
        int secs = (((currentPosition%(1000*60*60))%(1000*60*60))%(1000*60))/1000;

        if(hrs<1){
             time = min + ":"+secs;

        }else{
            time = hrs+":"+min+":"+secs;

        }

        return time;

    }
    private String getReadableTime2(int currentPosition) {
        String time;

        int hrs = currentPosition/(1000*60*60);
        int min = (currentPosition%(1000*60*60))/(1000*60);
        int secs = (((currentPosition%(1000*60*60))%(1000*60*60))%(1000*60))/1000;

        if(hrs<1){
            if(secs == 60){
                secs = 0;
            }
            time = min + ":"+secs;
        }else{
            if(secs == 60){
                secs = 0;
            }
            if(min == 60){
                min =0;
            }
            time = hrs+":"+min+":"+secs;

        }

        return time;

    }


    private void showPlayerView() {
        PlayerView.setVisibility(View.VISIBLE);
    }

    private void updatePlayerColors() {

    }



    private void userResponseOnRecordAudioPerm() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(recordAudioPermission)){
                //user alert dialog
                new AlertDialog.Builder(this)
                        .setTitle("Requesting to show Audio Visualizer")
                        .setMessage("Allow this app to display audio visualizer when music is playing")
                        .setPositiveButton("Allow", (dialogInterface, i) -> recordAudioPersmissionLauncher.launch(recordAudioPermission))
                        .setNegativeButton("Deny", (dialogInterface, i) -> {
                            Toast.makeText(getApplicationContext(),
                                    "You denied to show the media visulaizer", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // audio visulaizer
    private void activateAudioVisualizer() {


    }

//

    private void userResponse() {
        if(ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED){
            fetchsongs();
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(shouldShowRequestPermissionRationale(permission)){
                //show an educational UI to user explaining why we need the permission
                //user alert dialog
                new AlertDialog.Builder(this)
                        .setTitle("Requesting Permission")
                        .setMessage("Allow us to fetch songs on your device")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //request permission
                                storagePermissionLauncher.launch(permission);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "You denied us to show songs",Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        }

        else{
            Toast.makeText(this,"You cancelled to show songs",Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchsongs() {
        // define a list to carry songs
        List<Song> songs = new ArrayList<>();
        Uri mediastoreUri;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            mediastoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);

        }else{
            mediastoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        // Define projection
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST_ID
        };
        //order
        if(sorting == "ntoo"){
        sortOrder = MediaStore.Audio.Media.DATE_ADDED + " DESC";
        }
        else if(sorting == "oton"){
            sortOrder = MediaStore.Audio.Media.DATE_ADDED + " ASC";
        }
        else if(sorting == "atoz"){
            sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " ASC";
        }
        else{
            sortOrder = MediaStore.Audio.Media.DISPLAY_NAME + " DESC";
        }


        //get the songs
        try(Cursor cursor= getContentResolver().query(mediastoreUri,projection,null,null,sortOrder)){
            //cacahe cursor
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
            int albumnameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM);
            int artistidColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID);

            while (cursor.moveToNext()){
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int albumid = cursor.getInt(albumColumn);
                int size = cursor.getInt(sizeColumn);
                String artistname = cursor.getString(artistColumn);
                String albumname = cursor.getString(albumnameColumn);
                int artistid = cursor.getInt(artistidColumn);

                //songs Uri
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);

                //album artwork uri
                Uri albumArtworkUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),albumid);

                ///remove .mp3 extension
                name = name.substring(0,name.lastIndexOf("."));

                //sng item
                Song song = new Song(name,uri,albumArtworkUri,size,duration,albumname,artistname,albumid,artistid,0,false);

                //add song item to song list
                songs.add(song);

            }

            //display songs
            allsongs.addAll(songs);
            showSongs(songs);
            

        }
    }

    private void showSongs(List<Song> songs) {

        if(songs.size()==0){
            Toast.makeText(this,"No songs availabale", Toast.LENGTH_SHORT).show();
            return;
        }


        //save songs
        allsongs.clear();
        allsongs.addAll(songs);
        List<Integer> nos = new ArrayList<>();
        int no = 0;
        for(Song song: songs){
            no = no + 1;
            nos.add(no);
        }

        songTotal.setText("Songs("+nos.size()+")");

        //updete text
        String title = getResources().getString(R.string.app_name) ;
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        //group by artist
        groupbyartist(allsongs);

        //group by album;
        groupbyalbum(allsongs);

        //layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        playlistIcon.setVisibility(View.GONE);
        playlisttotal.setVisibility(View.GONE);

        bottomSheetDialog1 = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog1.setContentView(R.layout.song_item_bottomsheet);
        BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog2.setContentView(R.layout.file_info_bottomsheet);
        //define adapter
        songAdapter = new SongAdapter(MainActivity.this,songs,exoPlayer,PlayerView,nos,bottomSheetDialog1,bottomSheetDialog2);
        addToPlaylist = new AddToPlaylist(allsongs,MainActivity.this,eachplaylistsong,null);
        //set adapter to recyclerview
        recyclerView.setAdapter(songAdapter);
        //allSongs tab
        allSongs.setTextColor(getResources().getColor(R.color.primary_color));
        //playlisttab

        playlistAdapter = new PlaylistAdapter(MainActivity.this,allplaylists,allsongs,recyclerView,exoPlayer,nos,search,menubar,sortingbar,closeView,addtoplaylist,
                songTotal,sorticon,playlistIcon,playlisttotal,bottomsheet,bottomsheetholder,removefromplaylist,deleteplaylist);
       playlist.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               recyclerView.setAdapter(playlistAdapter);
               playlist.setTextColor(getResources().getColor(R.color.primary_color));
               allSongs.setTextColor(getResources().getColor(R.color.white));
               artist.setTextColor(getResources().getColor(R.color.white));
               album.setTextColor(getResources().getColor(R.color.white));
               songTotal.setVisibility(View.GONE);
               sorticon.setVisibility(View.GONE);
               playlistIcon.setVisibility(View.VISIBLE);
               playlisttotal.setVisibility(View.VISIBLE);
               playlisttotal.setText("Playlists("+allplaylists.size()+")");
               deleteplaylist.setVisibility(View.GONE);
               sortingbar.setVisibility(View.VISIBLE);
               playedSongs();

           }
       });
       allSongs.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               songAdapter = new SongAdapter(getApplicationContext(),songs,exoPlayer,PlayerView,nos,bottomSheetDialog1,bottomSheetDialog2);
               recyclerView.setAdapter(songAdapter);
               allSongs.setTextColor(getResources().getColor(R.color.primary_color));
               playlist.setTextColor(getResources().getColor(R.color.white));
               artist.setTextColor(getResources().getColor(R.color.white));
               album.setTextColor(getResources().getColor(R.color.white));
               playlistIcon.setVisibility(View.GONE);
               playlisttotal.setVisibility(View.GONE);
               songTotal.setVisibility(View.VISIBLE);
               sorticon.setVisibility(View.VISIBLE);
               addtoplaylist.setVisibility(View.GONE);
               deleteplaylist.setVisibility(View.GONE);
               sortingbar.setVisibility(View.VISIBLE);

           }
       });

        artist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                artistAdapter = new ArtistAdapter(allsongs,MainActivity.this,recyclerView,artistnames,artistNames,exoPlayer,nos);
                recyclerView.setAdapter(artistAdapter);
                artist.setTextColor(getResources().getColor(R.color.primary_color));
                playlist.setTextColor(getResources().getColor(R.color.white));
                allSongs.setTextColor(getResources().getColor(R.color.white));
                album.setTextColor(getResources().getColor(R.color.white));
                playlistIcon.setVisibility(View.GONE);
                playlisttotal.setVisibility(View.GONE);
                songTotal.setVisibility(View.GONE);
                sorticon.setVisibility(View.GONE);
                addtoplaylist.setVisibility(View.GONE);
                deleteplaylist.setVisibility(View.GONE);
                sortingbar.setVisibility(View.GONE);

            }
        });

        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                albumAdapter = new AlbumAdapter(allsongs,MainActivity.this,recyclerView,albumnames,albumNames,exoPlayer,nos);
                recyclerView.setAdapter(albumAdapter);
                album.setTextColor(getResources().getColor(R.color.primary_color));
                playlist.setTextColor(getResources().getColor(R.color.white));
                allSongs.setTextColor(getResources().getColor(R.color.white));
                artist.setTextColor(getResources().getColor(R.color.white));
                playlistIcon.setVisibility(View.GONE);
                playlisttotal.setVisibility(View.GONE);
                songTotal.setVisibility(View.GONE);
                sorticon.setVisibility(View.GONE);
                addtoplaylist.setVisibility(View.GONE);
                deleteplaylist.setVisibility(View.GONE);
                sortingbar.setVisibility(View.GONE);

            }
        });


    }

    private void playedSongs() {
            DatabaseHelper db = new DatabaseHelper(this);
            db.removePlaylist("Most Played");
            for(String s: db.getPlayedSongs()){
                db.addSong(s,"Most Played");
            }
    }


    private List<String> joinsamenames(List<String> jointhis) {
           List<String> uniqueNames = new ArrayList<>();
           uniqueNames.add(jointhis.get(0));
            for(int i = 1;i<jointhis.size();i++){
                if(Objects.equals(jointhis.get(i), jointhis.get(i - 1))){

                    jointhis.remove(i-1);
                }
            }

            return jointhis;

    }

    private void groupbyalbum(List<Song> allsongss) {
         albumnames.clear();
         albumNames.clear();

        List<Song> songss = new ArrayList<>(allsongss);
        List<Song> namee2 = new ArrayList<>();
        List<Song> namee3 = new ArrayList<>();
        //insertion sort
        for(int k = 0; k <songss.size();k++){
            String name = songss.get(k).getAlbumname();
            albumnames.add(name);
            int a = 0;
//
            for(Song song: songss){
                if(song.getAlbumname().equalsIgnoreCase(name) && song.getAlbumid() == songss.get(k).getAlbumid()){
                    a = a + 1;
                    namee2.add(song);
                }
            }
            albumNames.add(a);
            songss.removeAll(namee2);

        }

        if(songss.size() != 0){
            for(int k = 0; k <songss.size();k++){
                String name = songss.get(k).getAlbumname();
                albumnames.add(name);
                int a = 0;
//
                for(Song song: songss){
                    if(song.getAlbumname().equalsIgnoreCase(name) || song.getAlbumid() == songss.get(k).getAlbumid()){
                        a = a + 1;
                        namee3.add(song);
                    }
                }
                albumNames.add(a);
                songss.removeAll(namee3);

            }
        }

    }

    private void groupbyartist(List<Song> allSong) {
        artistnames.clear();
        artistNames.clear();
        List<Song> songss = new ArrayList<>(allSong);
        List<Song> namee2 = new ArrayList<>();
        List<Song> namee4 = new ArrayList<>();
        //insertion sort
        for(int k = 0; k <songss.size();k++){
            String name = songss.get(k).getArtistname();
            artistnames.add(name);
            int a = 0;
//
            for(Song song: songss){
                if(song.getArtistname().equalsIgnoreCase(name) && song.getArtistid() == songss.get(k).getArtistid()){
                    a = a + 1;
                    namee2.add(song);
                }
            }
            artistNames.add(a);
            songss.removeAll(namee2);

        }
        if(songss.size() != 0){
            for(int k = 0; k <songss.size();k++){
                String name = songss.get(k).getArtistname();
                artistnames.add(name);
                int a = 0;
//
                for(Song song: songss){
                    if(song.getArtistname().equalsIgnoreCase(name) || song.getArtistid() == songss.get(k).getArtistid()){
                        a = a + 1;
                        namee4.add(song);
                    }
                }
                artistNames.add(a);
                songss.removeAll(namee4);

            }
        }





    }

    //Bottom navigationView
    private final NavigationBarView.OnItemSelectedListener nav = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.musicMenu:
                    break;
                case R.id.videoMenu:
                    Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                    startActivity(intent);
                    break;
            }
            return true;
        }
    };


    //setting the menu /search


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_btn,menu);

        //Search
        MenuItem menuItem = menu.findItem(R.id.searchBtn);
        SearchView searchView = (SearchView) menuItem.getActionView();
        //search song
        SearchSong(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    private void SearchSong(SearchView searchView) {
        //SearchView listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //filter song
                filterSongs(newText.toLowerCase());
                return true;
            }

        });

    }

    private void filterSongs(String query) {
        List<Song> filtered = new ArrayList<>();
        if(allsongs.size()>0){
            for (Song song: allsongs
                 ) {
                if(song.getTitle().toLowerCase().contains(query)){
                    filtered.add(song);
                }
            }

            if(songAdapter != null){
                songAdapter.filterSongs(filtered);
            }
        }

    }


    @Override
    protected void onStop() {
        super.onStop();

    }



    public void onBackPressed(){
        if(PlayerView.getVisibility() == View.VISIBLE) {
            PlayerView.setVisibility(View.GONE);
        }
        else{
            if(back_pressed + TIME_DELAY > System.currentTimeMillis()){
                super .onBackPressed();
                if(exoPlayer.isPlaying()){
                    exoPlayer.stop();
                }
            }else{
                Toast.makeText(MainActivity.this,"Press once again to exit",Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    private void doUnbindService() {
        if(isBound){
            unbindService(playerServiceConnection);
            isBound = false;
        }
    }
}