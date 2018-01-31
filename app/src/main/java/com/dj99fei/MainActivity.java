package com.dj99fei;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import rx.Observable;
import rx.functions.Actions;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    private Observable<Integer> natureNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addSubscription(
                fromView(R.id.double_click)
                        .lift(new DoubleClickOperator())
                        .map(a -> 1)
                        .doOnNext(avoid -> {
                            Toast.makeText(this, "double clicked", Toast.LENGTH_SHORT).show();
                        })
                        .subscribe()
        );

        natureNum = generate(Observable.empty(), 1);

        fromView(R.id.nature_number)
                .map(avoid -> 1)
                .flatMap(
                        o ->
                        natureNum
//                            .take(10)
                                .doOnNext(i -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(i)))

                ).subscribe(Actions.empty(), throwable -> {
            throwable.printStackTrace();
        });
    }

    Observable<Integer> generate(Observable<Integer> source, int initValue) {
        return source.mergeWith(Observable.defer(() -> generate(Observable.just(initValue), initValue + 1)))
                .subscribeOn(Schedulers.computation());
    }
}
