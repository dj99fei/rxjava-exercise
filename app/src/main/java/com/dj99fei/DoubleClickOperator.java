package com.dj99fei;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by chengfei on 2018/1/31.
 */
class DoubleClickOperator implements Observable.Operator<Void, Void> {

    private long last = -1;

    @Override
    public Subscriber<? super Void> call(Subscriber<? super Void> child) {

        Subscriber<? super Void> s = new Subscriber<Void>() {
            @Override
            public void onCompleted() {
                child.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                child.onError(e);

            }

            @Override
            public void onNext(Void a) {
                if (last == -1) {
                    last = System.currentTimeMillis();
                } else if (System.currentTimeMillis() - last < 1000) {
                    child.onNext(a);
                    last = -1;
                } else {
                    last = -1;
                }
            }
        };
        child.add(s);
        return s;
    }
}
