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
import com.feadca.protectora.model.Animal
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.utils.ANIMAL_LIST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnimalsViewModel (application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con los animales
    val animalListLD: MutableLiveData<List<Animal>> = MutableLiveData()

    // Función para obtener los animales de la protectora
    fun getAnimals() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = ANIMAL_LIST // URL de donde obtendremos los animales

            makeAnimalsRequest(url) // Realizamos la petición
        }
    }

    // Petición para obtener los animales
    private fun makeAnimalsRequest(url: String) {
        // Cola con la que realizaremos la petición de animales
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val animalsRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {

                // Obtenemos el listado de animales
                val articles = it.getJSONArray("data")

                // Listado de entradas del blog
                var animalList: MutableList<Animal> = mutableListOf<Animal>()

                // Bucle donde crearemos el array de animales
                for (i in 0 until articles.length()) {
                    val animal = articles.getJSONObject(i)

                    // Añadimos cada objeto al array con el listado
                    animalList.add(
                        Animal(animal.getInt("id"), animal.getString("nombre"), animal.getString("imagen"),
                            null, animal.getString("especie"), null, null, animal.getInt("edad"),
                            animal.getString("sexo"), animal.getString("tamanyo"), null, null,
                            null, null, null, null, null, null, null)
                    )
                }

                // Una vez obtenemos todas las entradas, actualizamos nuestro LiveData
                animalListLD.postValue(animalList)
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

        queue.add(animalsRequest) // Añadimos la petición y la realizamos
    }
}