package com.android.player.anote.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.player.anote.R;
import com.android.player.anote.Video;
import com.android.player.anote.VideoFilesActivity;

import java.util.ArrayList;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.ViewHolder> {

    private ArrayList<Video> mediaFiles;
    private ArrayList<String> videoPath;
    private Context context;

    public VideoFolderAdapter(ArrayList<Video> mediaFiles, ArrayList<String> videoPath, Context context) {
        this.mediaFiles = mediaFiles;
        this.videoPath = videoPath;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_folder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoFolderAdapter.ViewHolder holder, int position) {

        int indexPath = videoPath.get(position).lastIndexOf("/");
        String nameOfFolder = videoPath.get(position).substring(indexPath+1);
        holder.folderName.setText(nameOfFolder);
        holder.folderPath.setText(videoPath.get(position));
        holder.noOfFiles.setText("5 videos");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoFilesActivity.class);
                intent.putExtra("folderName",nameOfFolder);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoPath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView folderName,folderPath,noOfFiles;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            folderName = itemView.findViewById(R.id.videoFolderName);
            folderPath = itemView.findViewById(R.id.videoFolderPath);
            noOfFiles = itemView.findViewById(R.id.noOfFiles);
        }
    }
}
