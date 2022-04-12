package com.feadca.protectora.ui

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.feadca.protectora.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Acciones ejecutadas al pulsar el texto de login
        binding.tvLogin.setOnClickListener {
            hideKeyboard() // Ocultamos el teclado

            if( binding.etEmail.text.toString() != "" && binding.etPassword.text.toString() != "")
            {
                // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
                binding.layout.alpha = 0.5f

                // Mostramos la progress bar
                binding.progressBar.visibility = View.VISIBLE;

                // TODO: Llamada a la API
            }else{
                Snackbar.make(binding.layout, "Introduzca su email y contraseña", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}