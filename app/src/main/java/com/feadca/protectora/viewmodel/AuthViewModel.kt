package com.feadca.protectora.viewmodel

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.feadca.protectora.R
import com.feadca.protectora.model.User
import com.feadca.protectora.utils.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * @Author: Federico Adrià Carrasco
 * @Date: 04/06/2022
 * @Email: fadriacarrasco@gmail.com
 *
 */

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con la autenticación
    val userDataLD: MutableLiveData<User?> = MutableLiveData()
    val userDataTokenLD: MutableLiveData<User?> = MutableLiveData()
    val recoverLD: MutableLiveData<String?> = MutableLiveData()
    val registerLD: MutableLiveData<String?> = MutableLiveData()
    val errorLD: MutableLiveData<String?> = MutableLiveData()

    // Función para realizar la operación de Login
    fun login(email: String, pass: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = LOGIN_URL // URL donde realizaremos la petición
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
        val loginRequest: JsonObjectRequest = object : JsonObjectRequest(
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
                        resultData["rol"].toString(), resultData["foto"].toString(), null, null, null,
                        null, null, null, null, null
                    )

                    // Actualizamos el valor del live data
                    userDataLD.postValue(loggedUser)
                } else {
                    errorLD.postValue("Error: el email o la contraseña son incorrectos")
                }
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

        queue.add(loginRequest) // Añadimos la petición y la realizamos
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

    // Función encargada de realizar un login empleando el token del usuario
    fun loginToken(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = LOGIN_TOKEN_URL // Login donde realizaremos la petición
            val data = prepareLoginTokenParams(token) // Datos a enviar en la petición

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            makeLoginTokenRequest(url, jsonObject) // Realizamos la petición
        }
    }

    // Función usada para preparar los parámetros a enviar en la petición de Login
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

    // Función usada para realizar la petición de Login mediante token
    private fun makeLoginTokenRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de Login
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val loginTokenRequest: JsonObjectRequest = object : JsonObjectRequest(
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
                        resultData["rol"].toString(), resultData["foto"].toString(), null, null, null,
                        null, null, null, null, null
                    )

                    // Actualizamos el valor del live data
                    userDataTokenLD.postValue(loggedUser)
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

        queue.add(loginTokenRequest) // Añadimos la petición y la realizamos
    }

    // Función encargada de reiniciar la contraseña del usuario
    fun recoverPassword(email: String) {
        val url = RECOVER_PASSWORD_URL

        val data = prepareRecoverPasswordParams(email) // Datos a enviar en la petición

        // Convertimos nuestro mapa en un json que enviaremos en la petición
        val jsonObject = JSONObject(data as Map<*, *>?)

        makeRecoverPasswordRequest(url, jsonObject) // Realizamos la petición
    }

    // Función usada para preparar los parámetros a enviar en la petición de recuperación de contraseña
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

    // Función encargada de realizar la petición de recuperación de contraseña
    private fun makeRecoverPasswordRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de Login
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val recoverPasswordRequest: JsonObjectRequest = object : JsonObjectRequest(
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

        queue.add(recoverPasswordRequest) // Añadimos la petición y la realizamos
    }

    // Función encargada de realizar la operación de registro de usuario
    fun register(
        email: String,
        user: String,
        pass: String,
        fullName: String,
        phone: String,
        direction: String,
        city: String,
        zipCode: String,
        dangerousDogPermission: Boolean,
        birthDate: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = REGISTER_URL // URL donde realizaremos la petición

            // Preparamos los datos a enviar en el request
            val data = prepareRegisterParams(
                email,
                user,
                pass,
                fullName,
                phone,
                direction,
                city,
                zipCode,
                dangerousDogPermission,
                birthDate
            )

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            makeRegisterRequest(url, jsonObject) // Realizamos la petición
        }
    }

    // Función encargada de preparar los datos con los que realizar la petición de registro
    private fun prepareRegisterParams(
        email: String,
        user: String,
        pass: String,
        fullName: String,
        phone: String,
        direction: String,
        city: String,
        zipCode: String,
        dangerousDogPermission: Boolean,
        birthDate: String
    ): Any {
        val data =
            HashMap<String, HashMap<String, Any>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, Any>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("email", email)
        params.put("usuario", user)
        params.put("contrasenya", pass)
        params.put("nombreCompleto", fullName)

        if (phone != "") params.put("telefono", phone)
        if (direction != "") params.put("direccion", direction)
        if (city != "") params.put("ciudad", city)
        if (zipCode != "") params.put("codigoPostal", zipCode)
        params.put("permisoPPP", dangerousDogPermission)
        if (birthDate != "") params.put("fechaNacimiento", birthDate)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    // Función encargada de realizar la petición de registro
    private fun makeRegisterRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de Login
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val registerRequest: JsonObjectRequest = object : JsonObjectRequest(
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

                registerLD.postValue(resultData["status"].toString())
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

        queue.add(registerRequest) // Añadimos la petición y la realizamos
    }

    // Función usada para realizar logout
    fun logout(token: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            if (token != null) {
                val url = LOGOUT_URL // Ruta donde realizaremos la petición
                val data = prepareLogoutParams(token!!) // Datos a enviar en la petición

                // Convertimos nuestro mapa en un json que enviaremos en la petición
                val jsonObject = JSONObject(data as Map<*, *>?)

                makeLogoutRequest(url, jsonObject) // Realizamos la petición
            }
        }
    }

    // Función usada para preparar los params del logout
    private fun prepareLogoutParams(token: String): Any {
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

    // Función encargada de realizar la petición de logout
    private fun makeLogoutRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de Login
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val logoutRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.POST,
            url,
            data,
            Response.Listener {
                // Eliminamos el token del almacenamiento del dispositivo
                val preferences: SharedPreferences = context.getSharedPreferences(context.getString(
                                    R.string.shared_file), 0)
                preferences.edit().remove("token").commit()
            },
            Response.ErrorListener { error ->
                Log.i("Error Logout", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(logoutRequest) // Añadimos la petición y la realizamos
    }
}