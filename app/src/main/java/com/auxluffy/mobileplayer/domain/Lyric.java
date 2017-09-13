package com.auxluffy.mobileplayer.domain;

/**
 * 创建歌词对象
 * 作用：一句歌词
 */
public class Lyric implements Comparable<Lyric> {
    /**
     * 歌词内容
     */
    private String content;
    /**
     * 时间戳
     */
    private long timePoint;
    /**
     * 高亮时间
     */
    private long sleepTime;

    public Lyric() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public int compareTo(Lyric another) {
        return (int) (this.timePoint - another.getTimePoint());
    }
}
