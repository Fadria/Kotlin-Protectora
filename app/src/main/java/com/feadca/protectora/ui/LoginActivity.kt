package com.feadca.protectora.ui

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.databinding.ActivityLoginBinding
import com.feadca.protectora.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    // Datos usados en el Login
    lateinit var email:String
    lateinit var pass:String

    // Variable que contiene la referencia al ViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Indicamos el fichero que contiene el ViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Acciones ejecutadas al detectar una actualización en el LiveData userDataLD
        authViewModel.userDataLD.observe(this){
            // TODO: Guardamos los datos del usuario en un Shared Preferences
            showSnackbar("Login completado")
        }

        // Acciones ejecutadas al detectar una actualización en el LiveData errorLD
        authViewModel.errorLD.observe(this){
            showSnackbar(it!!)

            // Cambiamos la opacidad de la pantalla para que el usuario sepa que puede continuar usando la app
            binding.layout.alpha = 1.0f

            // Ocultamos la progress bar
            binding.progressBar.visibility = View.INVISIBLE;

            // Permitimos que se puedan usar los inputs de nuevo
            binding.etEmail.isEnabled = true;
            binding.etPassword.isEnabled = true;
        }

        // Acciones ejecutadas al pulsar el texto de login
        binding.btnLogin.setOnClickListener {
            // Actualizamos los valores de las variables para usarlos posteriormente
            email = binding.etEmail.text.toString()
            pass = binding.etPassword.text.toString()

            if( email != "" && pass != "" )
            {
                // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
                binding.layout.alpha = 0.5f

                // Mostramos la progress bar
                binding.progressBar.visibility = View.VISIBLE;

                // Impedimos que se puedan usar los inputs mientras realizamos la llamada a la API
                binding.etEmail.isEnabled = false;
                binding.etPassword.isEnabled = false;

                login( email, pass ) // Llamamos a la función que contactará con el ViewModel

                hideKeyboard() // Ocultamos el teclado
                clearFocus()
            }else{
                showSnackbar("Introduzca su email y contraseña")
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    // Función encargada de llamar al Login del ViewModel
    private fun login(email: String, pass: String) {
        val email = binding.etEmail.text.toString()
        val pass = binding.etPassword.text.toString()

        authViewModel.login(email, pass)
    }

    // Función para dejar los campos del formulario vacíos
    private fun clearFocus(){
        binding.etEmail.setText("")
        binding.etPassword.setText("")
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}