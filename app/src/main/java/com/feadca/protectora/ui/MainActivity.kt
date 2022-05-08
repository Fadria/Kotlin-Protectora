package com.feadca.protectora.ui

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ActivityMainBinding
import com.feadca.protectora.ui.auth.LoginActivity
import com.feadca.protectora.viewmodel.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal


class MainActivity : AppCompatActivity() {
    // Enlace con las vistas
    private lateinit var binding: ActivityMainBinding

    // Variable con la que poder acceder al valor del appBar
    private lateinit var appBarConfiguration: AppBarConfiguration

    // Variable que contiene la referencia al ViewModel
    private lateinit var authViewModel: AuthViewModel

    // Variables que usaremos para diferentes elementos de la vista
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navHostFragment: NavHostFragment

    // Variable con la que usaremos el controlador de navegacion
    private  lateinit var navController: NavController

    // Datos usados por PayPal
    val clientKey =
        "AUeP9egNuRSoRdgmVivf0HXdZ-me2C98WeTtoHuswGi7ZA4l9oFMac1K-zaM-0aUJEarwXWPJXzJENtS"
    val PAYPAL_REQUEST_CODE = 123

    // Variable que contendrá la configuración de PayPal
    private val config = PayPalConfiguration()
        // Utilizamos el entorno NO_NETWORK, ya que es la opción que nos permite completar el proceso
        // sin la necesidad de crear una cuenta asociada a una empresa
        .environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK)
        .clientId(clientKey) // Indicamos la Key a usar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).also { binding = it }
            .root)

        // Activamos la toolbar
        setSupportActionBar(binding.toolbar)

        // Actualización de las variables del layout
        drawerLayout = binding.drawerLayout
        navView = binding.navView

        // Indicamos el fichero que contiene el ViewModel
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Preparamos el controller de navegación
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Señalamos los niveles superiores que tendremos en el menú
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.aboutFragment,
                R.id.contactFragment,
                R.id.donationsFragment,
                R.id.animalsFragment,
                R.id.blogFragment,
                R.id.profileFragment,
            ),
            drawerLayout
        )

        // Inicialmente nuestra primera ventana será about
        navView.setCheckedItem(R.id.aboutFragment);

        // Actualización del título del appbar
        (this as MainActivity?)!!.title = "Quiénes somos"

        // Icono usado para hacer logout
        binding.iwLogout.setOnClickListener {
            // Eliminamos el token del almacenamiento del dispositivo
            val prefs = getSharedPreferences(getString(R.string.shared_file), 0)
            val token = prefs.getString("TOKEN", null) // Obtenemos el valor para eliminarlo de la base de datos
            prefs.edit().remove("TOKEN").commit()

            // Llamamos a la función logout del viewmodel
            authViewModel.logout(token)

            // Realizamos un intent a la pantalla de Login
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Activamos el navController
        NavigationUI.setupWithNavController(navView, navController)
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration)

    }

    // Evitamos que al pulsar el botón de retroceso se cierre la aplicación
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START) // Cerramos el drawer
        } else {
            super.onBackPressed() // Cerramos la aplicación
        }
    }

    // Función encargada de obtener realizar la donación
    fun makePayment(amount: Double) {
        // Creamos el pago de PayPal
        val payment = PayPalPayment(
            BigDecimal(0.01), "EUR", "Donación Protectora Nuevo Lazo",
            PayPalPayment.PAYMENT_INTENT_SALE
        )

        // Creación del intent para el pago
        val intent = Intent(this, PaymentActivity::class.java)

        // Añadimos la configuración al intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)

        // Lanzamos el intent
        startActivityForResult(intent, PAYPAL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Si el código recibido es de PayPal procederemos con las siguientes acciones
        if (requestCode == PAYPAL_REQUEST_CODE) {

            // Si el resultado es OK eso significa que se ha completado el pago
            if (resultCode == RESULT_OK) {

                // Obtenemos la confirmación de pago
                val confirm: PaymentConfirmation =
                    data!!.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)!!

                // Si no es nula procederemos
                if (confirm != null) {
                    try {
                        // Obtenemos los detalles del pago
                        val paymentDetails = confirm.toJSONObject().toString(4)
                        // Preparamos los datos a mostrar en la alerta
                        val payObj = JSONObject(paymentDetails)
                        val payID = payObj.getJSONObject("response").getString("id")
                        val state = payObj.getJSONObject("response").getString("state")

                        // Mostramos la alerta
                        MaterialAlertDialogBuilder(this)
                            .setTitle(getString(R.string.payment_completed))
                            .setMessage(
                                "Pago $state\n con la id $payID"
                            )
                            .setNeutralButton(getString(R.string.cerrar)) { _, _ -> }
                            .show()
                    } catch (e: JSONException) {
                        // Informamos en el caso de que se produzca un error
                        MaterialAlertDialogBuilder(this)
                            .setTitle(getString(R.string.paymentError))
                            .setMessage(
                                getString(R.string.errorPago)
                            )
                            .setNeutralButton(getString(R.string.cerrar)) { _, _ -> }
                            .show()
                    }
                }
                // Informamos de que la donación se ha cancelado
            } else if (resultCode == RESULT_CANCELED) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.paymentCancelled))
                    .setMessage(
                        getString(R.string.donacion_cancelada)
                    )
                    .setNeutralButton(getString(R.string.cerrar)) { _, _ -> }
                    .show()
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // Ante errores desconocidos, informamos de ello
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(
                        getString(R.string.errorIntenteMasTarde)
                    )
                    .setNeutralButton(getString(R.string.cerrar)) { _, _ -> }
                    .show()
            }
        }
    }

    // Función encargada de cargar un artículo del blog
    fun loadArticle(articleId: Int) {
        var bundle: Bundle? = Bundle()
        bundle!!.putInt("articleId", articleId)

        navController.navigate(R.id.action_blogFragment_to_articleFragment, bundle)
    }
}