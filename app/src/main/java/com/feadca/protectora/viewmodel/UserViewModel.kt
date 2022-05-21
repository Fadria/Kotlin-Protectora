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
import com.feadca.protectora.utils.USER_DATA
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class UserViewModel (application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con los usuarios
    val userLD: MutableLiveData<User> = MutableLiveData()
    val errorLD: MutableLiveData<String?> = MutableLiveData()

    fun loadUserProfile(token: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = USER_DATA // URL de donde obtendremos los datos del usuario

            val data = prepareProfileParams(token) // Datos a enviar en la petición

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            makeUserProfileRequest(url, jsonObject) // Realizamos la petición
        }
    }

    // Función con la que prepararemos los parámetros que enviaremos a la API
    private fun prepareProfileParams(token: String?): Any {
        val data =
            HashMap<String, HashMap<String, String>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, String>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("token", token!!)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    private fun makeUserProfileRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
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
                    // Creamos una variable de tipo usuario con los datos recibidos
                    val userData: User = User(
                        null, resultData["usuario"].toString(),
                        resultData["rol"].toString(), resultData["foto"].toString(), null, null, null,
                        null, null, null, null, null
                    )

                    // Actualizamos el valor del live data
                    userLD.postValue(userData)
                } else {
                    errorLD.postValue("Se ha producido un error")
                }
            },
            Response.ErrorListener { error ->
                Log.i("error: ", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(request) // Añadimos la petición y la realizamos
    }
}