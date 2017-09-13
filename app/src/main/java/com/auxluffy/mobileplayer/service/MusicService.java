package com.auxluffy.mobileplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.auxluffy.mobileplayer.IMusicService;
import com.auxluffy.mobileplayer.R;
import com.auxluffy.mobileplayer.activity.AudioPlayerActivity;
import com.auxluffy.mobileplayer.domain.VideoItem;

import java.io.IOException;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * 作用：服务
 */
public class MusicService extends Service {

    public static final String OPENAUDIO = "com.auxluffy.mobileplayer_OPENAUDIO";
    private ArrayList<VideoItem> list;
    /**
     * 播放音乐和视频
     */
    private MediaPlayer mediaPlayer;
    /**
     * 当前播放单频信息
     */
    private VideoItem item;
    /**
     * 当前播放音频的列表中的位置
     */
    private int position;
    /**
     * 顺序播放模式
     */
    public static final int REPEAT_NOMAL = 1;
    /**
     * 单曲循环模式
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 全部循环播放模式
     */
    public static final int REPEAT_ALL = 3;

    private int playMode = REPEAT_NOMAL;


    @Override
    public void onCreate() {
        super.onCreate();
        //加载音乐列表
        getData();
    }

    public void getData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                ContentResolver resolver = MusicService.this.getContentResolver();
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,//视频播放的地址
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    list = new ArrayList<VideoItem>();
                    while (cursor.moveToNext()) {

                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);
                        VideoItem item = new VideoItem(name, duration, size, data, artist);
                        list.add(item);
                    }
                }

            }
        }.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    /**
     * 相当于服务的代理类，调用它的方法，调用服务的方法,stub对象是返回给Activity的可序列化对象
     */
    private IMusicService.Stub stub = new IMusicService.Stub() {
        //不能new通过MusicService.this来产生的service对象是当服务启动时由系统自动创建的，如果new的话就不是系统自动创建的，也就不能在后台运行此服务了
        MusicService service = MusicService.this;

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public String getArtist() throws RemoteException {
            return service.getArtist();
        }

        @Override
        public String getName() throws RemoteException {
            return service.getName();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            service.seekTo(position);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            service.setPlayMode(playMode);
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return service.isPlaying();
        }

        @Override
        public void notifyChange(String action) throws RemoteException {
            service.notifyChange(action);
        }

        @Override
        public String getAudioPath() throws RemoteException {
            return service.getAudioPath();
        }

        @Override
        public int getAudioSessionId() throws RemoteException {
            return service.getAudioSessionId();
        }
    };

    private String getAudioPath() {
        return item.getData();
    }

    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    /**
     * 根据提供的position来打开相应文件
     *
     * @param position
     */
    private void openAudio(int position) {
        if (list != null && list.size() > 0) {
            this.position = position;
            item = list.get(position);
            if (mediaPlayer != null) {
                //先重置再释放
                mediaPlayer.reset();//重置
                mediaPlayer.release();//释放加载的音乐文件

            }
            try {
                mediaPlayer = new MediaPlayer();
                //设置监听
                mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());//准备监听
                mediaPlayer.setOnCompletionListener(new MyOnCompletionListener());//监听播放完成
                mediaPlayer.setOnErrorListener(new MyOnErrorListener());//播放出错
                mediaPlayer.setDataSource(item.getData());
                mediaPlayer.prepareAsync();//一般用异步准备-->onPrepared()

                int playmode = getPlayMode();
                mediaPlayer.setLooping(false);
                if (playmode == MusicService.REPEAT_SINGLE) {//不回调播放完成
                    mediaPlayer.setLooping(true);
                }
                //mediaPlayer.start();//异步准备中已经调用过此方法了不用再重复调用了
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(MusicService.this, "播放列表还没有加载完成", Toast.LENGTH_SHORT).show();
        }
    }

    private NotificationManager manager;

    /**
     * 开始播放
     */
    private void start() {
        mediaPlayer.start();

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra("notification", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("321音乐")
                .setContentText("正在播放" + getName())
                .setContentIntent(pendingIntent)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;//设置属性,点击不消失
        manager.notify(1, notification);

    }

    /**
     * 暂停播放
     */
    private void pause() {
        mediaPlayer.pause();
    }

    /**
     * 得到演唱者的信息
     *
     * @return
     */
    private String getArtist() {
        if (item != null) {
            return item.getArtist();
        }
        return "";
    }

    /**
     * 得到当前播放文件的名称
     *
     * @return
     */
    private String getName() {
        if (item != null) {
            return item.getName();
        }
        return "";
    }

    /**
     * 得到当前播放进度
     *
     * @return
     */
    private int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();

        }
        return 0;
    }

    /**
     * 得到当前文件时长
     *
     * @return
     */
    private int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 拖动音频
     *
     * @param position
     */
    private void seekTo(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(position);
        }
    }

    /**
     * 得到播放模式
     *
     * @return
     */
    private int getPlayMode() {
        return playMode;
    }

    /**
     * 设置播放模式
     */
    private void setPlayMode(int playMode) {
        this.playMode = playMode;
        if (playMode == MusicService.REPEAT_SINGLE) {
            mediaPlayer.setLooping(true);//不回调播放完成了
        } else {
            mediaPlayer.setLooping(false);
        }

        //CacheUtils.putInt(this, "playmode", playmode);
    }

    /**
     * 播放前一个视频
     */
    private void pre() {
        //设置位置
        setPrePosition();
        //根据位置打开
        openPrePosition();
    }

    private void openPrePosition() {
        int playmode = getPlayMode();
        if (playmode == MusicService.REPEAT_NOMAL) {
            if (position < list.size()) {
                openAudio(position);
            } else {
                position = 0;
            }
        } else if (playmode == MusicService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position >=0) {
                openAudio(position);
            } else {
                position = 0;
            }
        }
    }

    private void setPrePosition() {
        int playmode = getPlayMode();
        if (playmode == MusicService.REPEAT_NOMAL) {
            position--;
        } else if (playmode == MusicService.REPEAT_SINGLE) {
            position--;
            if(position<0) {
                position = list.size()-1;
            }
        } else if (playmode == MusicService.REPEAT_ALL) {
            position--;
            if (position <0) {
                position = list.size()-1;
            }
        } else {
            position--;

        }
    }

    /**
     * 播放下一个视频
     */
    private void next() {
        //设置位置
        setNextPosition();
        //根据位置打开
        openNextPosition();
    }

    /**
     * 根据不同的模式，设置不同的下一个
     */
    private void openNextPosition() {

        int playmode = getPlayMode();
        if (playmode == MusicService.REPEAT_NOMAL) {
            if (position < list.size()) {
                openAudio(position);
            } else {
                position = list.size() - 1;
            }
        } else if (playmode == MusicService.REPEAT_SINGLE) {
            openAudio(position);
        } else if (playmode == MusicService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position < list.size()) {
                openAudio(position);
            } else {
                position = list.size() - 1;
            }
        }


    }

    private void setNextPosition() {
        int playmode = getPlayMode();
        if (playmode == MusicService.REPEAT_NOMAL) {
            position++;
        } else if (playmode == MusicService.REPEAT_SINGLE) {
            position++;
            if(position>list.size()-1) {
                position = 0;
            }
        } else if (playmode == MusicService.REPEAT_ALL) {
            position++;
            if (position > list.size() - 1) {
                position = 0;
            }
        } else {
            position++;

        }

    }

    /**
     * 加载完成的监听类
     */
    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            //发广播
            notifyChange(OPENAUDIO);

            //当准备好后就开始播放
            start();
        }
    }

    private void notifyChange(String action) {
        /*Intent intent = new Intent();
        intent.setAction(action);
        sendBroadcast(intent);*/

        EventBus.getDefault().post(item);
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {

            next();
            return false;//如果return false的话就由系统弹出对话框，return true的话系统就不再弹出对话框
        }
    }

    private int getAudioSessionId(){
        if(mediaPlayer!=null) {

            return mediaPlayer.getAudioSessionId();
        }
        return 0;
    }
}
