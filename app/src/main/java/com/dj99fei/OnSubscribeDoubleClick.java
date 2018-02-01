package com.dj99fei;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by chengfei on 2018/2/1.
 */

public class OnSubscribeDoubleClick implements Observable.OnSubscribe<Void> {

    private Observable<Void> source;

    public OnSubscribeDoubleClick(Observable<Void> source) {
        this.source = source;
    }

    @Override
    public void call(Subscriber<? super Void> actual) {
        DoubleSubscriber subscriber = new DoubleSubscriber(actual);
        actual.add(subscriber);
        source.unsafeSubscribe(subscriber);
    }

    class DoubleSubscriber extends Subscriber<Void> {

        Subscriber actual;

        public DoubleSubscriber(Subscriber<?> actual) {
            this.actual = actual;
        }

        private long last = -1;
        @Override
        public void onCompleted() {
            actual.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            actual.onError(e);

        }

        @Override
        public void onNext(Void a) {
            if (last == -1) {
                last = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - last < 1000) {
                actual.onNext(a);
                last = -1;
            } else {
                last = -1;
            }
        }
    }
}
