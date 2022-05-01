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
import com.feadca.protectora.model.User
import com.feadca.protectora.utils.CONTACT_URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class MailingViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con el mailing
    val contactLD: MutableLiveData<String?> = MutableLiveData()
    val errorLD: MutableLiveData<String?> = MutableLiveData()

    // Función para contactar con la protectora mediante un email
    fun contact(name: String, email: String, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = CONTACT_URL // Ruta donde realizaremos la petición
            val data = prepareContactParams(name, email, message) // Datos a enviar en la petición

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            makeContactRequest(url, jsonObject) // Realizamos la petición
        }
    }

    // Función con la que prepararemos los parámetros que enviaremos al endpoint
    private fun prepareContactParams(name: String, email: String, message: String): Any {
        val data =
            HashMap<String, HashMap<String, String>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, String>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("nombre", name)
        params.put("email", email)
        params.put("mensaje", message)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    // Función para realizar el request de contacto
    private fun makeContactRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de contacto
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val contactRequest: JsonObjectRequest = object : JsonObjectRequest(
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
                    contactLD.postValue("Mensaje enviado")
                } else {
                    errorLD.postValue("Se ha producido un error, inténtelo de nuevo más tarde")
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

        queue.add(contactRequest) // Añadimos la petición y la realizamos
    }
}