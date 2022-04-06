package com.feadca.protectora

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}