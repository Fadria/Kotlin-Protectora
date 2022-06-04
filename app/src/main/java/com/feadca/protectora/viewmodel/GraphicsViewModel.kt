package com.feadca.protectora.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.model.Graphic
import com.feadca.protectora.utils.GRAPHICS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

class GraphicsViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con los gráficos
    val graphicsLD: MutableLiveData<List<Graphic>> = MutableLiveData()

    // Función para obtener los gráficos
    fun getGraphics() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = GRAPHICS // URL de donde obtendremos los gráficos

            makeGraphicsRequest(url) // Realizamos la petición
        }
    }

    // Petición para obtener los gráficos
    private fun makeGraphicsRequest(url: String) {
        // Cola con la que realizaremos la petición de gráficos
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val graphicsRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {
                // Obtenemos el listado de gráficos
                val graphics = it.getJSONArray("data")

                // Variable usada para el listado de gráficos instanciados en objetos Graphic
                var graphicList: MutableList<Graphic> = mutableListOf<Graphic>()

                // Bucle donde crearemos el array de gráficos
                for (i in 0 until graphics.length()) {
                    val graphic = graphics.getJSONObject(i)

                    // Añadimos cada objeto al array con el listado
                    graphicList.add(
                        Graphic(graphic.getString("titulo"), "Fecha de publicación: " + graphic.getString("fecha"),
                            graphic.getString("imagen"))
                    )
                }

                // Una vez obtenemos todos los gráficos, actualizamos nuestro LiveData
                graphicsLD.postValue(graphicList)
            },
            Response.ErrorListener { error ->
                Log.i("Error:", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(graphicsRequest) // Añadimos la petición y la realizamos
    }
}