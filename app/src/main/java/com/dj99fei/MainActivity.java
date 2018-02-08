package com.dj99fei;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.internal.operators.OnSubscribeDoOnEach;
import rx.internal.util.ActionObserver;
import rx.schedulers.Schedulers;

import static rx.Observable.create;

public class MainActivity extends BaseActivity {

    private Observable<Integer> natureNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addSubscription(
                fromView(R.id.double_click_1)
                        .lift(new DoubleClickOperator())
                        .doOnNext(avoid -> {
                            Toast.makeText(this, "double clicked", Toast.LENGTH_SHORT).show();
                        })
                        .subscribe()
        );

        addSubscription(
                create(new OnSubscribeDoubleClick(fromView(R.id.double_click_2)))
                        .doOnNext(avoid -> {
                            Toast.makeText(this, "double clicked", Toast.LENGTH_SHORT).show();
                        })
                        .subscribe()
        );

        Observer<Void> observer = new ActionObserver<>(aVoid -> Toast.makeText(this, "double clicked", Toast.LENGTH_SHORT).show(), Actions.empty(), Actions.empty());

        addSubscription(
                create(new OnSubscribeDoOnEach<>(
                        create(new OnSubscribeDoubleClick(
                                create(new ViewClickOnSubscribe(findViewById(R.id.double_click_3))))), observer)).subscribe()
        );


        natureNum = generate(Observable.empty(), 1);

        addSubscription(
                fromView(R.id.nature_number)
                        .map(avoid -> 1)
                        .flatMap(o -> natureNum.doOnNext(i -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(i))))
                        .subscribe(Actions.empty(), throwable -> throwable.printStackTrace())
        );

        addSubscription(
                fromView(R.id.multi_subscribe_on)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .flatMap(o -> natureNum.take(10))
                        .map(avoid -> 1)
                        .subscribeOn(Schedulers.io())
                        .doOnNext(i -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(i) + "thread: " + Thread.currentThread().getName()))
                        .subscribe()
        );

        addSubscription(
                fromView(R.id.retry_finite)
                        .subscribe(avoid -> Observable.just(1)
                                .doOnNext(i -> {
                                    if (i == 1) {
                                        throw new RuntimeException();
                                    }
                                }).retry(3)
                                .subscribe(Actions.empty(), throwable -> Log.e(MainActivity.class.getSimpleName(), "error"))));


        addSubscription(
                fromView(R.id.sqrt)
                        .observeOn(Schedulers.computation())
                        .flatMap(aVoid -> sqrt(Observable.just(1.0f), 2).take(10))
                        .doOnNext(f -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(f)))
                        .subscribe()

        );

    }

    Observable<Integer> generate(Observable<Integer> source, int initValue) {
        return source.mergeWith(Observable.defer(() -> generate(Observable.just(initValue), initValue + 1)))
                .subscribeOn(Schedulers.computation());
    }


    float sqrtImprove(float guess, int x) {
        return ((x / guess) + guess) / 2;
    }


    Observable<Float> sqrt(Observable<Float> source, int x) {
        return source.mergeWith(Observable.defer(() -> sqrt(source, x).map(guess -> sqrtImprove(guess, x))));

    }
}
