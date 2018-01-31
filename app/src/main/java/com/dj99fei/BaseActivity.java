package com.dj99fei;

import android.support.v7.app.AppCompatActivity;

import com.jakewharton.rxbinding.view.RxView;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.util.SubscriptionList;

/**
 * Created by chengfei on 2018/1/31.
 */

public class BaseActivity extends AppCompatActivity {

    SubscriptionList mSubscriptionList = new SubscriptionList();

    protected Observable<Void> fromView(int id) {
        return RxView.clicks(findViewById(id)).subscribeOn(AndroidSchedulers.mainThread());
    }


    protected void addSubscription(Subscription subscription) {
        mSubscriptionList.add(subscription);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSubscriptionList.unsubscribe();
    }
}
