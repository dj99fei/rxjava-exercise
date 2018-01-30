package com.dj99fei;

import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chengfei on 2018/1/31.
 */

public class BaseActivity extends AppCompatActivity {

    protected Observable<Void> fromView(int id) {
        return RxView.clicks(findViewById(id)).subscribeOn(AndroidSchedulers.mainThread());
    }
}
