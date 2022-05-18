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
import com.feadca.protectora.model.Image
import com.feadca.protectora.utils.ANIMAL_INFO
import com.feadca.protectora.utils.ANIMAL_LIST
import com.feadca.protectora.utils.ANIMAL_PAGE
import com.feadca.protectora.utils.FILTER_ANIMALS
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class AnimalsViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con los animales
    val animalListLD: MutableLiveData<List<Animal>> = MutableLiveData()
    val animalFilteredListLD: MutableLiveData<List<Animal>> = MutableLiveData()
    val animalDataLD: MutableLiveData<Animal> = MutableLiveData()
    val infoLD: MutableLiveData<String> = MutableLiveData()
    val errorLD: MutableLiveData<String> = MutableLiveData()

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
                        Animal(
                            animal.getInt("id"),
                            animal.getString("nombre"),
                            animal.getString("imagen"),
                            null,
                            animal.getString("especie"),
                            null,
                            null,
                            animal.getInt("edad"),
                            animal.getString("sexo"),
                            animal.getString("tamanyo"),
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null
                        )
                    )
                }

                // Una vez obtenemos todos los registros, actualizamos nuestro LiveData
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

    fun filterAnimals(species: String, size: String, gender: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = FILTER_ANIMALS // Url donde realizaremos la petición
            val data = prepareFilterParams(species, size, gender) // Datos a enviar en la petición

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            makeFilterRequest(url, jsonObject) // Realizamos la petición
        }
    }

    // Función con la que prepararemos los parámetros que enviaremos al Login
    private fun prepareFilterParams(species: String, size: String, gender: String): Any {
        val data =
            HashMap<String, HashMap<String, String>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, String>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("especie", species)
        params.put("sexo", gender)

        if (size == "pequeño") {
            params.put("tamanyo", "pequenyo")
        } else {
            params.put("tamanyo", size)
        }

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    // Petición para obtener los animales
    private fun makeFilterRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de animales
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val filterRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            data,
            Response.Listener {
                val gson = Gson() // Inicializamos nuestra variable para trabajar con JSON
                val mapType =
                    object : TypeToken<Map<String, Any>>() {}.type // Mapa que recibiremos de la API

                // En primer lugar, obtenemos el mapa que nos devuelve ese response
                var responseMap: Map<String, Any> =
                    gson.fromJson(it.toString(), object : TypeToken<Map<String, Any>>() {}.type)

                // En segundo lugar, el mapa de donde obtendremos los datos
                var resultData: Map<String, Any> = gson.fromJson(
                    responseMap.get("result").toString(),
                    object : TypeToken<Map<String, Any>>() {}.type
                )

                // En tercer lugar, obtenemos el listado de ids de los animales a mostrar
                val animalJSON = resultData["data"]

                // Comprobamos que se ha encontrado al menos un registro con los datos indicados
                if(animalJSON != null) {
                    // Obtenemos el listado de animales
                    val typeToken = object : TypeToken<List<Animal>>() {}.type
                    val animals: List<Animal> = Gson().fromJson<List<Animal>>(animalJSON.toString(), typeToken)

                    // Obtenemos el listado de ids a mostrar
                    var listIds: MutableList<Int> = mutableListOf()
                    for (i in animals.indices) {
                        listIds.add(animals[i].id)
                    }

                    // Mostramos los animales que cumplan el filtro, en este caso coincidir con una id
                    animalFilteredListLD.postValue(animalListLD.value!!.filter { an ->
                        an.id in listIds
                    })
                }else{
                    errorLD.postValue("No se han encontrado animales que cumplan con los filtros indicados")
                }
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

        queue.add(filterRequest) // Añadimos la petición y la realizamos
    }

    fun loadAnimal(idAnimal: Int) {
        val url = ANIMAL_PAGE + idAnimal // URL donde realizaremos la petición

        makeAnimalRequest(url) // Realizamos la petición
    }

    private fun makeAnimalRequest(url: String) {
        // Cola con la que realizaremos la petición para obtener los datos del animal
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val animalRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {
                // Obtenemos el objeto animal
                val data = it.getJSONObject("data")

                // Obtenemos el listado de imágenes del animal
                var listadoImagenes: MutableList<Image> = mutableListOf()

                // Variable que contendrá el listado de imágenes del animal
                var imagenes = data.getJSONArray("imagenes")

                // Actualizamos el valor de la variable
                if (imagenes.length() > 0) {
                    for (i in 0 until imagenes.length()) {
                        val imagen = imagenes.getJSONObject(i)
                        listadoImagenes.add(Image(imagen.getInt("id"), imagen.getString("fecha"),
                            imagen.getString("imagen")))
                    }
                }else{
                    imagenes = null
                }

                // Creamos el objeto animal
                val animal:Animal = Animal(data.getInt("id"), data.getString("nombre"), data.getString("imagen"),
                data.getString("chip"), data.getString("especie"), data.getString("raza"), data.getString("nacimiento"),
                data.getInt("edad"), data.getString("sexo"), data.getString("tamanyo"), data.getString("urgente"),
                data.getDouble("peso"), data.getString("esterilizado"), data.getString("exotico"),
                data.getString("observaciones"), data.getString("pelo"), data.getString("historia"),
                data.getString("perroPeligroso"), listadoImagenes)

                // Actualizamos el LiveData
                animalDataLD.postValue(animal)
            },
            Response.ErrorListener { error ->
                Log.i("Error: ", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(animalRequest) // Añadimos la petición y la realizamos
    }

    // Función usada para pedir más información sobre un animal
    fun requestInfo(email: String, idAnimal: Int, animalName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = ANIMAL_INFO // Url donde realizaremos la petición
            val data = prepareInfoParams(email, idAnimal, animalName) // Datos a enviar en la petición

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            makeInfoRequest(url, jsonObject) // Realizamos la petición
        }
    }

    // Función usada para preparar los parámetros de solicitud de información
    private fun prepareInfoParams(email: String, idAnimal: Int, animalName: String): Any {
        val data =
            HashMap<String, HashMap<String, String>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, String>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("idAnimal", idAnimal.toString())
        params.put("email", email)
        params.put("nombre", animalName)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    // Función usada para el request de solicitud de información sobre un animal
    private fun makeInfoRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val infoRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            data,
            Response.Listener {
                val gson = Gson() // Inicializamos nuestra variable para trabajar con JSON
                val mapType =
                    object : TypeToken<Map<String, Any>>() {}.type // Mapa que recibiremos de la API

                // En primer lugar, obtenemos el mapa que nos devuelve ese response
                var responseMap: Map<String, Any> =
                    gson.fromJson(it.toString(), object : TypeToken<Map<String, Any>>() {}.type)

                // En segundo lugar, el mapa de donde obtendremos los datos del usuario logueado y el estado
                var resultData: Map<String, Any> = gson.fromJson(
                    responseMap.get("result").toString(),
                    object : TypeToken<Map<String, Any>>() {}.type
                )

                if (resultData["status"].toString() == "ok") {
                    // Actualizamos el valor del live data
                    infoLD.postValue("Solicitud enviada correctamente")
                } else {
                    errorLD.postValue("Solicitud incorrecta")
                }
            },
            Response.ErrorListener { error ->
                Log.i("Error Login", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(infoRequest) // Añadimos la petición y la realizamos
    }

}