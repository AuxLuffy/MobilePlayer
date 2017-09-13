package com.auxluffy.mobileplayer.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.auxluffy.mobileplayer.R;
import com.auxluffy.mobileplayer.base.BasePager;
import com.auxluffy.mobileplayer.pagers.AudioPager;
import com.auxluffy.mobileplayer.pagers.NetAudioPager;
import com.auxluffy.mobileplayer.pagers.NetVideoPager;
import com.auxluffy.mobileplayer.pagers.VideoPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/16.
 */
public class MainActivity extends FragmentActivity {

    public MainActivity(){

    }
    private List<BasePager> basePagers;
    private int position;
    private RadioGroup rg_main;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        basePagers = new ArrayList<>();
        basePagers.add(new VideoPager(this));
        basePagers.add(new AudioPager(this));
        basePagers.add(new NetVideoPager(this));
        basePagers.add(new NetAudioPager(this));

        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        rg_main.check(R.id.rb_video);

    }

    public BasePager getBasePager() {
        BasePager basePager = basePagers.get(position);
        if(basePager!=null && !basePager.isInitData) {
            basePager.initData();
            basePager.isInitData = true;
        }
        return basePagers.get(position);
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_video:
                    position = 0;
                    break;
                case R.id.rb_audio:
                    position = 1;
                    break;
                case R.id.rb_net_video:
                    position = 2;
                    break;
                case R.id.rb_net_audio:
                    position = 3;
                    break;
            }
            setFragment();
        }
    }

    private void setFragment() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fl_main, new Fragment() {
            @Nullable
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                BasePager basePager = getBasePager();
                return basePager.rootView;
            }
        });
        ft.commit();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    public boolean isExit = false;
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(position!=0) {
//                position = 0;     //这种方式有bug,radiobutton的状态不更新
//                setFragment();        //所以用setchecked的方式来更新其状态
                rg_main.check(R.id.rb_video);
                return true;
            }else if(!isExit) {
                Toast.makeText(MainActivity.this, "再按一次退出！", Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                },2000);
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
