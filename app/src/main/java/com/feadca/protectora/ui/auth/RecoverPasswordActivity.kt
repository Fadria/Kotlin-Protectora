package com.feadca.protectora.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.databinding.ActivityRecoverPasswordBinding
import com.feadca.protectora.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class RecoverPasswordActivity : AppCompatActivity() {
    lateinit var binding: ActivityRecoverPasswordBinding

    // Datos usados para recuperar la contraseña
    lateinit var email: String

    // Variable que contiene la referencia al ViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Indicamos el fichero que contiene el ViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Acciones ejecutadas al detectar una actualización en el LiveData successLD
        authViewModel.recoverLD.observe(this) {
            // Cambiamos la opacidad de la pantalla para que el usuario sepa que puede continuar usando la app
            binding.layout.alpha = 1.0f

            // Ocultamos la progress bar
            binding.progressBar.visibility = View.INVISIBLE;

            // Permitimos que se puedan usar los inputs de nuevo
            binding.etEmail.isEnabled = true;

            if (it == "error") {
                showSnackbar("No se ha encontrado el email indicado")
            } else {
                showSnackbar("Solicitud completada: revise su bandeja de entrada")
            }
        }

        // Realizamos las acciones pertinentes a la recuperación de contraseña
        binding.btnRecoverPassword.setOnClickListener {
            email = binding.etEmail.text.toString()

            if (email != "") {
                // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
                binding.layout.alpha = 0.5f

                // Mostramos la progress bar
                binding.progressBar.visibility = View.VISIBLE;

                // Impedimos que se puedan usar los inputs mientras realizamos la llamada a la API
                binding.etEmail.isEnabled = false;

                recoverPassword(email) // Llamamos a la función que contactará con el ViewModel

                hideKeyboard() // Ocultamos el teclado
                clearFocus() // Vaciamos los campos del formulario
            }else{
                showSnackbar("Por favor, introduzca un email")
            }
        }

        // Navegamos a la actividad del Login
        binding.tvGoToLogin.setOnClickListener {
            finish()
        }
    }

    // Función encargada de recuperar la contraseña
    private fun recoverPassword(email: String) {
        val email = binding.etEmail.text.toString()

        authViewModel.recoverPassword(email)
    }

    // Función para dejar los campos del formulario vacíos
    private fun clearFocus() {
        binding.etEmail.setText("")
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }
}