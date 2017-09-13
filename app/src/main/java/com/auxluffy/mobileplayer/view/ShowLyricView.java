package com.auxluffy.mobileplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.auxluffy.mobileplayer.domain.Lyric;
import com.auxluffy.mobileplayer.utils.DensityUtil;

import java.util.ArrayList;

/**
 * 作者：杨光福 on 2016/6/24 09:05
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：歌词显示控件
 */
public class ShowLyricView extends TextView {
    private ArrayList<Lyric> lyrics;
    private int width;
    private int height;
    private Paint paint;
    /**
     * 歌词索引-第几句歌词
     */
    private int index;
    private float textHeight = DensityUtil.dip2px(getContext(),20);
    private Paint whitepaint;
    private float currentPosition;
    /**
     * 高亮显示时间
     */
    private float sleepTime;
    /**
     * 时间戳
     */
    private float timePoint;

    public ShowLyricView(Context context) {
        this(context, null);
    }

    public ShowLyricView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShowLyricView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        /**
         * 控件的高和宽
         */
        width = w;
        height = h;
    }

    private void initView() {


//        lyrics = new ArrayList<>();
//
//
//        Lyric lyric = new Lyric();
//        for (int i = 0; i < 1000; i++) {
//            lyric.setContent(i + "aaaaaaaaa" + i);
//            lyric.setSleepTime(1000 + i);
//            lyric.setTimePoint(i * 1000);
//            //添加到集合中
//            lyrics.add(lyric);
//            lyric = new Lyric();
//        }


        //初始化画笔
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);//文字中间对齐
        paint.setTextSize(DensityUtil.dip2px(getContext(),20));

        whitepaint = new Paint();
        whitepaint.setColor(Color.WHITE);
        whitepaint.setAntiAlias(true);
        whitepaint.setTextAlign(Paint.Align.CENTER);//文字中间对齐
        whitepaint.setTextSize(DensityUtil.dip2px(getContext(),20));


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (lyrics != null && lyrics.size() > 0) {

            //平移动画
            float push = 0;
            if(sleepTime ==0){
                push = 0;
            }else{
                //平移这一行百分之几
                //所花的时间：休眠时间 = 平移的距离： 一行的高
                //平移的距离 = (所花的时间：休眠时间) * 一行的高
//                float distanceY = ((currentPosition-timePoint)/sleepTime)*textHeight;

                //转换成坐标 = 行高 + 平移的距离；
                push = textHeight + ((currentPosition-timePoint)/sleepTime)*textHeight;
            }

            canvas.translate(0,-push);


            //有歌词
            //1.绘制当前句;
            String content = lyrics.get(index).getContent();//歌词内容
            canvas.drawText(content, width / 2, height / 2, paint);

            float tempY = height / 2;
            // 2.绘制前面部分；
            for (int i = index - 1; i >= 0; i--) {
                String preContent = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preContent, width / 2, tempY, whitepaint);
            }
            // 3.绘制后面部分
            tempY = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextContent = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > height) {
                    break;
                }
                canvas.drawText(nextContent, width / 2, tempY, whitepaint);
            }
        } else {
            //没有歌词

            canvas.drawText("没有发现歌词...", width / 2, height / 2, paint);
        }
    }

    /**
     * 计算该高亮显示哪一句歌词
     *
     * @param currentPosition
     */
    public void setShowNext(int currentPosition) {
        this.currentPosition = currentPosition;
        if (lyrics == null || lyrics.size() == 0)
            return;

        for (int i = 1; i < lyrics.size(); i++) {


            if (currentPosition < lyrics.get(i).getTimePoint()) {

                int tempIndex = i - 1;//0//1

                if (currentPosition >= lyrics.get(tempIndex).getTimePoint()) {

                    index = tempIndex;//要找的这句的索引

                    sleepTime = lyrics.get(index).getSleepTime();

                    timePoint = lyrics.get(index).getTimePoint();

                }


            }

        }

        //强制重新绘制
        invalidate();//主线程，执行onDraw
//        postInvalidate();//子线程
    }

    /**
     * 设置歌词列表
     * @param lyrics
     */
    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics= lyrics;
    }
}
