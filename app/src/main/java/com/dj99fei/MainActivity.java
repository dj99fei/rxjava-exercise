package com.dj99fei;

import android.os.Bundle;
import android.widget.Toast;

import rx.Observable;
import rx.Subscriber;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fromView(R.id.double_click)
                .lift(new DoubleClickOperator())
                .map(a -> 1)
                .doOnNext(avoid -> {
                    Toast.makeText(this, "double clicked", Toast.LENGTH_SHORT).show();
                })
                .subscribe();

    }

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

}
