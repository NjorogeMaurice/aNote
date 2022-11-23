package com.android.player.anote;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import com.android.player.anote.adapters.VideoFolderAdapter;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {
    ActivityResultLauncher<String> storagePermissionLauncher;
    final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    ExoPlayer player;
    private ArrayList<Video> videoFiles = new ArrayList<>();
    private ArrayList<String> allFolderList = new ArrayList<>();
    RecyclerView recyclerView;
    VideoFolderAdapter adapter;
    private BottomNavigationView bottomNavigationView;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        bottomNavigationView = findViewById(R.id.bottomNavigation1);
        bottomNavigationView.setOnItemReselectedListener(nav);
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Videos");
        if(ContextCompat.checkSelfPermission(this,permission)== PackageManager.PERMISSION_GRANTED){
            fetchVideos();
        }

    }

    private void fetchVideos() {
        recyclerView = findViewById(R.id.videoFolderRecyclerView);
        swipeRefreshLayout = findViewById(R.id.refresh_folders);
        showFolder();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showFolder();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private void showFolder() {
        videoFiles = fetchMedia();
        adapter = new VideoFolderAdapter(videoFiles,allFolderList,VideoActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        adapter.notifyDataSetChanged();
    }

    private ArrayList<Video> fetchMedia() {
        ArrayList<Video> videoArrayList = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = getContentResolver().query(uri,null,null,null,null);
        if(cursor != null && cursor.moveToNext()){
            do{
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
                Video video = new Video(id,title,displayName,size,duration,path,dateAdded);

                int index = path.lastIndexOf("/");
                String subString = path.substring(0,index);
                if(!allFolderList.contains(subString)){
                    allFolderList.add(subString);
                }

                videoArrayList.add(video);
            }while(cursor.moveToNext());


        }

        return videoArrayList;
    }

    private final NavigationBarView.OnItemReselectedListener nav = new NavigationBarView.OnItemReselectedListener() {
        @Override
        public void onNavigationItemReselected(@NonNull MenuItem item) {
            switch (item.getItemId()){
                case R.id.musicMenu:
                    Intent intent = new Intent(VideoActivity.this, MainActivity.class);
                    startActivity(intent);
                    break;
                case R.id.videoMenu:
                    break;
            }

        }
    };
}