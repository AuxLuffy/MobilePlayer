package com.auxluffy.mobileplayer.pagers;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.auxluffy.mobileplayer.R;
import com.auxluffy.mobileplayer.activity.VideoPlayerActivity;
import com.auxluffy.mobileplayer.base.BasePager;
import com.auxluffy.mobileplayer.domain.VideoItem;
import com.auxluffy.mobileplayer.utils.CacheUtils;
import com.auxluffy.mobileplayer.utils.Constants;
import com.auxluffy.mobileplayer.utils.LogUtil;
import com.auxluffy.mobileplayer.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 网络视频
 * Created by Administrator on 2016/6/16.
 */
public class NetVideoPager extends BasePager {
    private XListView lv_netvideo;
    private TextView tv_nonet;
    private ProgressBar progressbar;
    private ArrayList<VideoItem> list;
    private NetVideoPagerAdapter adapter;

    public NetVideoPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netvideo_pager, null);
        lv_netvideo = (XListView) view.findViewById(R.id.lv_netvideo);
        //设置支持加载更多
        lv_netvideo.setPullLoadEnable(true);

        tv_nonet = (TextView) view.findViewById(R.id.tv_nonet);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        return view;
    }

    /**
     * 初始化数据
     */
    @Override
    public void initData() {
        super.initData();
        String saveJson = CacheUtils.getString(context,Constants.NET_VIDEO_URL);
        if(!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
        getDataFromNet();
        //设置点击某一条的监听
        lv_netvideo.setXListViewListener(new MyXlistViewListener());
        lv_netvideo.setOnItemClickListener(new MyNetVideoOnItemClickListener());
    }

    /**
     * 联网请求
     */
    public void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);
                Log.e("Tag", result);
                CacheUtils.putString(context, Constants.NET_VIDEO_URL, result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex.getMessage());
                Toast.makeText(context, "请求数据失败!", Toast.LENGTH_SHORT).show();
                lv_netvideo.stopRefresh();
                showData();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("请求数据完成");
            }
        });

    }
    /**
     * 解析json数据和显示数据
     *
     * @param json
     */
    private void processData(String json) {
        //准备好数据
        if(!isLoadMore) {

            list = parsedJson(json);
            showData();
            onLoad();
        }else {
            isLoadMore = false;
            list.addAll(parsedJson(json));
            adapter.notifyDataSetChanged();//这个千万别写为lv_netvideo.setAdapter(adapter);否刚加载更多的时候会重新定位到listview的最首部
            onLoad();
        }



    }

    /**
     * 设置xlistview数据、联网失败字符串显示与否
     */
    private void showData() {

        if(list!=null && list.size()>0) {
            //设置适配器
            adapter = new NetVideoPagerAdapter();
            lv_netvideo.setAdapter(adapter);

            tv_nonet.setVisibility(View.GONE);
        }else {
            //没有数据
            tv_nonet.setVisibility(View.VISIBLE);
        }
        //隐藏加载效果
        progressbar.setVisibility(View.GONE);
    }

    class NetVideoPagerAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView==null) {
                convertView = View.inflate(context,R.layout.item_netvideo_pager,null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                holder.tv_desc = (TextView) convertView.findViewById(R.id.tv_desc);
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder) convertView.getTag();
            }
            VideoItem item = list.get(position);
            Log.e("tig", "1"+holder.iv_icon.getResources()+"");
            x.image().bind(holder.iv_icon, item.getImageUrl());//xutils3联网请求图片
            Log.e("tig", "22222" + holder.iv_icon.getResources()+"");

            holder.tv_desc.setText(item.getDesc());
            holder.tv_name.setText(item.getName());
            return convertView;
        }
    }
    class ViewHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_desc;
    }


    /**
     * 解析json：手动解（用Android自身带的接口解，和第三方解析框架（Gson，fastjson）
     * 在这里我们使用自带的
     *
     * @param json
     * @return
     */
    private ArrayList<VideoItem> parsedJson(String json) {
        ArrayList<VideoItem> list = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray trailers = jsonObject.optJSONArray("trailers");
            if (trailers != null && trailers.length() > 0) {
                for (int i = 0; i < trailers.length(); i++) {
                    JSONObject jsonObject1 = trailers.getJSONObject(i);
                    String name = jsonObject1.optString("movieName");
                    String imageUrl = jsonObject1.optString("coverImg");
                    String desc = jsonObject1.optString("videoTitle");
                    String url = jsonObject1.optString("url");
                    VideoItem item = new VideoItem(name, desc, imageUrl, url);
                    list.add(item);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    private class MyNetVideoOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, VideoPlayerActivity.class);
            intent.putExtra("list",(ArrayList<VideoItem>)list);

            intent.putExtra("position", position-1);
            context.startActivity(intent);



        }
    }

    private class MyXlistViewListener implements XListView.IXListViewListener {
        /**
         * 下拉刷新
         */

        @Override
        public void onRefresh() {
            getDataFromNet();

        }

        /**
         * 加载更多
         */
        @Override
        public void onLoadMore() {
            getMoreDateFromNet();

        }
    }

    private void onLoad(){
        lv_netvideo.stopRefresh();
        lv_netvideo.stopLoadMore();
        lv_netvideo.setRefreshTime(getSystemTime());
    }
    private String getSystemTime(){
        SimpleDateFormat format = new SimpleDateFormat();
        return format.format(new Date());
    }
    private boolean isLoadMore = false;
    /**
     * 加载更多
     */
    private void getMoreDateFromNet() {
        RequestParams params = new RequestParams(Constants.NET_VIDEO_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("请求数据成功==" + result);
                isLoadMore = true;
                //缓存当前要加载的网页地址（json数据）
                CacheUtils.putString(context, Constants.NET_VIDEO_URL, result);
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("请求数据失败==" + ex.getMessage());
                Toast.makeText(context, "请求数据失败!", Toast.LENGTH_SHORT).show();
                isLoadMore = false;
                lv_netvideo.stopLoadMore();
                showData();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.e("请求数据完成");
                isLoadMore = false;
            }
        });
    }
}
