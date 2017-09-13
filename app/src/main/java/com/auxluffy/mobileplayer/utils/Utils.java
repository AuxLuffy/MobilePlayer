package com.auxluffy.mobileplayer.utils;

import android.content.Context;
import android.net.TrafficStats;

import java.util.Formatter;
import java.util.Locale;

public class Utils {

    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    public Utils() {
        // 转换成字符串的时间
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    }

    /**
     * 把毫秒转换成：1:20:30这里形式
     *
     * @param timeMs
     * @return
     */
    public String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;

        int minutes = (totalSeconds / 60) % 60;

        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds)
                    .toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 判断是否是网络视频
     *
     * @param data
     * @return
     */
    public boolean isNetUrl(String data) {
        boolean result = false;
        if (data != null) {
            if (data.toLowerCase().startsWith("http") || data.toLowerCase().startsWith("rtsp") || data.toLowerCase().startsWith("mms")) {
                result = true;
            }

        }
        return result;
    }

    private long lastTotoalRxBytes = 0;
    private long lastTimeStamp = 0;

    /**
     * 得到当前手机的网络速度
     * 每隔一秒去调用一次（这里没有具体体现是1秒）
     * @param context
     * @return
     */

    public String getNetSpeed(Context context) {
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotoalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        lastTimeStamp = nowTimeStamp;
        lastTotoalRxBytes = nowTotalRxBytes;
        String speedString =  String.valueOf(speed)+" kb/s";
        return speedString;
    }



}
