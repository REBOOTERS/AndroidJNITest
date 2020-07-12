package com.engineer.android.cpp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)
    }

    external fun mapStringInJNI():String
}