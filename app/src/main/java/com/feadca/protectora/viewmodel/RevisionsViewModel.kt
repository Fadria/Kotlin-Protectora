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
import com.feadca.protectora.utils.REVISION_LIST
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RevisionsViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con las revisiones
    val revisionListLD: MutableLiveData<List<Revision>> = MutableLiveData()
    val errorLD: MutableLiveData<String?> = MutableLiveData()

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
                            revision.getString("fecha"), revision.getString("observaciones")))
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
}