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
import com.feadca.protectora.utils.LOGIN_TOKEN_URL
import com.feadca.protectora.utils.LOGIN_URL
import com.feadca.protectora.utils.RECOVER_PASSWORD_URL
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con la autenticación
    val userDataLD: MutableLiveData<User?> = MutableLiveData()
    val recoverLD: MutableLiveData<String?> = MutableLiveData()
    val errorLD: MutableLiveData<String?> = MutableLiveData()

    // Función para realizar la operación de Login
    fun login(email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {

            val url = LOGIN_URL // Login donde realizaremos la petición
            val data = prepareLoginParams(email, pass) // Datos a enviar en la petición

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            makeLoginRequest(url, jsonObject) // Realizamos la petición
        }
    }

    // Función usada para realizar una petición de Login
    private fun makeLoginRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de Login
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val getRequest: JsonObjectRequest = object : JsonObjectRequest(
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
                    // Creamos una variable de tipo usuario con los datos recibidos
                    val loggedUser: User = User(
                        resultData["token"].toString(), resultData["usuario"].toString(),
                        resultData["rol"].toString(), resultData["foto"].toString()
                    )

                    // Actualizamos el valor del live data
                    userDataLD.postValue(loggedUser)
                } else {
                    errorLD.postValue("Error: el email o la contraseña son incorrectos")
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

        queue.add(getRequest) // Añadimos la petición y la realizamos
    }

    // Función con la que prepararemos los parámetros que enviaremos al Login
    private fun prepareLoginParams(email: String, pass: String): Any {
        val data =
            HashMap<String, HashMap<String, String>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, String>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("email", email)
        params.put("contrasenya", pass)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    fun loginToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = LOGIN_TOKEN_URL // Login donde realizaremos la petición
            val data = prepareLoginTokenParams(token) // Datos a enviar en la petición

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            makeLoginTokenRequest(url, jsonObject) // Realizamos la petición
        }
    }

    private fun prepareLoginTokenParams(token: String): Any {
        val data =
            HashMap<String, HashMap<String, String>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, String>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("token", token)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    private fun makeLoginTokenRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de Login
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val getRequest: JsonObjectRequest = object : JsonObjectRequest(
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
                    // Creamos una variable de tipo usuario con los datos recibidos
                    val loggedUser: User = User(
                        null, resultData["usuario"].toString(),
                        resultData["rol"].toString(), resultData["foto"].toString()
                    )

                    // Actualizamos el valor del live data
                    userDataLD.postValue(loggedUser)
                } else {
                    errorLD.postValue("Token caducado, inicie sesión")
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

        queue.add(getRequest) // Añadimos la petición y la realizamos
    }

    fun recoverPassword(email: String) {
        val url = RECOVER_PASSWORD_URL

        val data = prepareRecoverPasswordParams(email) // Datos a enviar en la petición

        // Convertimos nuestro mapa en un json que enviaremos en la petición
        val jsonObject = JSONObject(data as Map<*, *>?)

        makeRecoverPasswordRequest(url, jsonObject) // Realizamos la petición
    }

    private fun prepareRecoverPasswordParams(email: String): Any {
        val data =
            HashMap<String, HashMap<String, String>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, String>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("email", email)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    private fun makeRecoverPasswordRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de Login
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val getRequest: JsonObjectRequest = object : JsonObjectRequest(
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

                recoverLD.postValue(resultData["status"].toString())
            },
            Response.ErrorListener { error ->
                Log.i("Error Register", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(getRequest) // Añadimos la petición y la realizamos
    }

}