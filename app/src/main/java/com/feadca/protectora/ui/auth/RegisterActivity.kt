package com.feadca.protectora.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.databinding.ActivityRegisterBinding
import com.feadca.protectora.viewmodel.AuthViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

class RegisterActivity : AppCompatActivity() {
    // Enlace con las vistas
    lateinit var binding: ActivityRegisterBinding

    // Datos del usuario a registrar
    lateinit var email: String
    lateinit var user: String
    lateinit var pass: String
    lateinit var fullName: String
    lateinit var phone: String
    var birthDate = ""
    lateinit var direction: String
    lateinit var city: String
    lateinit var zipCode: String
    var dangerousDogPermission: Boolean = false

    // Variable que contiene la referencia al ViewModel
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Indicamos el fichero que contiene el ViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Observamos el LD del registro para informar del resultado de la operación
        authViewModel.registerLD.observe(this) {
            // Cambiamos la opacidad de la pantalla para que el usuario sepa que puede continuar usando la app
            binding.layout.alpha = 1.0f

            // Ocultamos la progress bar
            binding.progressBar.visibility = View.INVISIBLE;

            // Dependiendo del resultado de la operación mostraremos un mensaje u otro
            when(it) {
                "error" -> showSnackbar("Se ha producido un error. Inténtelo de nuevo más tarde.")
                "emailUsado" -> showSnackbar("El email indicado ya está en uso")
                "usuarioUsado" -> showSnackbar("El usuario indicado ya está en uso")
                else -> {
                    showSnackbar("Registro completado correctamente. Proceda a iniciar sesión")
                }
            }
        }

        // Botón que desencadenará la opereración de registro
        binding.btnRegister.setOnClickListener {
            if (validateFields() == true) {
                registerUser()
            } else {
                showSnackbar("Por favor, rellene los campos obligatorios")
            }
        }

        // Listener para navegar a la actividad del Login
        binding.tvGoToLogin.setOnClickListener {
            finish()
        }

        // Listener para elegir la fecha de nacimiento
        binding.etBirthDate.setOnClickListener {
            chooseBirthDate()
        }
    }

    // Función usada para valdar los campos del formulario y actualizar los valores de las variables
    private fun validateFields(): Any {
        // Actualizamos los valores según los datos del formulario
        email = binding.etEmail.text.toString()
        user = binding.etUser.text.toString()
        pass = binding.etPassword.text.toString()
        fullName = binding.etFullName.text.toString()
        phone = binding.etPhone.text.toString()
        direction = binding.etAddress.text.toString()
        city = binding.etCity.text.toString()
        zipCode = binding.etZipcode.text.toString()
        dangerousDogPermission = binding.swDangerousDog.isChecked

        // Si ningún campo se encuentra en blanco devolveremos true, en caso contrario, false
        return (email != "" && user != "" && pass != "" && fullName != "")
    }

    // Función usada para registrar un usuario mediante el viewModel
    private fun registerUser() {
        // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
        binding.layout.alpha = 0.5f

        // Mostramos la progress bar
        binding.progressBar.visibility = View.VISIBLE;

        // Llamada a la función encargada de registrar a un usuario
        authViewModel.register(
            email,
            user,
            pass,
            fullName,
            phone,
            direction,
            city,
            zipCode,
            dangerousDogPermission,
            birthDate
        )

        hideKeyboard() // Ocultamos el teclado
    }

    // Función que nos permitirá elegir la fecha de nacimiento
    private fun chooseBirthDate() {
        // Inicializamos el datePicker y lo buildeamos
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccione su fecha de nacimiento")
            .build()

        // Mostramos el datePicker
        datePicker.show(supportFragmentManager, "DatePicker")

        // Evento que será ejecutado cuando el usuario confirme la fecha
        datePicker.addOnPositiveButtonClickListener {
            // Formateamos la fecha al formato usado en la base de datos
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd")

            // Actualizamos el valor de nuestra variable
            birthDate = dateFormatter.format(Date(it))
            binding.etBirthDate.setText(birthDate)
        }
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(binding.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}