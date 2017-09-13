package com.auxluffy.testuriconn;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_start_all_videoplayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setDataAndType(Uri.parse("http://192.168.191.1:8080/rmvb.rmvb"), "video/*");
                startActivity(intent);
            }
        });

    }


}
