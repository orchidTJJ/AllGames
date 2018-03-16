package com.orchid.counter.allgames;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by orchid on 2018/3/15.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
