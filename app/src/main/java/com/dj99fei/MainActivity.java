package com.dj99fei;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.Random;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Actions;
import rx.internal.operators.OnSubscribeDoOnEach;
import rx.internal.util.ActionObserver;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

import static com.dj99fei.Balance.balance;
import static rx.Observable.create;

public class MainActivity extends BaseActivity {

    private Observable<Integer> natureNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        natureNum = generate(Observable.empty(), 1);
        fromView(R.id.nature_number)
                .flatMap(o -> natureNum)
                .take(10)
                .doOnNext(i -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(i)))
                .subscribe(Actions.empty(), throwable -> {
                    throwable.printStackTrace();
                });

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

        addSubscription(
                fromView(R.id.retry)
                        .subscribe(
                                a -> Observable.just(1)
                                        .observeOn(Schedulers.computation())
                                        .doOnNext(i -> {
                                            Log.e(MainActivity.class.getSimpleName(), String.valueOf(i));
                                            if (true) {
                                                throw new RuntimeException();
                                            }
                                        }).retry(3)
                                        .subscribe(Actions.empty(), throwable -> {
                                            throwable.printStackTrace();
                                        })
                        ));


        // retry防事件流中断
        fromView(R.id.retry_continually)
                .flatMap(a -> Observable.just(new Random().nextInt() % 2 == 0)
                        .doOnNext(b -> {
                            if (b) {
                                throw new RuntimeException();
                            }
                        }))
                .doOnError(throwable -> Toast.makeText(this, "error", Toast.LENGTH_SHORT).show())
                .retry()
                .subscribe();

        addSubscription(
                fromView(R.id.sqrt)
                        .flatMap(a -> sqrt(Observable.just(1.0f), 3))
                        .take(5)
                        .subscribe(f -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(f)), throwable -> throwable.printStackTrace())
        );

        BehaviorSubject<Float> withdraw = BehaviorSubject.create();
        withdraw.startWith(0f);

        EditText withdrawEdit = findViewById(R.id.withdraw);


        fromView(R.id.balance)
                .doOnNext(aVoid -> withdraw.onNext(Float.parseFloat(withdrawEdit.getText().toString())))
                .subscribe();

//        balance(Observable.from(new Float[]{10f, 20f, 30f, 100f}), 100f)
//                .doOnNext(f -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(f)))
//                .subscribe();


        balance(withdraw, 100f)
                .doOnNext(f -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(f)))
                .subscribe();

//        balance(fromView(R.id.balance)
//                .doOnNext(aVoid -> withdraw.onNext(Float.parseFloat(withdrawEdit.getText().toString())))
//                .flatMap(o -> withdraw), 100f)
//
//                .doOnNext(f -> Log.e(MainActivity.class.getSimpleName(), String.valueOf(f)))
//                .subscribe();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},1);
        }
    }

    float sqrtImprove(float guess, int x) {
        return ((x / guess) + guess) / 2;
    }

    Observable<Float> sqrt(Observable<Float> init, int x) {
        return init.mergeWith(Observable.defer(() -> sqrt(init, x).map(guess -> sqrtImprove(guess, x))));
    }

    Observable<Integer> generate(Observable<Integer> source, int initValue) {
        return source.mergeWith(Observable.defer(() -> generate(Observable.just(initValue), initValue + 1)))
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getFromCamera();
                } else {
                    Toast.makeText(this, "没有相机的权限，请前往权限设置", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private void getFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File out = new File(Environment.getExternalStorageDirectory()
                    .toString());
            Uri uri = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(this, "com.gewara.provider", out);
            } else {
                uri = Uri.fromFile(out);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
