package com.feadca.protectora.ui.content

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentUserEditBinding
import com.feadca.protectora.model.User
import com.feadca.protectora.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class UserEditFragment : Fragment() {

    // Variable que contiene la referencia al ViewModel
    private lateinit var viewModel: UserViewModel

    private lateinit var userData: User

    // Datos del usuario a actualizar
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

    // Enlace con las vistas
    private var binding: FragmentUserEditBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        viewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Actualizamos la vinculación a la vista
        binding = FragmentUserEditBinding.bind(view)

        // Obtenemos el listado de revisiones del animal
        val arguments = arguments
        userData = (arguments!!.getParcelable("userData") as User?)!!

        loadFields(userData) // Cargamos los datos en el formulario


        binding!!.btnUpdateUser.setOnClickListener {
            if (validateFields() == true) {
                updateUser()
            } else {
                showSnackbar("Por favor, rellene los campos obligatorios")
            }
        }

        // Observamos el LD de la actualización para informar del resultado de la operación
        viewModel.updateLD.observe(viewLifecycleOwner) {
            // Cambiamos la opacidad de la pantalla para que el usuario sepa que puede continuar usando la app
            binding!!.layout.alpha = 1.0f

            // Ocultamos la progress bar
            binding!!.progressBar.visibility = View.INVISIBLE;

            // Dependiendo del resultado de la operación mostraremos un mensaje u otro
            when(it) {
                "error" -> showSnackbar("Se ha producido un error. Inténtelo de nuevo más tarde.")
                "emailUsado" -> showSnackbar("El email indicado ya está en uso")
                "usuarioUsado" -> showSnackbar("El usuario indicado ya está en uso")
                else -> {
                    showSnackbar("Actualización completada. Los cambios serán efectivos en el siguiente inicio de sesión.")
                }
            }
        }
    }

    private fun validateFields(): Any {
        // Actualizamos los valores según los datos del formulario
        email = binding!!.etEmail.text.toString()
        user = binding!!.etUser.text.toString()
        pass = binding!!.etPassword.text.toString()
        fullName = binding!!.etFullName.text.toString()
        phone = binding!!.etPhone.text.toString()
        direction = binding!!.etAddress.text.toString()
        city = binding!!.etCity.text.toString()
        zipCode = binding!!.etZipcode.text.toString()
        dangerousDogPermission = binding!!.swDangerousDog.isChecked

        // Si ningún campo se encuentra en blanco devolveremos true, en caso contrario, false
        return (email != "" && user != "" && fullName != "")
    }

    private fun updateUser() {
        // Cambiamos la opacidad de la pantalla para que el usuario vea mejor que debe esperar
        binding!!.layout.alpha = 0.5f

        // Mostramos la progress bar
        binding!!.progressBar.visibility = View.VISIBLE;

        // Obtenemos el token guardado en el dispositivo o el valor null
        val prefs = this.requireActivity().getSharedPreferences(getString(R.string.shared_file), Context.MODE_PRIVATE)
        val token = prefs.getString("TOKEN", null)

        // Llamada a la función encargada de registrar a un usuario
        viewModel.updateUser(
            email,
            user,
            pass,
            fullName,
            phone,
            direction,
            city,
            zipCode,
            dangerousDogPermission,
            birthDate,
            token
        )

        requireContext().hideKeyboard() // Ocultamos el teclado
    }

    // Función encargada de cargar los campos del formulario
    private fun loadFields(userData: User) {
        binding!!.etEmail.setText(userData.email.toString())
        binding!!.etUser.setText(userData.user.toString())
        binding!!.etFullName.setText(userData.fullName.toString())
        binding!!.etPhone.setText(userData.phone.toString())
        binding!!.etBirthDate.setText(userData.birthDay.toString())
        binding!!.etAddress.setText(userData.address.toString())
        binding!!.etCity.setText(userData.city.toString())
        binding!!.etZipcode.setText(userData.zipCode.toString())

        if(userData.licensePPP == "Con permiso PPP") binding!!.swDangerousDog.isPressed = true
    }

    // Función encargada de mostrar avisos
    private fun showSnackbar(message: String) {
        Snackbar.make(binding!!.layout, message, Snackbar.LENGTH_SHORT)
            .show()
    }

    // Función encargada de ocultar el teclado
    private fun Context.hideKeyboard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow((requireActivity().getWindow().getCurrentFocus())?.windowToken, 0)
    }

}