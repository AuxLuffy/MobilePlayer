package com.auxluffy.mobileplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.auxluffy.mobileplayer.activity.MainActivity;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends Activity {

    Timer timer;
    boolean isJump;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        (timer = new Timer()).schedule(new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }, 2000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isJump) {
            isJump = true;
            timer.cancel();
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        }
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        timer.cancel();
    }
}
