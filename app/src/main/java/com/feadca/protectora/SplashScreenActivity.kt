package com.feadca.protectora

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class SplashScreenActivity : AppCompatActivity() {

    // Variable usada para indicar el tiempo que nos mantendremos en la splash screen
    val SPLASH_SCREEN_TIME = 5000

    // Variables que contendrán las animaciones que usaremos en la splash screen
    private lateinit var topAnimation: Animation
    private lateinit var bottomAnimation: Animation

    // Variables que contienen la referencia a los elementos del layout
    private lateinit var logo: ImageView
    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ocultamos la barra de estado
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val actionBar = supportActionBar
        actionBar!!.hide()

        setContentView(R.layout.activity_splash_screen)

        // Actualizamos las variables con las animaciones a realizar
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation)
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation)

        // Actualizamos las referencias a los elementos del layout
        logo = findViewById(R.id.ivLogo)
        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)

        // Realizamos la animación pertinente en cada elemento
        logo.animation = topAnimation
        tvTitle.animation = bottomAnimation
        tvDescription.animation = bottomAnimation

        // Pasado el tiempo del SPLASH_SCREEN_TIME realizaremos un Intent al MainActivity
        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_SCREEN_TIME.toLong())
    }
}