package com.android.player.anote;

import static com.google.android.exoplayer2.util.NotificationUtil.IMPORTANCE_HIGH;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Binder;
import android.os.IBinder;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;

import java.util.Objects;

public class PlayerService extends Service {
    //Class members
    private final IBinder serviceBinder = new ServiceBinder();

    ExoPlayer player;
    PlayerNotificationManager playerNotificationManager;



    // service binder class for clients
    public class ServiceBinder extends Binder{
       public PlayerService getPlayerService(){
           return PlayerService.this;
       }
    }
    @Override
    public IBinder onBind(Intent intent) {
       return serviceBinder;
    }

    //Override onCreate

    @Override
    public void onCreate() {
        super.onCreate();
        //assign variables
        player = new ExoPlayer.Builder(getApplicationContext()).build();
        //audio focus attributes
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build();

        player.setAudioAttributes(audioAttributes,true);

        //notification manager
        final String channelId = getResources().getString(R.string.app_name) + " music channel ";
        final int notificationId = 1111111;
        playerNotificationManager = new PlayerNotificationManager.Builder(this, notificationId,channelId)
                .setChannelImportance(IMPORTANCE_HIGH)
                .setNotificationListener(notificationListener)
                .setMediaDescriptionAdapter(descriptionAdapter)
                .setSmallIconResourceId(R.drawable.not)
                .setChannelDescriptionResourceId(R.string.app_name)
                .setNextActionIconResourceId(R.drawable.ic_skip_next)
                .setPreviousActionIconResourceId(R.drawable.ic_skip_previous)
                .setPauseActionIconResourceId(R.drawable.ic_pause)
                .setPlayActionIconResourceId(R.drawable.ic_music_play)
                .setChannelNameResourceId(R.string.app_name)
                .build();


        //set player to notification manager
        playerNotificationManager.setPlayer(player);
        playerNotificationManager.setPriority(NotificationCompat.PRIORITY_HIGH);
        playerNotificationManager.setUseRewindAction(false);
        playerNotificationManager.setUseFastForwardAction(false);
    }

    @Override
    public void onDestroy() {
        // release the player
        if(player.isPlaying()){
            player.stop();
        }
        playerNotificationManager.setPlayer(null);
        player.release();
        player=null;
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    // notification listener
    PlayerNotificationManager.NotificationListener notificationListener = new PlayerNotificationManager.NotificationListener() {
        @Override
        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
            PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);

            stopForeground(true);
            if(player.isPlaying()){
                player.pause();
            }
        }

        @Override
        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
            PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
            startForeground(notificationId,notification);
        }
    };

    PlayerNotificationManager.MediaDescriptionAdapter descriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            return Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title;
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            //Intent to open app when clicked
            Intent openAppIntent = new Intent(getApplicationContext(),MainActivity.class);

            return PendingIntent.getActivity(getApplicationContext(),0,openAppIntent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return null;
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            //try creating an image view
            ImageView view = new ImageView(getApplicationContext());
            view.setImageDrawable(getResources().getDrawable(R.drawable.music));


            BitmapDrawable bitmapDrawable= (BitmapDrawable) view.getDrawable();
            if(bitmapDrawable == null){
                bitmapDrawable = (BitmapDrawable) ContextCompat.getDrawable(getApplicationContext(), R.drawable.music);
            }


            assert bitmapDrawable != null;
            return bitmapDrawable.getBitmap();
        }
    };
}