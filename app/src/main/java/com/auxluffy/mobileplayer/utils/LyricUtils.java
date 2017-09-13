package com.auxluffy.mobileplayer.utils;

import android.util.Log;

import com.auxluffy.mobileplayer.domain.Lyric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Administrator on 2016/6/24.
 */
public class LyricUtils {
    //难点
    private ArrayList<Lyric> lyrics;
    public void readLyricFile(File file) {
        if(file==null||!file.exists()) {
            //歌词文件不存在
            lyrics = null;
        }else {
            //歌词文件存在
            lyrics = new ArrayList<>();
            //解析歌词
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),getCharset(file)));
                String line;
                while ((line = br.readLine())!=null){
                    parseLyric(line);

                }
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //歌词排序
            Collections.sort(lyrics);
            //计算每句歌词的高亮时间
            for (int i = 0; i < lyrics.size(); i++) {
                Lyric oneLyric = lyrics.get(i);
                if(i+1<lyrics.size()) {
                    Lyric twoLyric = lyrics.get(i+1);
                    oneLyric.setSleepTime(twoLyric.getTimePoint()-oneLyric.getTimePoint());
                }
            }
            //

        }
    }

    /**
     * 解析任一句歌词得到歌词内容
     * 格式：[02:04.12][03:37.32][00:59.73]我在这里欢笑
     * @param line
     * @return
     */
    private void parseLyric(String line) {
        ArrayList<Long> positions = new ArrayList<>();
        while (line!=null) {
            int pos1 = line.indexOf("[");//第一个"["出现的位置
            int pos2 = line.indexOf("]");//第一个"]"出现的位置

            if(pos1==0&&pos2!=-1) {//不一定是歌词
                String point = line.substring(pos1+1,pos2);
                Log.e("TAG", line);
                long position = str2Long(point);
                if(position==-1) {
                    return;
                }
                positions.add(position);
                line = line.substring(pos2+1);
            }else {
                break;
            }
        }
        if(positions.size()>0) {
            for (long position : positions){
                Lyric lrc = new Lyric();
                lrc.setContent(line);
                lrc.setTimePoint(position);
                lyrics.add(lrc);
            }
        }

    }

   
    /**
     * 将得到的时间字符串转化为
     * @param point
     * @return
     */
    private long str2Long(String point) {

        String[] sections = point.split(":");
        long time = -1;
        try {
            time = Long.parseLong(sections[0])*60*1000+(long)Float.parseFloat(sections[1])*1000;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return time;
    }

    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }
    /**
     * 判断文件编码
     * @param file 文件
     * @return 编码：GBK,UTF-8,UTF-16LE
     */
    public String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

}
