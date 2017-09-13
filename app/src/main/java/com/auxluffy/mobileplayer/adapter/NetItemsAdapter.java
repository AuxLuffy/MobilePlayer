package com.auxluffy.mobileplayer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auxluffy.mobileplayer.R;
import com.auxluffy.mobileplayer.domain.JsonItem;
import com.auxluffy.mobileplayer.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import pl.droidsonroids.gif.GifImageView;

/**
 * Created by Administrator on 2016/6/25.
 */
public class NetItemsAdapter extends BaseAdapter {
    Utils utils = new Utils();
    ArrayList<JsonItem.ListBean> ls;
    Context mContext;
    LayoutInflater inflater;
    final int VIEW_TYPE = 4;
    final int TYPE_1 = 0;
    final int TYPE_2 = 1;
    final int TYPE_3 = 2;
    final int TYPE_4 = 3;

    public NetItemsAdapter(Context context, ArrayList<JsonItem.ListBean> list) {
        ls = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return ls.size();
    }

    @Override
    public Object getItem(int position) {
        return ls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 每个convert view都会调用此方法，获得当前所需要的view样式
    @Override
    public int getItemViewType(int position) {

        int p = -1;
        JsonItem.ListBean bean = ls.get(position);
        String type = bean.getType();
        Log.e("TAG", "type===="+type);
        if ("video".equals(type))
            p = TYPE_1;
        else if ("gif".equals(type)) {
            p = TYPE_2;
        } else if ("image".equals(type)) {
            p = TYPE_3;
        } else {
            p = TYPE_4;
        }
        return p;

    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            holder = new ViewHolder();
            inflater = LayoutInflater.from(mContext);
            // 按当前所需的样式，确定new的布局
            switch (type) {
                case TYPE_1:

                    convertView = inflater.inflate(R.layout.all_video_item, parent, false);


                    initCommomView(convertView, holder);

                    holder.jcv_videoplayer = (JCVideoPlayer) convertView.findViewById(R.id.jcv_videoplayer);
                    holder.tv_video_duration = (TextView) convertView.findViewById(R.id.tv_video_duration);
                    holder.tv_play_nums = (TextView) convertView.findViewById(R.id.tv_play_nums);
                    holder.iv_commant = (ImageView) convertView.findViewById(R.id.iv_commant);
                    holder.tv_commant_context = (TextView) convertView.findViewById(R.id.tv_commant_context);
                    convertView.setTag(holder);
                    break;

                case TYPE_2:
                    convertView = inflater.inflate(R.layout.all_gif_item, parent, false);
                    initCommomView(convertView, holder);
                    holder.iv_image_gif = (GifImageView) convertView.findViewById(R.id.iv_image_gif);
                    break;
                case TYPE_3:
                    convertView = inflater.inflate(R.layout.all_image_item, parent, false);
                    holder.iv_image_icon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
                    initCommomView(convertView, holder);
                    break;
                case TYPE_4:
                    convertView = inflater.inflate(R.layout.all_text_item, parent, false);
                    initCommomView(convertView, holder);
                    break;
                default:
                    break;
            }
            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }
        JsonItem.ListBean item = ls.get(position);
        // 设置资源
        switch (type) {
            case TYPE_1:
                bindData(holder, item);
                holder.jcv_videoplayer.setUp(item.getVideo().getVideo().get(0), item.getVideo().getThumbnail().get(0), null);
                holder.tv_play_nums.setText(item.getVideo().getPlaycount() + "次播放");
                holder.tv_video_duration.setText(utils.stringForTime(item.getVideo().getDuration() * 1000) + "");
                break;


            case TYPE_2:
                bindData(holder, item);
               /* Glide.with(mContext)
                        .load(item.getGif().getDownload_url().get(0))
                        .placeholder(R.drawable.video_default_icon)
                        .error(R.drawable.video_default_icon)
                        .crossFade()
                        .into(holder.iv_image_gif);*/
                // x.image().bind(holder.iv_image_gif,item.getGif().getDownload_url().get(1));
                try {
                    Glide.with(mContext).load(item.getGif().getImages().get(0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(holder.iv_image_gif);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case TYPE_3:
                bindData(holder, item);
                holder.iv_image_icon.setImageResource(R.drawable.bg_item);
                if (item.getImage() != null && item.getImage().getSmall() != null && item.getImage().getSmall().size() > 0) {
                    x.image().bind(holder.iv_image_icon, item.getImage().getSmall().get(0));
                   /* Glide.with(mContext)
                            .load(item.getImage().getBig().get(0))*/
                } else if (item.getImage() != null && item.getImage().getBig() != null && item.getImage().getBig().size() > 0) {
                    x.image().bind(holder.iv_image_icon, item.getImage().getBig().get(0));
                }
                break;
            case TYPE_4:
                bindData(holder, item);
                break;
        }

        return convertView;
    }

    private void bindData(ViewHolder viewHolder, JsonItem.ListBean mediaItem) {
        if (mediaItem.getU() != null && mediaItem.getU().getHeader() != null && mediaItem.getU().getHeader().get(0) != null) {
            x.image().bind(viewHolder.iv_headpic, mediaItem.getU().getHeader().get(0));
        }
        if (mediaItem.getU() != null && mediaItem.getU().getName() != null) {
            viewHolder.tv_name.setText(mediaItem.getU().getName() + "");
        }
        viewHolder.tv_context.setText(mediaItem.getText());
        viewHolder.tv_time_refresh.setText(mediaItem.getPasstime());


        List<JsonItem.ListBean.TagsBean> tagsEntities = mediaItem.getTags();
        if (tagsEntities != null && tagsEntities.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tagsEntities.size(); i++) {
                buffer.append(tagsEntities.get(i).getName() + " ");
            }
            viewHolder.tv_video_kind_text.setText(buffer.toString());
        }


        viewHolder.tv_shenhe_ding_number.setText(mediaItem.getUp());
        viewHolder.tv_shenhe_cai_number.setText(mediaItem.getDown() + "");
        //viewHolder.tv_posts_number.setText(mediaItem.getForward());

    }

    private void initCommomView(View convertView, ViewHolder holder) {
        holder.iv_headpic = (ImageView) convertView.findViewById(R.id.iv_headpic);
        holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        holder.tv_time_refresh = (TextView) convertView.findViewById(R.id.tv_time_refresh);

        holder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);

        holder.tv_video_kind_text = (TextView) convertView.findViewById(R.id.tv_video_kind_text);
        holder.tv_shenhe_cai_number = (TextView) convertView.findViewById(R.id.tv_shenhe_cai_number);
        holder.tv_shenhe_ding_number = (TextView) convertView.findViewById(R.id.tv_shenhe_ding_number);
        holder.tv_posts_number = (TextView) convertView.findViewById(R.id.tv_posts_number);
    }

    public class ViewHolder {
        //公共头
        ImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        ImageView iv_right_more;

        //中间部分
        TextView tv_context;
        JCVideoPlayer jcv_videoplayer;
        TextView tv_video_duration;
        TextView tv_play_nums;
        ImageView iv_commant;
        TextView tv_commant_context;

        ImageView iv_image_icon;

        GifImageView iv_image_gif;


        //底部
        TextView tv_video_kind_text;
        TextView tv_shenhe_ding_number;
        TextView tv_shenhe_cai_number;
        TextView tv_posts_number;
        LinearLayout ll_download;
    }
  


}
