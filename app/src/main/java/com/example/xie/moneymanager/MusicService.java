package com.example.xie.moneymanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;

import java.util.ArrayList;

public class MusicService extends Service   {
    public MusicService() {
    }

    private static final String TAG = "MusicService";
    public MediaPlayer mediaPlayer;

    class MyBinder extends Binder implements MediaPlayer.OnCompletionListener {

        // 播放音乐
        public void play() {
            try {
                if (mediaPlayer == null) {
                    // 创建一个MediaPlayer播放器
                    mediaPlayer = new MediaPlayer();
                    // 指定参数为音频文件
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    // 指定播放的路径
                   String songs[] ={
                           /* "http://129.204.188.236:8080/AroundTaxiSSM/jay1.mp3",
                            "http://129.204.188.236:8080/AroundTaxiSSM/jay2.mp3",
                            "http://129.204.188.236:8080/AroundTaxiSSM/jay3.mp3",
                            "http://129.204.188.236:8080/AroundTaxiSSM/jay4.mp3",*/
                            "http://129.204.188.236:8080/AroundTaxiSSM/lz.mp3"

                    };
                    int i=(int)(Math.random()*songs.length);
                    mediaPlayer.setDataSource(songs[i]);
                    // 准备播放
                    mediaPlayer.prepare();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        public void onPrepared(MediaPlayer mp) {
                            // 开始播放
                            mediaPlayer.start();
                            mediaPlayer.setLooping(true);
                        }
                    });
                } else {
                    int position = getCurrentProgress();
                    mediaPlayer.seekTo(position);
                    mediaPlayer.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void pause() {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); // 暂停播放
            } else if (mediaPlayer != null && (!mediaPlayer.isPlaying())) {
                mediaPlayer.start();
            }
        }

        @Override
     public void onCompletion(MediaPlayer mp) {
               // 当歌曲播放完毕，切歌到下一首
            mediaPlayer.reset();
            play();
           /* try {
                String songs[] = {
                        "http://129.204.188.236:8080/AroundTaxiSSM/lz.mp3",
                        "http://129.204.188.236:8080/AroundTaxiSSM/song.mp3",
                        "http://129.204.188.236:8080/AroundTaxiSSM/123.mp3"
                };
                int i = (int) (Math.random() * songs.length);
                mediaPlayer.setDataSource(songs[i]);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }catch (Exception e){
                e.printStackTrace();
            }*/
     }

    }

    public void onCreate() {
        super.onCreate();
    }

    // 获取当前进度
    public int getCurrentProgress() {
        if (mediaPlayer != null & mediaPlayer.isPlaying()) {
            return mediaPlayer.getCurrentPosition();
        } else if (mediaPlayer != null & (!mediaPlayer.isPlaying())) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // 第一步执行onBind方法
        return new MyBinder();
    }

}
