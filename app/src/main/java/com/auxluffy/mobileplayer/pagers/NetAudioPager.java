package com.auxluffy.mobileplayer.pagers;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auxluffy.mobileplayer.R;
import com.auxluffy.mobileplayer.adapter.NetItemsAdapter;
import com.auxluffy.mobileplayer.base.BasePager;
import com.auxluffy.mobileplayer.domain.JsonItem;
import com.auxluffy.mobileplayer.utils.Constants;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络音乐
 * Created by Administrator on 2016/6/16.
 */
public class NetAudioPager extends BasePager {
    private ListView lv_netvideo;
    private TextView tv_nonet;
    private ProgressBar progressbar;
    //private
    private List<JsonItem.ListBean> list;
    private NetItemsAdapter adapter;

    public NetAudioPager(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.netaudio_pager, null);
        lv_netvideo = (ListView) view.findViewById(R.id.lv_netvideo);
        //设置支持加载更多
        //lv_netvideo.setPullLoadEnable(true);

        tv_nonet = (TextView) view.findViewById(R.id.tv_nonet);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        getDataFromNet();

    }

    /**
     * 解析json数据
     *
     * @param json
     */
    private void processData(String json) {


        Gson gson = new Gson();
        JsonItem jsons = gson.fromJson(json, JsonItem.class);
        list = jsons.getList();
        adapter = new NetItemsAdapter(context, (ArrayList<JsonItem.ListBean>) list);
        lv_netvideo.setAdapter(adapter);

    }


    /**
     * 联网请求
     */
    private void getDataFromNet() {

        RequestParams params = new RequestParams(Constants.ALL_RES_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TAG", "联网成功。。。result = " + result);
                processData(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "联网失败。。。");
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.e("TAG", "联网中断。。。");
            }

            @Override
            public void onFinished() {
                Log.e("TAG", "联网成功。。。");
            }
        });
    }
}
