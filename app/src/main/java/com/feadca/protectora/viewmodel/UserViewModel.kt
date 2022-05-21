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
import com.feadca.protectora.utils.USER_UPDATE
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
    val updateLD: MutableLiveData<String?> = MutableLiveData()
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
                    val userData: User = User(
                        null, resultData["usuario"].toString(),
                        resultData["rol"].toString(), resultData["foto"].toString(), resultData["nombreCompleto"].toString(),
                        resultData["email"].toString(), resultData["telefono"].toString(), resultData["direccion"].toString()
                        ,resultData["ciudad"].toString(), resultData["codigoPostal"].toString(),
                        resultData["permisoPPP"].toString(), resultData["fechaNacimiento"].toString()
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

    fun updateUser(
        email: String,
        user: String,
        pass: String,
        fullName: String,
        phone: String,
        direction: String,
        city: String,
        zipCode: String,
        dangerousDogPermission: Boolean,
        birthDate: String,
        token: String?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = USER_UPDATE // URL donde realizaremos la petición

            // Preparamos los datos a enviar en el request
            val data = prepareUpdateUserParams(
                email,
                user,
                pass,
                fullName,
                phone,
                direction,
                city,
                zipCode,
                dangerousDogPermission,
                birthDate,
                token
            )

            // Convertimos nuestro mapa en un json que enviaremos en la petición
            val jsonObject = JSONObject(data as Map<*, *>?)

            Log.i("aaaaaaaaaaaaaaaaaaaa", jsonObject.toString())

            makeUpdateUserRequest(url, jsonObject) // Realizamos la petición
        }
    }

    private fun prepareUpdateUserParams(
        email: String,
        user: String,
        pass: String,
        fullName: String,
        phone: String,
        direction: String,
        city: String,
        zipCode: String,
        dangerousDogPermission: Boolean,
        birthDate: String,
        token: String?
    ): Any {
        val data =
            HashMap<String, HashMap<String, Any>>() // Mapa que contendrá el cuerpo de la petición
        val params = HashMap<String, Any>() // Mapa con los parámetros a enviar

        // Añadimos los parámetros a enviar
        params.put("email", email)
        params.put("usuario", user)
        if ( pass != "" ) params.put("contrasenya", pass)
        params.put("nombreCompleto", fullName)
        if ( phone != "" ) params.put("telefono", phone)
        if ( direction != "" ) params.put("direccion", direction)
        if ( city != "" ) params.put("ciudad", city)
        if ( zipCode != "" ) params.put("codigoPostal", zipCode)
        params.put("permisoPPP", dangerousDogPermission)
        if ( birthDate != "" ) params.put("fechaNacimiento", birthDate)

        params.put("token", token!!)

        // Añadimos esos parámetros al cuerpo
        data.put("data", params)

        // Devolvemos los datos
        return data
    }

    private fun makeUpdateUserRequest(url: String, data: JSONObject) {
        // Cola con la que realizaremos la petición de Login
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val updateRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.PUT,
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

                updateLD.postValue(resultData["status"].toString())
            },
            Response.ErrorListener { error ->
                Log.i("error:", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(updateRequest) // Añadimos la petición y la realizamos
    }
}