package com.android.player.anote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.android.player.anote.adapters.VideoAdapter;

import java.util.ArrayList;

public class VideoFilesActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    VideoAdapter videoAdapter;
    Intent intent;
    private ArrayList<Video> videoFiles = new ArrayList<>();
    String folder_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_files);

        recyclerView = findViewById(R.id.videos_rv);
        folder_name = getIntent().getStringExtra("folderName");
        Toolbar toolbar;
        toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(folder_name);
        showVideoFiles();
    }

    private void showVideoFiles() {
        videoFiles= fetchMediaItems(folder_name);
        videoAdapter = new VideoAdapter(videoFiles,this);
        recyclerView.setAdapter(videoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL,false));
        videoAdapter.notifyDataSetChanged();

    }

    private ArrayList<Video> fetchMediaItems(String folderName) {
        ArrayList<Video> videos = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Video.Media.DATA+" like?";
        String[] selctionArgs = new String[]{
              "%"+folderName+"%"
        };

        Cursor cursor = getContentResolver().query(uri,null,selection,selctionArgs,null);
        if(cursor!=null&&cursor.moveToNext()){
            do{
                String id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String size = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String dateAdded = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED));
                Video video = new Video(id,title,displayName,size,duration,path,dateAdded);
                videos.add(video);
            }while (cursor.moveToNext());
        }

        return videos;
    }
}