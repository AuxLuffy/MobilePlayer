package com.auxluffy.mobileplayer.pagers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auxluffy.mobileplayer.R;
import com.auxluffy.mobileplayer.activity.AudioPlayerActivity;
import com.auxluffy.mobileplayer.base.BasePager;
import com.auxluffy.mobileplayer.domain.VideoItem;
import com.auxluffy.mobileplayer.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 本地音乐
 * Created by Administrator on 2016/6/16.
 */
public class AudioPager extends BasePager {

    private ListView lv_video;
    private TextView tv_novideo;
    private VideoAdapter adpter;
    private ProgressBar progressbar;

    List<VideoItem> list;
    Utils utils;


    public AudioPager(Context context) {
        super(context);
        utils = new Utils();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        lv_video = (ListView) view.findViewById(R.id.lv_video);
        tv_novideo = (TextView) view.findViewById(R.id.tv_novideo);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        lv_video.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener{

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            VideoItem item = list.get(position);

            /*Intent intent = new Intent();
            intent.setDataAndType(Uri.parse(item.getData()),"video*//*");//播放一个视频(video*//*),让系统决定谁来播放
            context.startActivity(intent);*/
            Intent intent = new Intent(context, AudioPlayerActivity.class);
//            intent.putExtra("list",(ArrayList<VideoItem>)list);

            intent.putExtra("position",position);
            //intent.setDataAndType(Uri.parse(item.getData()),"video/*");
            context.startActivity(intent);
        }
    }

    @Override
    public void initData() {

        getData();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:

                    if(list!=null && list.size()>0) {
                        adpter = new VideoAdapter();
                        lv_video.setAdapter(adpter);
                        tv_novideo.setVisibility(View.GONE);
                    }else {
                        tv_novideo.setVisibility(View.VISIBLE);
                        tv_novideo.setText("没有找到音频文件...");
                    }
                    progressbar.setVisibility(View.GONE);
                    break;
            }
        }
    };

    /**
     *
     */
    public void getData() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                ContentResolver resolver = context.getContentResolver();
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION,
                        MediaStore.Audio.Media.SIZE,
                        MediaStore.Audio.Media.DATA,//视频播放的地址
                        MediaStore.Audio.Media.ARTIST
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor!=null) {
                    list = new ArrayList<VideoItem>();
                    while (cursor.moveToNext()){

                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);
                        VideoItem item = new VideoItem(name,duration,size,data,artist);
                        list.add(item);
                    }
                }

                handler.sendEmptyMessage(1);
            }
        }.start();
    }

    class VideoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public VideoItem getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = View.inflate(context,R.layout.item_video_pager,null);
                holder = new ViewHolder();
                holder.tv_duration = (TextView) convertView.findViewById(R.id.tv_duration);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_size = (TextView) convertView.findViewById(R.id.tv_size);
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                convertView.setTag(holder);
                //VideoItem item = list.get(position);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            VideoItem item = list.get(position);
            holder.tv_name.setText(item.getName());
            holder.tv_duration.setText(utils.stringForTime((int) item.getDuration()));
            holder.tv_size.setText(Formatter.formatFileSize(context, item.getSize()));
            holder.iv_icon.setImageResource(R.drawable.music_default_bg);
            return convertView;
        }
    }
    class ViewHolder{
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
        ImageView iv_icon;
    }
}
