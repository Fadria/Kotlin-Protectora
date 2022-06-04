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
import com.feadca.protectora.model.Revision
import com.feadca.protectora.utils.REVISION_CREATE
import com.feadca.protectora.utils.REVISION_LIST
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class RevisionsViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con las revisiones
    val revisionListLD: MutableLiveData<List<Revision>> = MutableLiveData()
    val errorLD: MutableLiveData<String?> = MutableLiveData()
    val createRevisionLD: MutableLiveData<String?> = MutableLiveData()

    // Función para obtener las revisiones
    fun getRevisions(idAnimal: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = REVISION_LIST + idAnimal // URL de donde obtendremos las revisiones de los animales

            makeRevisionsRequest(url, idAnimal) // Realizamos la petición
        }
    }

    // Petición para obtener las revisiones de un animal
    private fun makeRevisionsRequest(url: String, idAnimal: Int) {
        // Cola con la que realizaremos la petición de revisiones
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val revisionsRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {
                if (it.getString("status") == "ok") {
                    // Obtenemos el listado de revisiones
                    val revisions = it.getJSONArray("data")

                    // Listado de revisiones
                    var revisionList: MutableList<Revision> = mutableListOf<Revision>()

                    // Bucle donde crearemos el array de revisiones
                    for (i in 0 until revisions.length()) {
                        val revision = revisions.getJSONObject(i)

                        // Añadimos cada objeto al array con el listado
                        revisionList.add(Revision(revision.getString("nombreVoluntario"),
                            revision.getString("fecha"), revision.getString("observaciones"),
                            revision.getInt("idAnimal")))
                    }

                    // Una vez obtenemos todas las entradas, actualizamos nuestro LiveData
                    revisionListLD.postValue(revisionList)
                } else {
                    errorLD.postValue("No se ha encontrado el animal con ID " + idAnimal.toString())
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

        queue.add(revisionsRequest) // Añadimos la petición y la realizamos
    }

    // Función usada para crear la revisión de un animal
    fun createRevision(id: String, observations: String, token: String?, date: String) {
        val url = REVISION_CREATE // Ruta donde realizaremos la petición

        val data = prepareCreateRevisionParams(id, observations, token, date)

        // Convertimos nuestro mapa en un json que enviaremos en la petición
        val jsonObject = JSONObject(data as Map<*, *>?)

        makeCreateRevisionRequest(url, jsonObject) // Realizamos la petición
    }

    // Función con la que prepararemos los parámetros que enviaremos al endpoint
    private fun prepareCreateRevisionParams(id: String, observations: String, token: String?, date: String): Any {
        val data =
            HashMap<String, HashMap<String, String>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, String>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("id", id)
        params.put("observaciones", observations)
        params.put("token", token!!)
        params.put("fecha", date)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    // Función encargada de realizar la petición para crear la revisión de un animal
    private fun makeCreateRevisionRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de creación de revisión
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val createRevisionRequest: JsonObjectRequest = object : JsonObjectRequest(
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

                // En segundo lugar, el mapa de donde obtendremos el resultado de la operación
                var resultData: Map<String, Any> = gson.fromJson(
                    responseMap.get("result").toString(),
                    object : TypeToken<Map<String, Any>>() {}.type
                )

                if (resultData["status"].toString() == "ok") {
                    // Actualizamos el valor del live data
                    createRevisionLD.postValue("Revisión creada satisfactoriamente")
                } else {
                    createRevisionLD.postValue("Se ha producido un error, inténtelo de nuevo más tarde")
                }
            },
            Response.ErrorListener { error ->
                Log.i("Se ha producido un error", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(createRevisionRequest) // Añadimos la petición y la realizamos
    }
}