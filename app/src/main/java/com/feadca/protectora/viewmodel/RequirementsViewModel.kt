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
import com.feadca.protectora.model.Requirement
import com.feadca.protectora.utils.REQUIREMENTS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RequirementsViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para los requisitos de adopción
    val requirementsLD: MutableLiveData<List<Requirement>> = MutableLiveData()
    val requirementLD: MutableLiveData<Requirement?> = MutableLiveData()

    // Función para obtener los requisitos
    fun getRequirements() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = REQUIREMENTS // URL de donde obtendremos los requisitos

            makeRequirementsRequest(url) // Realizamos la petición
        }
    }

    // Función usada para realizar la petición usada con la finalidad de obtener los requisitos
    private fun makeRequirementsRequest(url: String) {
        // Cola con la que realizaremos la petición de requisitos
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val requirementRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {

                // Obtenemos el listado de requisitos
                val requirements = it.getJSONArray("data")

                // Listado de requisitos
                var requirementList: MutableList<Requirement> = mutableListOf<Requirement>()

                // Bucle donde crearemos el array de artículos
                for (i in 0 until requirements.length()) {
                    val requirement = requirements.getJSONObject(i)

                    // Añadimos cada objeto al array con el listado
                    requirementList.add(
                        Requirement(requirement.getInt("id"), requirement.getString("titulo"),
                            null, null)
                    )
                }

                // Una vez obtenemos todas las entradas, actualizamos nuestro LiveData
                requirementsLD.postValue(requirementList)
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

        queue.add(requirementRequest) // Añadimos la petición y la realizamos
    }

    // Función usada para obtener los datos de un requisito
    fun loadRequirement(requirementId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "$REQUIREMENTS/$requirementId" // URL de donde obtendremos los datos del requisito

            makeRequirementRequest(url, requirementId) // Realizamos la petición
        }
    }

    // Función usada para realizar la petición encargada de obtener los datos de un requisito
    private fun makeRequirementRequest(url: String, requirementId: Int) {
        // Cola con la que realizaremos la petición
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val requirementRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {

                // Obtenemos el requisito
                val requirementJSON = it.getJSONArray("data").getJSONObject(0)

                // Creamos y añadimos el objeto requisito a su live data
                val requirement: Requirement = Requirement(requirementId,
                    requirementJSON.getString("titulo"),
                    requirementJSON.getString("imagen"), requirementJSON.getString("contenido"))

                requirementLD.postValue(requirement)
            },
            Response.ErrorListener { error ->
                Log.i("Error", error.toString())
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["User-Agent"] = "Mozilla/5.0"
                return params
            }
        }

        queue.add(requirementRequest) // Añadimos la petición y la realizamos
    }

}