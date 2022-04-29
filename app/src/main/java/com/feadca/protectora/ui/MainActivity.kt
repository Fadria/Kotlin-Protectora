package com.feadca.protectora.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ActivityMainBinding.inflate(layoutInflater).also { binding = it }
            .root)

        // Activamos la toolbar
        setSupportActionBar(binding.toolbar)

        // Actualización de las variables del layout
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        // Preparamos el controller de navegación
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Señalamos los niveles superiores que tendremos en el menú
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.aboutFragment,
                R.id.contactFragment,
                R.id.donationsFragment,
                R.id.animalsFragment,
                R.id.graphicsFragment,
                R.id.profileFragment,
            ),
            drawerLayout
        )

        // Inicialmente nuestra primera ventana será about
        navView.setCheckedItem(R.id.aboutFragment);

        // Icono usado para hacer logout
        binding.iwLogout.setOnClickListener {
            Toast.makeText(this, "WIP: logout", Toast.LENGTH_SHORT).show()
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
}