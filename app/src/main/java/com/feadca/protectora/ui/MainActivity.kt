package com.feadca.protectora.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.Glide
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ActivityMainBinding
import com.feadca.protectora.model.Revision
import com.feadca.protectora.model.User
import com.feadca.protectora.ui.auth.LoginActivity
import com.feadca.protectora.viewmodel.AuthViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.paypal.android.sdk.payments.*
import org.json.JSONException
import org.json.JSONObject
import org.w3c.dom.Text
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

        // Actualizamos el drawer con los valores del usuario;
        navView.getHeaderView(0).findViewById<TextView>(R.id.userName).text = intent.getStringExtra("USER")

        // Si no es un voluntario ocultaremos las secciones que no puedan ser usadas
        if ( intent.getStringExtra("ROLE") == "voluntario") {
            // Añadimos la imagen del rol
            Glide.with(navController.context)
                .load(R.drawable.default_user_icon) // Imagen a mostrar
                .placeholder(AppCompatResources.getDrawable(navController.context, R.drawable.loading)) // Imagen mostrada durante la carga
                .error(AppCompatResources.getDrawable(navController.context, R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                .into(binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.userImage)) // Indicamos donde serán colocadas las imágenes en la vista
        }

        if( intent.getStringExtra("ROLE") == "invitado") {
            val menu = navView.menu
            menu.findItem(R.id.revisionsFragment).isVisible = false
            menu.findItem(R.id.createRevisionFragment).isVisible = false
            menu.findItem(R.id.profileFragment).isVisible = false

            // Añadimos la imagen del rol
            Glide.with(navController.context)
                .load(R.drawable.default_user_icon2) // Imagen a mostrar
                .placeholder(AppCompatResources.getDrawable(navController.context, R.drawable.loading)) // Imagen mostrada durante la carga
                .error(AppCompatResources.getDrawable(navController.context, R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                .into(binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.userImage)) // Indicamos donde serán colocadas las imágenes en la vista
        }

        if( intent.getStringExtra("ROLE") == "adoptante") {
            val menu = navView.menu
            menu.findItem(R.id.profileFragment).isVisible = true

            // Añadimos la imagen del rol
            Glide.with(navController.context)
                .load(R.drawable.default_user_icon3) // Imagen a mostrar
                .placeholder(AppCompatResources.getDrawable(navController.context, R.drawable.loading)) // Imagen mostrada durante la carga
                .error(AppCompatResources.getDrawable(navController.context, R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                .into(binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.userImage)) // Indicamos donde serán colocadas las imágenes en la vista
        }

        // Señalamos los niveles superiores que tendremos en el menú
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.aboutFragment,
                R.id.contactFragment,
                R.id.donationsFragment,
                R.id.animalsFragment,
                R.id.blogFragment,
                R.id.profileFragment,
                R.id.graphicsFragment,
                R.id.revisionsFragment,
                R.id.createRevisionFragment,
                R.id.becomeVolunteerFragment,
                R.id.requirementsFragment,
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
            BigDecimal(amount), "EUR", "Donación Protectora Nuevo Lazo",
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
                                "Muchas gracias por su pago. ID de la transacción: $payID"
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

        navController.navigate(R.id.action_blogFragment_to_articleFragment2, bundle)
    }

    // Función usada para navegar a la lista de revisiones
    fun navigateToRevisionList(revisionList: List<Revision>?) {
        var bundle: Bundle? = Bundle()
        bundle!!.putParcelableArray("revisionList", revisionList!!.toTypedArray())

        navController.navigate(R.id.action_revisionsFragment_to_revisionListFragment, bundle)
    }

    // Función usada para navegar a los datos de un animal
    fun loadAnimal(id: Int) {
        var bundle: Bundle? = Bundle()
        bundle!!.putInt("idAnimal", id)

        navController.navigate(R.id.action_animalsFragment_to_animalFragment, bundle)
    }

    fun navigateToEditUser(userData: User) {
        var bundle: Bundle? = Bundle()
        bundle!!.putParcelable("userData", userData)

        navController.navigate(R.id.action_profileFragment_to_userEditFragment, bundle)
    }

    fun loadRequirement(id: Int) {
        var bundle: Bundle? = Bundle()
        bundle!!.putInt("idRequirement", id)

        navController.navigate(R.id.action_requirementsFragment_to_requirementFragment, bundle)
    }
}