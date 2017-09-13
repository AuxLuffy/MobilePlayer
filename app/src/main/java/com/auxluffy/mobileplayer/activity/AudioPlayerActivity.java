package com.auxluffy.mobileplayer.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auxluffy.mobileplayer.IMusicService;
import com.auxluffy.mobileplayer.R;
import com.auxluffy.mobileplayer.domain.Lyric;
import com.auxluffy.mobileplayer.domain.VideoItem;
import com.auxluffy.mobileplayer.service.MusicService;
import com.auxluffy.mobileplayer.utils.LyricUtils;
import com.auxluffy.mobileplayer.utils.Utils;
import com.auxluffy.mobileplayer.view.BaseVisualizerView;
import com.auxluffy.mobileplayer.view.ShowLyricView;

import java.io.File;
import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/6/17.
 */
public class AudioPlayerActivity extends Activity implements View.OnClickListener {
    private static final int PROGRESS = 1;
    private static final int SHOW_LYRIC = 2;
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPlaymode;
    private Button btnAudioPre;
    private Button btnAudioPlayPause;
    private Button btnAudioNext;
    private Button btnAudioLyric;
    private Uri uri;
    private ArrayList<VideoItem> list;
    private int position;
    private IMusicService service;
    private Utils utils;
    private boolean notification;
    private ShowLyricView show_lyric_view;
    private BaseVisualizerView base_visualizer_view;

    private MyReceiver myReceiver;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            service = IMusicService.Stub.asInterface(iBinder);

            if (service != null) {
                if(!notification) {
                    try {
                        service.openAudio(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else {
                    //不处理，发广播
                    //第一种方式：
                    showViewData();

                    //第二种方式:
                   /* try {
                        service.notifyChange(MusicService.OPENAUDIO);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }*/
                }

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private Visualizer mVisualizer;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-06-22 16:09:53 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPlaymode = (Button) findViewById(R.id.btn_audio_playmode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioPlayPause = (Button) findViewById(R.id.btn_audio_play_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnAudioLyric = (Button) findViewById(R.id.btn_audio_lyric);
        show_lyric_view = (ShowLyricView) findViewById(R.id.show_lyric_view);
        ivIcon.setBackgroundResource(R.drawable.animation_list);

        base_visualizer_view = (BaseVisualizerView)findViewById(R.id.base_visualizer_view);
        AnimationDrawable drawable = (AnimationDrawable) ivIcon.getBackground();
        drawable.start();
        btnAudioPlaymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioPlayPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnAudioLyric.setOnClickListener(this);

        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2016-06-22 16:09:53 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnAudioPlaymode) {
            setPlaymode();


        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
            try {

                btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_play_pause_selector);
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnAudioPlayPause) {
            // Handle clicks for btnAudioPlayPause
            playAndPause();
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
            try {
                btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_play_pause_selector);
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnAudioLyric) {
            // Handle clicks for btnAudioLyric
        }
    }

    private void setPlaymode() {
        // Handle clicks for btnAudioPlaymode
        try {
            int playmode = service.getPlayMode();
            if(playmode == MusicService.REPEAT_NOMAL) {
                playmode = MusicService.REPEAT_SINGLE;
            }else if(playmode == MusicService.REPEAT_SINGLE) {
                playmode = MusicService.REPEAT_ALL;
            }else if(playmode == MusicService.REPEAT_ALL){
                playmode = MusicService.REPEAT_NOMAL;
            }else {
                playmode = MusicService.REPEAT_NOMAL;
            }
            //保存设置的模式
            service.setPlayMode(playmode);

            showPlayMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlayMode() {
        try {
            int playmode = service.getPlayMode();
            if(playmode == MusicService.REPEAT_NOMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }else if(playmode == MusicService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single);
                Toast.makeText(AudioPlayerActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
            }else if(playmode == MusicService.REPEAT_ALL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all);
                Toast.makeText(AudioPlayerActivity.this, "全曲循环", Toast.LENGTH_SHORT).show();
            }else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal);
                Toast.makeText(AudioPlayerActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * 校验按钮状态
     */
    private void showPlayModeButton() {
        try {
            int playmode = service.getPlayMode();
            if(playmode == MusicService.REPEAT_NOMAL) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal);
            }else if(playmode == MusicService.REPEAT_SINGLE) {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_single);
            }else if(playmode == MusicService.REPEAT_ALL){
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_all);
            }else {
                btnAudioPlaymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void playAndPause() {
        if (service != null) {
            try {
                if (service.isPlaying()) {
                    //暂停
                    service.pause();
                    //按钮-播放状态
                    btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_play_start_selector);
                } else {
                    //播放
                    service.start();
                    //按钮-暂停
                    btnAudioPlayPause.setBackgroundResource(R.drawable.btn_audio_play_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_LYRIC:

                    try {
                        //得到播放进度
                        int currentPosition = service.getCurrentPosition();
                        //根据进度查找该高亮显示的条目
                        show_lyric_view.setShowNext(currentPosition);
                        //不断发消息
                        handler.removeMessages(SHOW_LYRIC);
                        handler.sendEmptyMessage(SHOW_LYRIC);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                    break;
                case PROGRESS:
                    try {
                        int currentPositon = service.getCurrentPosition();
                        int duration = service.getDuration();
                        tvTime.setText(utils.stringForTime(currentPositon) + "/" + utils.stringForTime(duration));
                        seekbarAudio.setProgress(service.getCurrentPosition());

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    handler.removeMessages(PROGRESS);
                    handler.sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_audio_player);

        initData();
        findViews();

        //获取其他页面传递过来的数据
        getData();
        //绑定并启动服务
        bindAndStartService();
    }

    /**
     * 生成一个VisualizerView对象，使音频频谱的波段能够反映到 VisualizerView上
     */
    private void setupVisualizerFxAndUi()
    {

        int audioSessionid = 0;
        try {
            audioSessionid = service.getAudioSessionId();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("audioSessionid==" + audioSessionid);
        mVisualizer = new Visualizer(audioSessionid);
        // 参数内必须是2的位数
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        // 设置允许波形表示，并且捕获它
        base_visualizer_view.setVisualizer(mVisualizer);
        mVisualizer.setEnabled(true);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (isFinishing() /*&& mMediaPlayer != null*/)
        {
            mVisualizer.release();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
        }
    }

    private void initData() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.OPENAUDIO);
        registerReceiver(myReceiver, intentFilter);
        utils = new Utils();
        EventBus.getDefault().register(this);
    }

    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MusicService.OPENAUDIO)) {
                //显示歌曲名称和演唱者信息
                showViewData();
            }
        }
    }

    /**
     *
     * @param item
     */
    public void onEventMainThread(VideoItem item){
        showViewData();
    }
    /**
     * 接收MusicServicer服务的广播,并更新页面
     */
    private void showViewData() {

        try {
            tvArtist.setText(service.getArtist());
            tvName.setText(service.getName());
            seekbarAudio.setMax(service.getDuration());
            handler.sendEmptyMessage(PROGRESS);
            //显示歌词文件
            showLyric();


            showPlayModeButton();//校验按钮状态

            setupVisualizerFxAndUi();//频谱动起来
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示歌词文件分解
     *
     */
    private void showLyric() {

        try {
            //得到播放歌曲的路径
            String audioPath = service.getAudioPath();
            //切歌曲的前缀
            audioPath = audioPath.substring(0,audioPath.indexOf("."));
            //变成歌词
            Log.e("Tag", audioPath);
            File file = new File(audioPath + ".txt");
            if(!file.exists()) {
                file = new File(audioPath + ".Irl");
            }
            //解析歌词
            Log.e("Tag", file+"");
            LyricUtils lyricUtils = new LyricUtils();

            lyricUtils.readLyricFile(file);

            //将歌词初始化入view（绑定）
            ArrayList<Lyric> lyrics = lyricUtils.getLyrics();
            show_lyric_view.setLyrics(lyrics);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        handler.sendEmptyMessage(SHOW_LYRIC);
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction("com.auxluffy.mobileplayer_OPENAUDIO");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);//开启一次新的服务
        startService(intent);//屏蔽多次开启服务
    }


    private void getData() {
        notification = getIntent().getBooleanExtra("notification", false);
        if(!this.notification) {
            //来自列表
            position = getIntent().getIntExtra("position", 0);
        }
    }



    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if(fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
    @Override
    protected void onDestroy() {
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;

        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
