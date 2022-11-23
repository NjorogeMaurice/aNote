package com.android.player.anote.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.player.anote.R;
import com.android.player.anote.Video;
import com.android.player.anote.VideoPlayerActivity;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private ArrayList<Video> VideoList;
    private Context context;

    public VideoAdapter(ArrayList<Video> videoList, Context context) {
        VideoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_files,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoAdapter.ViewHolder holder, int position) {
         holder.videoName.setText(VideoList.get(position).getDisplayName());
         String size = VideoList.get(position).getSize();
         holder.videoSize.setText(android.text.format.Formatter.formatFileSize(context,Long.parseLong(size)));
         double miliseconds = Double.parseDouble(VideoList.get(position).getDuration());
         holder.videoDuration.setText(timeConversion((long) miliseconds));


        Glide.with(context).load(new File(VideoList.get(position).getPath())).into(holder.thumbnail);
         
         holder.video_more.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Toast.makeText(context, "Videos more clicked", Toast.LENGTH_SHORT).show();
             }
         });

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 Intent intent = new Intent(context, VideoPlayerActivity.class);
                 intent.putExtra("position",position);
                 intent.putExtra("video_title",VideoList.get(position).getDisplayName());
                 Bundle bundle = new Bundle();
                 bundle.putParcelableArrayList("videoArrayList",VideoList);
                 intent.putExtras(bundle);
                 context.startActivity(intent);
             }
         });
    }

    @Override
    public int getItemCount() {
        return VideoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail,video_more;
        TextView videoName, videoSize, videoDuration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            video_more = itemView.findViewById(R.id.videos_more);
            videoName = itemView.findViewById(R.id.video_name);
            videoSize = itemView.findViewById(R.id.video_size);
            videoDuration = itemView.findViewById(R.id.video_duration);
        }
    }

    public String timeConversion(long value){
        String videoTime = null;
        int duration = (int) value;
        int hrs = duration/3600000;
        int mins = (duration/60000)%60000;
        int secs = duration%60000/1000;
        if(hrs > 0){
            videoTime = String.format("%02d:%02d:02d",hrs,mins,secs);
        }else{
            videoTime = String.format("%02d:%02d",mins,secs);
        }


        return videoTime;
    }
}
