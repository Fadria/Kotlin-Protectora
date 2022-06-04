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
import com.feadca.protectora.utils.BLOG_ENTRIES
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BlogViewModel(application: Application) : AndroidViewModel(application) {
    val context = application

    // Variables LiveData que usaremos para las diferentes operaciones relacionadas con el blog
    val blogNewsLD: MutableLiveData<List<BlogEntry>> = MutableLiveData()
    val articleLD: MutableLiveData<BlogEntry?> = MutableLiveData()

    // Función para obtener las noticias del blog
    fun getArticles() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = BLOG_ENTRIES // URL de donde obtendremos las noticias y artículos del blog

            makeArticlesRequest(url) // Realizamos la petición
        }
    }

    // Funciónn usada para realizar la petición encargada de obtener los artículos del Blog
    private fun makeArticlesRequest(url: String) {
        // Cola con la que realizaremos la petición de gráficos
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val articlesRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {

                // Obtenemos el listado de artículos
                val articles = it.getJSONArray("data")

                // Listado de entradas del blog
                var entryList: MutableList<BlogEntry> = mutableListOf<BlogEntry>()

                // Bucle donde crearemos el array de artículos
                for (i in 0 until articles.length()) {
                    val article = articles.getJSONObject(i)

                    // Añadimos cada objeto al array con el listado
                    entryList.add(BlogEntry(article.getInt("id"), article.getString("titulo"),
                        article.getString("fechaPublicacion"),
                        article.getString("imagenPortada"), article.getString("autor"),
                        article.getString("contenido"), null))
                }

                // Una vez obtenemos todas las entradas, actualizamos nuestro LiveData
                blogNewsLD.postValue(entryList)
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

        queue.add(articlesRequest) // Añadimos la petición y la realizamos
    }

    // Función encargada de recibir los datos de un artículo
    fun loadArticle(articleId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val url = "$BLOG_ENTRIES/$articleId" // URL de donde obtendremos las noticias y artículos del blog

            makeArticleRequest(url, articleId) // Realizamos la petición
        }
    }

    // Función encargada de realizar la petición para obtener los datos de un artículo
    private fun makeArticleRequest(url: String, articleId: Int) {
        // Cola con la que realizaremos la petición
        val queue = Volley.newRequestQueue(context)

        // Variable que contendrá nuestra petición
        val articleRequest: JsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            Response.Listener {

                // Obtenemos el listado de artículos
                val articleJSON = it.getJSONArray("data").getJSONObject(0)

                val article:BlogEntry = BlogEntry(articleId,
                    articleJSON.getString("titulo"), articleJSON.getString("fechaPublicacion"),
                    articleJSON.getString("imagenPortada"), articleJSON.getString("autor"),
                    articleJSON.getString("contenido"), articleJSON.getString("imagenPie"))

                articleLD.postValue(article)
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

        queue.add(articleRequest) // Añadimos la petición y la realizamos
    }
}