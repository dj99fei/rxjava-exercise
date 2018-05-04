package com.dj99fei;


import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by chengfei on 2018/2/3.
 */

public class Balance {

    public static Observable<Float> balance(Observable<Float> withdraw, float balance) {
        return Observable.just(balance)
                .mergeWith(Observable.defer(() -> balance(withdraw.skip(1), (balance + withdraw.take(1).toBlocking().first())
                ))).subscribeOn(Schedulers.computation());
    }
}
