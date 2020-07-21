package com.engineer.android.cpp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_example.*

class ExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_example)
        val result = stringFromJNI()
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        simple_map.text = mapStringWithJNI("Android")
    }

    private external fun stringFromJNI(): String
    private external fun mapStringWithJNI(input: String): String

    companion object {
        init {
            System.loadLibrary("fly")
        }
    }
}