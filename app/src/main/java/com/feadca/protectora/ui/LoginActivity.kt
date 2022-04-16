package com.feadca.protectora.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ActivityLoginBinding
import com.feadca.protectora.model.User
import com.feadca.protectora.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    // Datos usados en el Login
    lateinit var email: String
    lateinit var pass: String

    // Variable que contiene la referencia al ViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Indicamos el fichero que contiene el ViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        loginWithToken() // Al cargar la aplicación intentaremos loguearnos con el token

        // Acciones ejecutadas al detectar una actualización en el LiveData userDataLD
        authViewModel.userDataLD.observe(this) {
            val prefs =
                getSharedPreferences(getString(R.string.shared_file), Context.MODE_PRIVATE).edit()
            prefs.putString("TOKEN", it!!.token)
            prefs.apply()

            showDrawer(it)
        }

        // Acciones ejecutadas al detectar una actualización en el LiveData errorLD
        authViewModel.errorLD.observe(this) {
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

            if (email != "" && pass != "") {
                // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
                binding.layout.alpha = 0.5f

                // Mostramos la progress bar
                binding.progressBar.visibility = View.VISIBLE;

                // Impedimos que se puedan usar los inputs mientras realizamos la llamada a la API
                binding.etEmail.isEnabled = false;
                binding.etPassword.isEnabled = false;

                login(email, pass) // Llamamos a la función que contactará con el ViewModel

                hideKeyboard() // Ocultamos el teclado
                clearFocus() // Vaciamos los campos del formulario
            } else {
                showSnackbar("Introduzca su email y contraseña")
            }
        }

        // Navegamos a la actividad de recuperación de contraseña
        binding.tvRecoverPassword.setOnClickListener {
            val recoverPasswordIntent = Intent(this, RecoverPasswordActivity::class.java)
            startActivity(recoverPasswordIntent)
        }
    }

    private fun showDrawer(it: User?) {
        // TODO: dependiendo del rol nos situaremos en un drawer u otro

        val drawerIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("USER", it!!.usuario)
            putExtra("USER", it!!.rol)
            putExtra("USER", it!!.foto)
        }

        startActivity(drawerIntent)
        finish()
    }

    // Función encargada de mostrar avisos
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

    // Función encargada de realizar el login con un token
    private fun loginWithToken() {
        // Obtenemos el token guardado en el dispositivo o el valor null
        val prefs = getSharedPreferences(getString(R.string.shared_file), Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", null)

        // Si tenemos un valor guardado, realizaremos el login
        if (token != null) {
            authViewModel.loginToken(token)
        }
    }

    // Función para dejar los campos del formulario vacíos
    private fun clearFocus() {
        binding.etEmail.setText("")
        binding.etPassword.setText("")
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}