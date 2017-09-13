package com.auxluffy.mobileplayer.domain;

import java.util.List;

/**
 * Created by Administrator on 2016/6/25.
 */
public class JsonItem {


    /**
     * count : 4105
     * np : 1466816281
     */

    private InfoBean info;
    /**
     * comment : 23
     * tags : [{"id":18910,"name":"hx"},{"id":9480,"name":"链接"},{"id":55,"name":"微视频"}]
     * bookmark : 43
     * text : 当年薛之谦帮助过的那位老奶奶的视频采访。他虽不曾被这世界温柔以待，却依然保持着善良之心。
     * up : 618
     * share_url : http://b.f.costrub.com/share/19040429.html?wx.qq.com
     * down : 50
     * forward : 59
     * u : {"header":["http://wimg.spriteapp.cn/profile/large/2016/05/30/574bb6d60037d_mini.jpg","http://dimg.spriteapp.cn/profile/large/2016/05/30/574bb6d60037d_mini.jpg"],"is_v":true,"uid":"5083551","is_vip":false,"name":"百思姐夫V武大狼"}
     * passtime : 2016-06-25 11:48:01
     * video : {"playfcount":220,"height":360,"width":640,"video":["http://bvideo.spriteapp.cn/video/2016/0623/576bf47b513ed_wpd.mp4","http://wvideo.spriteapp.cn/video/2016/0623/576bf47b513ed_wpd.mp4"],"duration":358,"playcount":5559,"thumbnail":["http://wimg.spriteapp.cn/picture/2016/0623/576bf47b17705__b.jpg","http://dimg.spriteapp.cn/picture/2016/0623/576bf47b17705__b.jpg"],"download":["http://bvideo.spriteapp.cn/video/2016/0623/576bf47b513ed_wpc.mp4","http://wvideo.spriteapp.cn/video/2016/0623/576bf47b513ed_wpc.mp4"]}
     * type : video
     * id : 19040429
     */

    private List<ListBean> list;

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class InfoBean {
        private int count;
        private int np;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getNp() {
            return np;
        }

        public void setNp(int np) {
            this.np = np;
        }
    }

    public static class ListBean {
        private String comment;
        private String bookmark;
        private String text;
        private String up;
        private String share_url;
        private int down;
        private int forward;
        /**
         * header : ["http://wimg.spriteapp.cn/profile/large/2016/05/30/574bb6d60037d_mini.jpg","http://dimg.spriteapp.cn/profile/large/2016/05/30/574bb6d60037d_mini.jpg"]
         * is_v : true
         * uid : 5083551
         * is_vip : false
         * name : 百思姐夫V武大狼
         */

        private UBean u;
        private String passtime;
        /**
         * playfcount : 220
         * height : 360
         * width : 640
         * video : ["http://bvideo.spriteapp.cn/video/2016/0623/576bf47b513ed_wpd.mp4","http://wvideo.spriteapp.cn/video/2016/0623/576bf47b513ed_wpd.mp4"]
         * duration : 358
         * playcount : 5559
         * thumbnail : ["http://wimg.spriteapp.cn/picture/2016/0623/576bf47b17705__b.jpg","http://dimg.spriteapp.cn/picture/2016/0623/576bf47b17705__b.jpg"]
         * download : ["http://bvideo.spriteapp.cn/video/2016/0623/576bf47b513ed_wpc.mp4","http://wvideo.spriteapp.cn/video/2016/0623/576bf47b513ed_wpc.mp4"]
         */

        private VideoBean video;
        private String type;
        private String id;
        /**
         * id : 18910
         * name : hx
         */

        private List<TagsBean> tags;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getBookmark() {
            return bookmark;
        }

        public void setBookmark(String bookmark) {
            this.bookmark = bookmark;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUp() {
            return up;
        }

        public void setUp(String up) {
            this.up = up;
        }

        public String getShare_url() {
            return share_url;
        }

        public void setShare_url(String share_url) {
            this.share_url = share_url;
        }

        public int getDown() {
            return down;
        }

        public void setDown(int down) {
            this.down = down;
        }

        public int getForward() {
            return forward;
        }

        public void setForward(int forward) {
            this.forward = forward;
        }

        public UBean getU() {
            return u;
        }

        public void setU(UBean u) {
            this.u = u;
        }

        public String getPasstime() {
            return passtime;
        }

        public void setPasstime(String passtime) {
            this.passtime = passtime;
        }

        public VideoBean getVideo() {
            return video;
        }

        public void setVideo(VideoBean video) {
            this.video = video;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<TagsBean> getTags() {
            return tags;
        }

        public void setTags(List<TagsBean> tags) {
            this.tags = tags;
        }

        public static class UBean {
            private boolean is_v;
            private String uid;
            private boolean is_vip;
            private String name;
            private List<String> header;

            public boolean isIs_v() {
                return is_v;
            }

            public void setIs_v(boolean is_v) {
                this.is_v = is_v;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public boolean isIs_vip() {
                return is_vip;
            }

            public void setIs_vip(boolean is_vip) {
                this.is_vip = is_vip;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<String> getHeader() {
                return header;
            }

            public void setHeader(List<String> header) {
                this.header = header;
            }
        }

        public static class VideoBean {
            private int playfcount;
            private int height;
            private int width;
            private int duration;
            private int playcount;
            private List<String> video;
            private List<String> thumbnail;
            private List<String> download;

            public int getPlayfcount() {
                return playfcount;
            }

            public void setPlayfcount(int playfcount) {
                this.playfcount = playfcount;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getDuration() {
                return duration;
            }

            public void setDuration(int duration) {
                this.duration = duration;
            }

            public int getPlaycount() {
                return playcount;
            }

            public void setPlaycount(int playcount) {
                this.playcount = playcount;
            }

            public List<String> getVideo() {
                return video;
            }

            public void setVideo(List<String> video) {
                this.video = video;
            }

            public List<String> getThumbnail() {
                return thumbnail;
            }

            public void setThumbnail(List<String> thumbnail) {
                this.thumbnail = thumbnail;
            }

            public List<String> getDownload() {
                return download;
            }

            public void setDownload(List<String> download) {
                this.download = download;
            }
        }

        public static class TagsBean {
            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
        private GifEntity gif;
        public static class GifEntity{
            private List<String> download_url;
            private List<String> gif_thumbnail;
            private int height;
            private int width;
            private List<String> images;

            public List<String> getDownload_url() {
                return download_url;
            }

            public void setDownload_url(List<String> download_url) {
                this.download_url = download_url;
            }

            public List<String> getGif_thumbnail() {
                return gif_thumbnail;
            }

            public void setGif_thumbnail(List<String> gif_thumbnail) {
                this.gif_thumbnail = gif_thumbnail;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public List<String> getImages() {
                return images;
            }

            public void setImages(List<String> images) {
                this.images = images;
            }
        }

        public GifEntity getGif() {
            return gif;
        }
        public static class ImageEntity{
            private List<String> big;
            private List<String> download_url;
            private List<String> medium;
            private List<String> small;
            private int width;
            private int height;

            public List<String> getBig() {
                return big;
            }

            public void setBig(List<String> big) {
                this.big = big;
            }

            public List<String> getDownload_url() {
                return download_url;
            }

            public void setDownload_url(List<String> download_url) {
                this.download_url = download_url;
            }

            public List<String> getMedium() {
                return medium;
            }

            public void setMedium(List<String> medium) {
                this.medium = medium;
            }

            public List<String> getSmall() {
                return small;
            }

            public void setSmall(List<String> small) {
                this.small = small;
            }

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }
        private ImageEntity image;
        public ImageEntity getImage(){
            return image;
        }
    }
}
