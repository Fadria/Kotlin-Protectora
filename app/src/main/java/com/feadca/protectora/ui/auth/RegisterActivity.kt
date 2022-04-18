package com.feadca.protectora.ui.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.databinding.ActivityRegisterBinding
import com.feadca.protectora.viewmodel.AuthViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    // Datos del usuario a registrar
    lateinit var email: String
    lateinit var user: String
    lateinit var pass: String
    lateinit var fullName: String
    lateinit var phone: String
    lateinit var birthDate: String
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

        // Navegamos a la actividad del Login
        binding.tvGoToLogin.setOnClickListener {
            finish()
        }

        // Listener para elegir la fecha de nacimiento
        binding.etBirthDate.setOnClickListener {
            chooseBirthDate()
        }
    }

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
        return (email != "" && user != "" && pass != "" && fullName != "" && phone != "" && direction != ""
                && city != "" && zipCode != "" && birthDate != "")
    }

    // Función usada para registrar un usuario mediante el viewModel
    private fun registerUser() {
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