// IMusicService.aidl
package com.auxluffy.mobileplayer;

// Declare any non-default types here with import statements

interface IMusicService {
        /**
         * @param position
         */
        void openAudio(int position);

        /**
         * 开始播放
         */
       void start() ;

        /**
         * 暂停播放
         */
        void pause();

        /**
         * 得到演唱者的信息
         *
         * @return
         */
       String getArtist();

        /**
         * 得到当前播放文件的名称
         *
         * @return
         */
        String getName() ;

        /**
         * 得到当前播放进度
         *
         * @return
         */
        int getCurrentPosition();

        /**
         * 得到当前文件时长
         *
         * @return
         */
        int getDuration();

        /**
         * 拖动音频
         *
         * @param position
         */
        void seekTo(int position);

        /**
         * 得到播放模式
         * @return
         */
        int getPlayMode();

        /**
         * 设置播放模式
         */
        void setPlayMode(int playMode);

        /**
         * 播放前一个视频
         */
        void pre();

        /**
         * 播放下一个视频
         */
        void next();

        /**
        * 判断是否正在播放音频
        */
        boolean isPlaying();

        void notifyChange(String action);

        String getAudioPath();

        /**
        * 获得音乐的sessionId
        */
        int getAudioSessionId();
}
