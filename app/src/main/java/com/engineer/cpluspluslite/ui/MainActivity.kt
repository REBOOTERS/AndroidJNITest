package com.engineer.cpluspluslite.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.cpp.ExampleActivity
import com.engineer.cpluspluslite.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var mContext: AppCompatActivity


    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContext = this
        setContentView(R.layout.activity_main)
        sample_text.setOnClickListener { startActivity(Intent(mContext, ExampleActivity::class.java)) }
        gen.setOnClickListener { startActivity(Intent(mContext, GenGifActivity::class.java)) }
        reverse.setOnClickListener { startActivity(Intent(mContext, ReverseGifActivity::class.java)) }
    }
}
