package com.engineer.cpluspluslite;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vanniktech.rxpermission.Permission;
import com.vanniktech.rxpermission.RealRxPermission;
import com.vanniktech.rxpermission.RxPermission;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("gifflen");
    }

    private Context mContext;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        ImageView imageView = findViewById(R.id.image);

        findViewById(R.id.go).setOnClickListener(v ->
                RealRxPermission.getInstance(mContext).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribeOn(Schedulers.io())
                        .subscribe(permission -> {
                            if (permission.state() == Permission.State.GRANTED) {
                                String path = Environment.getExternalStorageDirectory() + "/gif/";
                                final ArrayList<File> lists = new ArrayList<>();
                                File file = new File(path);
                                if (file.exists() && file.isDirectory()) {
                                    File[] files = file.listFiles();
                                    for (File file1 : files) {
                                        lists.add(file1);
                                    }
                                    lists.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
                                }

                                String path1 = Environment.getExternalStorageDirectory() + File.separator + "test.gif";

                                Gifflen gifflen = new Gifflen.Builder()
                                        .listener(path11 -> {
                                            Glide.with(mContext).load(path11).into(imageView);
                                        }).build();
                                gifflen.encode(path1, lists);
                            }
                        }, throwable -> throwable.printStackTrace()));
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
