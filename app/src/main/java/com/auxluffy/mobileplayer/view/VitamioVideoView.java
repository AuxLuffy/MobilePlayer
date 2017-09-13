package com.auxluffy.mobileplayer.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/6/20.
 */
public class VitamioVideoView extends io.vov.vitamio.widget.VideoView {


    public VitamioVideoView(Context context) {
        super(context);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 重写测量方法
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    public void setVideoSize(int width,int height){
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = width;
        params.height = height;
        setLayoutParams(params);
    }

}
