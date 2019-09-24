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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("gifflen");
    }

    private Context mContext;
    private boolean mHasPermission;

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

        findViewById(R.id.go).setOnClickListener(v -> {

            if (mHasPermission) {
                String path = Environment.getExternalStorageDirectory() + "/gif/";
                final List<File> lists = new ArrayList<>();
                File file = new File(path);
                if (file.exists() && file.isDirectory()) {
                    File[] files = file.listFiles();
                    if (files != null) {
                        Collections.addAll(lists, files);
                        Collections.sort(lists, (o1, o2) -> o1.getName().compareTo(o2.getName()));
                    }else {
                        return;
                    }
                } else {
                    return;
                }

                for (File list : lists) {
                    System.out.println(list.getName());
                }

                String dest = Environment.getExternalStorageDirectory() + File.separator + "test.gif";

                Gifflen gifflen = new Gifflen.Builder()
                        .listener(p ->
                                Glide.with(mContext)
                                        .load(p)
                                        .into(imageView))
                        .build();
                gifflen.encode(dest, lists);
            }else {
                requestPermission();
            }


        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
    }


    @SuppressLint("CheckResult")
    private void requestPermission() {
        RealRxPermission.getInstance(mContext).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission ->
                        mHasPermission = permission.state() == Permission.State.GRANTED);
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
