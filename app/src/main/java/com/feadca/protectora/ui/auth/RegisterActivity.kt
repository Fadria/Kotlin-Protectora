package com.feadca.protectora.ui.auth

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.feadca.protectora.databinding.ActivityLoginBinding
import com.feadca.protectora.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}