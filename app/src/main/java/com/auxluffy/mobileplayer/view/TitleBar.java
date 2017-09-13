package com.auxluffy.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.auxluffy.mobileplayer.R;

/**
 * Created by Administrator on 2016/6/17.
 */
public class TitleBar extends LinearLayout {
    private TextView tv_search;
    private RelativeLayout rl_game;
    private ImageView iv_history;
    public TitleBar(Context context) {
        super(context);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tv_search = (TextView) getChildAt(1);
        rl_game = (RelativeLayout) getChildAt(2);
        iv_history = (ImageView) getChildAt(3);
        MyOnclickListener listener = new MyOnclickListener();
        tv_search.setOnClickListener(listener);
        rl_game.setOnClickListener(listener);
        iv_history.setOnClickListener(listener);
    }

    class MyOnclickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_search:
                    Toast.makeText(getContext(), "搜索全网", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.rl_game:
                    Toast.makeText(getContext(), "游戏", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.iv_history:
                    Toast.makeText(getContext(), "播放历史", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    }
}
