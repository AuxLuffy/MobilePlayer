package com.auxluffy.mobileplayer.base;

import android.content.Context;
import android.view.View;

/**
 * 为了方便管理，提高代码复用率，并且简洁化主Activity的代码量，方便维护
 * 如果不做的话，每个Fragment要加载的主界面都要写在MainActivity中会导致其特别大，不方便维护，查错
 * 可以将界面中的布局页面对象化，并且使四个页面继承此基类，同时也可以利用多态实现很多方面的便捷
 * 作用：本地视频，本地音乐，网络视频，网络音乐的基类/父类/公共类
 *
 * Created by Administrator on 2016/6/16.
 */

public abstract class BasePager {
    /*
    上下文
     */
    public final Context context;
    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    public abstract View initView();//每一个界面实现界面的方法不同，所以将此方法定义为一个抽象方法

    /**
     * 要想由此类可以获取一个界面，所以此方法应包括一个View
     * 因为其不继承View，所以可以有一个View的属性
     *  用来接收各个界面的实例
     */

    public boolean isInitData;
    public View rootView;

    /**
     * 当子页面要初始化数据时，就要调用一个方法初始化数据，不强制，所以不写成抽象的
     *
     */
    public void initData(){

    };
}
