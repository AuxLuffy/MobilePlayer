package com.auxluffy.mobileplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auxluffy.mobileplayer.R;
import com.auxluffy.mobileplayer.domain.VideoItem;
import com.auxluffy.mobileplayer.utils.LogUtil;
import com.auxluffy.mobileplayer.utils.Utils;
import com.auxluffy.mobileplayer.view.VitamioVideoView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;

/**
 * Created by Administrator on 2016/6/17.
 */
public class VitamioVideoPlayer extends Activity implements View.OnClickListener {
    private static final int HIDE_CONTROLLER = 3;
    /**
     * 显示网速
     */
    private static final int SHOW_NET_SPEED = 2;
    //接收到的列表数据
    private ArrayList<VideoItem> list;
    private final int VIDEO_SEEKBAR_UPDATE = 1;
    //接收到的视频对象在list中的位置
    private int position;
    //VideoView对象，视频播放器对放器
    private VitamioVideoView vv_item;
    //手势识别器
    GestureDetector detector;
    //声音管理器
    AudioManager audioManager;
    //View中的子视图
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystemtime;
    private Button btnVideoVoice;
    private SeekBar seekbarVoice;
    private Button btnVideoSwichPlayer;
    private LinearLayout llBottom;
    private TextView tvCurrent;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnVideoExit;
    private Button btnVideoPre;
    private Button btnVideoPlayPause;
    private Button btnVideoNext;
    private Button btnVideoSwichScreen;
    //当前播放的条目对象
    private VideoItem item;
    //加载网络视频的进度条:加载视频前的进度条
    private LinearLayout ll_loading;
    private TextView tv_netspeed;
    //网络视频的监听卡:加载视频后的卡顿显示
    private LinearLayout ll_buffer;
    private TextView tv_buffer_speak;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_NET_SPEED:
                    String speed = util.getNetSpeed(VitamioVideoPlayer.this);
                    tv_buffer_speak.setText(speed);
                    tv_netspeed.setText(speed);
                    removeMessages(SHOW_NET_SPEED);
                    sendEmptyMessageDelayed(SHOW_NET_SPEED, 1000);
                    break;
                case VIDEO_SEEKBAR_UPDATE:
                    int currentTime = (int) vv_item.getCurrentPosition();
                    seekbarVideo.setProgress(currentTime);
                    tvCurrent.setText(util.stringForTime(currentTime));
                    tvSystemtime.setText(getSystemTime());

                    //视频缓冲进度的更新
                    if (isNetUri) {
                        //更新缓冲进度
                        int bufferPercentage = vv_item.getBufferPercentage();
                        int secondaryProgress = bufferPercentage * seekbarVideo.getMax() / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);

                    } else {
                        seekbarVideo.setSecondaryProgress(0);
                    }
                    currentPosition = (int) vv_item.getCurrentPosition();
                    int buffer = currentPosition - prePosition;
                    if (vv_item.isPlaying()) {
                        if (buffer < 500) {
                            //播放卡了
                            ll_buffer.setVisibility(View.VISIBLE);
                        } else {
                            //播放不卡
                            ll_buffer.setVisibility(View.GONE);
                        }
                    }

                    prePosition = currentPosition;
                    handler.removeMessages(VIDEO_SEEKBAR_UPDATE);
                    handler.sendEmptyMessageDelayed(VIDEO_SEEKBAR_UPDATE, 1000);
                    break;
                case HIDE_CONTROLLER:
                    hideMediaController();
                    break;

            }
        }
    };
    //private int currentTime;
    private Utils util;
    private BatteryReciever reciever;
    private boolean isShowMediaController;
    private int screenWidth;
    private int screenHeight;
    private int videoWidth;
    private int videoHeight;
    private boolean isFullScreen = false;
    private int maxVolume;
    private int currentVolume;

    private boolean isMute = false;
    private boolean isNetUri;
    private boolean isFromUri = false;
    /**
     * 上一秒播放的进度
     */
    private int prePosition;
    /**
     * 当前播放进度
     */
    private int currentPosition;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2016-06-17 22:33:55 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {

        vv_item = (VitamioVideoView) findViewById(R.id.vv_item);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystemtime = (TextView) findViewById(R.id.tv_systemtime);
        btnVideoVoice = (Button) findViewById(R.id.btn_video_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnVideoSwichPlayer = (Button) findViewById(R.id.btn_video_swich_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrent = (TextView) findViewById(R.id.tv_current);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnVideoExit = (Button) findViewById(R.id.btn_video_exit);
        btnVideoPre = (Button) findViewById(R.id.btn_video_pre);
        btnVideoPlayPause = (Button) findViewById(R.id.btn_video_play_pause);
        btnVideoNext = (Button) findViewById(R.id.btn_video_next);
        btnVideoSwichScreen = (Button) findViewById(R.id.btn_video_swich_screen);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_buffer_speak = (TextView) findViewById(R.id.tv_buffer_speak);
        tv_netspeed = (TextView) findViewById(R.id.tv_netspeed);

        btnVideoVoice.setOnClickListener(this);
        btnVideoSwichPlayer.setOnClickListener(this);
        btnVideoExit.setOnClickListener(this);
        btnVideoPre.setOnClickListener(this);
        btnVideoPlayPause.setOnClickListener(this);
        btnVideoNext.setOnClickListener(this);
        btnVideoSwichScreen.setOnClickListener(this);

        seekbarVoice.setMax(maxVolume);
        seekbarVoice.setProgress(currentVolume);

        handler.sendEmptyMessage(SHOW_NET_SPEED);
    }

    /**
     * 所有控制按钮的监听方法
     */
    @Override
    public void onClick(View v) {
        if (v == btnVideoVoice) {
            // Handle clicks for btnVideoVoice
            if (!isMute) {
                seekbarVoice.setProgress(0);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                isMute = true;
            } else {
                seekbarVoice.setProgress(currentVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                isMute = false;
            }
        } else if (v == btnVideoSwichPlayer) {
            // Handle clicks for btnVideoSwichPlayer
            showSwitchPlayerDialog();

        } else if (v == btnVideoExit) {
            // Handle clicks for btnVideoExit
            finish();
        } else if (v == btnVideoPre) {//当按下上一个视频的按钮时的动作
            // Handle clicks for btnVideoPre
            preVideo();

        } else if (v == btnVideoPlayPause) {//当按下停止播放按钮时的动作
            // Handle clicks for btnVideoPlayPause
            PlayOrPause();


        } else if (v == btnVideoNext) {//当按下下一个视频的按钮时的动作
            // Handle clicks for btnVideoNext
            nextVideo();

        } else if (v == btnVideoSwichScreen) {//当按下屏幕切换时执行，实现全屏/默认切换
            // Handle clicks for btnVideoSwichScreen
            switchScreenType();
        }
        handler.removeMessages(HIDE_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);

    }
    private void showSwitchPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("切换播放器");
        builder.setMessage("当前是万能播放器，是否切换至系统播放器？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startSystemVideoPlayer();
            }
        });
        builder.show();
    }

    private void startSystemVideoPlayer() {
        if(vv_item!=null) {//
            vv_item.stopPlayback();
        }
        Intent intent = new Intent(this,VideoPlayerActivity.class);
        if(list!=null&&list.size()>0) {
            intent.putExtra("list",list);
            intent.putExtra("position", position);

        }else if(uri != null) {
            intent.setData(uri);
        }
        startActivity(intent);
        finish();
    }

    private void PlayOrPause() {
        if (vv_item.isPlaying()) {//当正在播放时，就开始播放，将背景设置为playselector,并将视频停止
            //handler.removeMessages(VIDEO_SEEKBAR_UPDATE);
            vv_item.pause();
            btnVideoPlayPause.setBackgroundResource(R.drawable.btn_video_play_selector);

        } else {//当播放停止时，就停止播放，将背景设置为pauseselector，并开启视频播放

            vv_item.start();
            btnVideoPlayPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
            handler.sendEmptyMessage(VIDEO_SEEKBAR_UPDATE);
        }
    }

    /**
     * 开启下一个视频
     */
    private void nextVideo() {
        position++;
        if (position < list.size()) {
            setData();
            startNewVideoUi();
            ll_loading.setVisibility(View.VISIBLE);
        } else {
            finish();
        }

    }

    /**
     * 开启上一个视频
     */
    private void preVideo() {
        position--;
        if (position >= 0) {
            setData();
            startNewVideoUi();
            ll_loading.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    /**
     * 播放新视频时重置所有按钮的状态
     */
    private void resetBtnState() {

        if (!isFromUri) {
            btnVideoNext.setEnabled(true);
            btnVideoNext.setBackgroundResource(R.drawable.btn_video_next_selector);
            btnVideoPre.setEnabled(true);
            btnVideoPre.setBackgroundResource(R.drawable.btn_video_pre_selector);
            if (position == 0) {
                btnVideoPre.setEnabled(false);
                btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
            }
            if (position == list.size() - 1) {
                btnVideoNext.setEnabled(false);
                btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            }
        } else {
            btnVideoNext.setEnabled(false);
            btnVideoNext.setBackgroundResource(R.drawable.btn_next_gray);
            btnVideoPre.setEnabled(false);
            btnVideoPre.setBackgroundResource(R.drawable.btn_pre_gray);
        }


        btnVideoPlayPause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_full_selector);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);//初始化vitamio库
        setContentView(R.layout.activity_vitamio_video_player);
        initData();
        findViews();

        //获取其他页面传递过来的数据
        getData();
        //设置播放器数据
        setData();
        //只有第一次启动时主动隐藏控制面板
        hideMediaController();
        isShowMediaController = false;
        //设置监听
        setListeners();
        //vv_item.setMediaController(new MediaController(this));


    }

    /**
     * 初始化数据
     * utils  reciever电量广播接收
     * detector手势识别器
     * 屏幕宽高   音频管理器
     * 当前音量   最大音量
     */
    private void initData() {
        //currentTime = 0;

        util = new Utils();
        reciever = new BatteryReciever();

        //注册一个广播来接收电量，注册的广播也要取消，所以可以把它设成一个成员变量
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(reciever, intentFilter);
        //实例化手势识别器
        detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {

                PlayOrPause();
                super.onLongPress(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                switchScreenType();
                return super.onDoubleTap(e);

            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (isShowMediaController) {
                    handler.removeMessages(HIDE_CONTROLLER);

                    handler.sendEmptyMessage(HIDE_CONTROLLER);
                    isShowMediaController = false;
                } else {
                    showMediaController();
                    isShowMediaController = true;
                    handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);

                }
                return super.onSingleTapConfirmed(e);
            }
        });
        /*//获取屏幕宽度方式一:
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = getWindowManager().getDefaultDisplay().getHeight();*/

        //获取屏幕宽度方式二:
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);


    }

    /**
     * 改全屏切换
     */
    private void switchScreenType() {
        if (isFullScreen) {
            vv_item.setVideoSize(videoWidth, videoHeight);
            isFullScreen = false;
            btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_full_selector);

        } else {
            vv_item.setVideoSize(screenWidth, screenHeight);
            isFullScreen = true;
            btnVideoSwichScreen.setBackgroundResource(R.drawable.btn_video_swich_screen_default_selector);
        }
    }

    /**
     * 滑动的起始坐标
     */
    private float startY;
    /**
     * 在屏幕滑动的最大距离
     */
    private int touchRang;

    /**
     * 当开始滑动这个时刻的当前音量
     */
    private int mVol;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://当手指按下的时候执行
                //1.记录起始坐标
                startY = event.getY();
                touchRang = Math.min(screenHeight, screenWidth);//screenHeight
                mVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(HIDE_CONTROLLER);
                break;
            case MotionEvent.ACTION_MOVE://当手指在屏幕滑动的时候执行
                //2.来到新的坐标
                float endY = event.getY();
                float distanceY = startY - endY;
                //屏幕滑动的距离 ： 总距离 = 改变声音 ： 总声音
                //改变声音 = （屏幕滑动的距离 ： 总距离）* 总声音
                float delta = (distanceY / touchRang) * maxVolume;

                //要设置的声音 = 原来的声音+ 改变声音；
                int volume = (int) Math.min(Math.max(mVol + delta, 0), maxVolume);

                if (delta != 0) {
                    updateVoiceSeekBar(volume);
                }


                break;
            case MotionEvent.ACTION_UP://当手指离开屏幕的时候执行
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        llTop.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);

    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);

    }

    /**
     * 返回一个字符串表示的时间格式: HH:mm:ss
     *
     * @return
     */
    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    public void setBattery(int battery) {
        if (battery <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (battery <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);
        } else if (battery <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);
        } else if (battery <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);
        } else if (battery <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);
        } else if (battery <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);
        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);
        }
    }

    class BatteryReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);//得到电量 0~100
            setBattery(level);

        }
    }

    /**
     * 所有监听器的设置
     */
    private void setListeners() {
        //视频加载成功的监听
        vv_item.setOnPreparedListener(new MyVideoViewOnPreparedListener());
        //视频出错的监听
        vv_item.setOnErrorListener(new MyVideoViewOnErrorListener());
        //视频播放完成的监听
        vv_item.setOnCompletionListener(new MyVideoViewOnCompletionListener());
        //视频进度的进度条拖动监听
        seekbarVideo.setOnSeekBarChangeListener(new MyVideoOnSeekBarChangeListener());
        //声音进度条拖动监听
        seekbarVoice.setOnSeekBarChangeListener(new MyVoiceOnSeekBarChangeListener());
        //视频监听卡
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        //android2.3以上才有的监听卡，4.2才把监听卡封装到VideoView中的
        // vv_item.setOnInfoListener(new MyOnInfoListener());
        //}
        //android2.3以前是怎么监听卡呢？
        //通过判断前一秒与下一秒的播放进度是不是相同或相差多与否
    }

    class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始卡，拖动卡
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END://结束卡，结束拖动卡
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

    class MyVideoViewOnCompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

            nextVideo();
        }
    }

    class MyVideoViewOnErrorListener implements MediaPlayer.OnErrorListener {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //1.视频文件不支持--切换到万能播放器播放
            //2.播放的文件中有损坏 或者说网络断断续续，重新播放
            //3.播放的文件中间有损坏--下载解决bug
            showErroDialog();
            return true;
        }
    }

    private void showErroDialog() {
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("视频播放出错，请检查地址是否有误或视频是否损坏！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    class MyVideoViewOnPreparedListener implements MediaPlayer.OnPreparedListener {

        @Override
        public void onPrepared(MediaPlayer mp) {
            Log.e("TAG", "111111111");
            //真实视频的宽高
            int mVideoHeight = mp.getVideoHeight();
            int mVideoWidth = mp.getVideoWidth();
            videoHeight = screenHeight;
            videoWidth = screenWidth;
            if (mVideoWidth * screenHeight < screenWidth * mVideoHeight) {

                videoWidth = screenHeight * mVideoWidth / mVideoHeight;
                LogUtil.e("" + videoWidth);
            } else if (mVideoWidth * screenHeight > screenWidth * mVideoHeight) {

                videoHeight = screenWidth * mVideoHeight / mVideoWidth;
                LogUtil.e("" + videoHeight);
            }
            Log.e("TAG", "vv_item-1");
            vv_item.start();
            Log.e("TAG", "vv_item+1");
            //vv_item.setKeepScreenOn(true);
            //设置屏幕常亮
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            vv_item.setVideoSize(videoWidth, videoHeight);
            isFullScreen = false;
            startNewVideoUi();//ui的初始化

            handler.sendEmptyMessage(VIDEO_SEEKBAR_UPDATE);
            //缓冲进度设为不可见
            ll_loading.setVisibility(View.GONE);
        }
    }

    class MyVoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {//千万别忘记写

                updateVoiceSeekBar(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_CONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 1000);
        }
    }

    /**
     * 更新声单进度条
     *
     * @param progress
     */
    private void updateVoiceSeekBar(int progress) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        seekbarVoice.setProgress(progress);
        currentVolume = progress;
        if (currentVolume == 0) {
            isMute = true;
        } else {
            isMute = false;
        }
    }

    /**
     * 视频进度的进度条拖动监听
     */
    class MyVideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 当进度条改变时调 用
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {

                vv_item.seekTo(progress);
            }

        }

        /**
         * 当手指触碰时调 用
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //handler.removeMessages(VIDEO_SEEKBAR_UPDATE);
            handler.removeMessages(HIDE_CONTROLLER);
        }

        /**
         * 手指离开时调用
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
        }
    }

    /**
     * 新开一个视频时需要做的ui初始化
     */
    private void startNewVideoUi() {
        resetBtnState();
        seekbarVideo.setMax((int) vv_item.getDuration());
        tvDuration.setText(util.stringForTime((int) vv_item.getDuration()));
        seekbarVideo.setProgress(0);
        tvDuration.setText(util.stringForTime((int) vv_item.getDuration()));
        tvCurrent.setText("0:00");
    }

    /**
     * 设置播放器数据:地址
     * 条目名称设置
     * 得到相应视频的宽度
     */
    private void setData() {
        //得到item对应的地址并加载至videoView
        if (list != null && list.size() > 0) {
            isFromUri = false;
            item = list.get(position);
            isNetUri = util.isNetUrl(item.getData());
            vv_item.setVideoPath(item.getData());
            tvName.setText(item.getName());
        } else if (uri != null) {
            isFromUri = true;
            vv_item.setVideoURI(uri);
            isNetUri = util.isNetUrl(uri.toString());
            tvName.setText(uri.toString());
            list = new ArrayList<>();
        }

    }

    Uri uri;

    /**
     * 获取其他页面传递过来的数据
     */
    private void getData() {
        //获得上一个activity的数据


        Intent intent = getIntent();
        uri = intent.getData();

        list = (ArrayList<VideoItem>) intent.getSerializableExtra("list");
        position = intent.getIntExtra("position", 0);
        
        /*if(uri!=null) {
            list = new ArrayList<>();
            private String name;
            private long duration;
            private long size;
            private String data;
            private String artist;


            list.add(new VideoItem(uri.toString(),0,0,null,null));
        }*/
    }

    private List<VideoItem> itemsFromUri(Uri uri) {

        return null;
    }

    boolean isFirstMute = true;
    boolean isFirstMaxVol = true;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            handler.removeMessages(HIDE_CONTROLLER);
            showMediaController();
            int volume = currentVolume - 1;
            isFirstMute = true;
            if (volume >= 0) {
                updateVoiceSeekBar(volume);
            } else {
                if (isFirstMaxVol) {

                    Toast.makeText(VitamioVideoPlayer.this, "已是最小音量!", Toast.LENGTH_SHORT).show();
                    isFirstMaxVol = false;
                }
            }
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            handler.removeMessages(HIDE_CONTROLLER);
            showMediaController();
            int volume = currentVolume + 1;
            isFirstMaxVol = true;
            if (volume <= maxVolume) {
                updateVoiceSeekBar(volume);
            } else {

                if (isFirstMute) {
                    Toast.makeText(VitamioVideoPlayer.this, "已是最大音量！", Toast.LENGTH_SHORT).show();
                    isFirstMute = false;
                }
            }
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 3000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //当释放资源时先放父类的再放子类的
        super.onDestroy();
        LogUtil.e("onDestroy()");
        handler.removeCallbacksAndMessages(null);
        if (reciever != null) {
            unregisterReceiver(reciever);
            reciever = null;
        }
    }
}
