package com.feadca.protectora.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.feadca.protectora.R
import com.feadca.protectora.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var binding: ActivityMainBinding

    // Variables que contendrán las referencias al layout
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar;

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializamos las variables
        drawerLayout = binding.drawerLayout
        navigationView = binding.mainNavView
        toolbar = binding.toolbar

        // Activamos la toolbar
        setSupportActionBar(toolbar)

        // Preparamos la action bar
        var toogle:ActionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()

        // Indicamos las funciones que usaremos al pulsar cada opción
        navigationView.setNavigationItemSelectedListener(this)

        // La opción seleccionada por defecto será nav_home
        navigationView.setCheckedItem(R.id.nav_home)
    }

    // Evitamos que al pulsar el botón de retroceso se cierre la aplicación
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START) // Cerramos el drawer
        } else {
            super.onBackPressed() // Cerramos la aplicación
        }
    }


    // Función usada para indicar las funciones que ejecutará cada opción del menú
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // Cerramos el drawer
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}