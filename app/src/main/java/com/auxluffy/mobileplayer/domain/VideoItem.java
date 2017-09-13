package com.auxluffy.mobileplayer.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/17.
 */
public class VideoItem implements Serializable{
    private String name;
    private long duration;
    private long size;
    private String data;//播放地址
    private String artist;

    private String imageUrl;
    private String desc;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public VideoItem() {
    }

    public VideoItem(String name, long duration, long size, String data, String artist) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.artist = artist;
    }

    public VideoItem(String name, long duration, long size, String data, String artist, String imageUrl, String desc) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.artist = artist;
        this.imageUrl = imageUrl;
        this.desc = desc;
    }

    public VideoItem(String name, String desc, String imageUrl, String data) {
        this.name = name;
        this.desc = desc;
        this.imageUrl = imageUrl;
        this.data = data;
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
