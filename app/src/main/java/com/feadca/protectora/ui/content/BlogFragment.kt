package com.feadca.protectora.ui.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feadca.protectora.R
import com.feadca.protectora.adapters.BlogEntryAdapter
import com.feadca.protectora.databinding.FragmentBlogBinding
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.viewmodel.BlogViewModel

class BlogFragment : Fragment(R.layout.fragment_blog) {
    // Enlace con las vistas
    private var fragmentBlogBinding: FragmentBlogBinding? = null

    // Adaptador que usaremos en el RecyclerView de las entradas del blog
    private lateinit var adapter:BlogEntryAdapter

    // Variable que contiene la referencia al ViewModel
    private lateinit var blogEntryViewModel: BlogViewModel

    // Variable que contendrá nuestra lista de entradas
    private val blogEntryList = mutableListOf<BlogEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        blogEntryViewModel = ViewModelProvider(this)[BlogViewModel::class.java]

        // Actualizamos la vinculación a la vista
        val binding = FragmentBlogBinding.bind(view)
        fragmentBlogBinding = binding

        // Llamamos a la función que prepara nuestro RecyclerView
        initRecyclerView()

        // Llamamos a la función del viewmodel encargada de obtener los artículos del blog
        blogEntryViewModel.getArticles()

        blogEntryViewModel.blogNewsLD.observe(viewLifecycleOwner) {
            blogEntryList.clear() // Vaciamos la lista
            blogEntryList.addAll(it) // Añadimos todos los elementos del live data
            adapter?.notifyDataSetChanged() // Notificamos de cambios para cargar de nuevo el RecyclerView
        }
    }

    // Función encargada de inicializar nuestro RecyclerView
    private fun initRecyclerView() {
        adapter = BlogEntryAdapter(blogEntryList)
        fragmentBlogBinding!!.rvBlog.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        fragmentBlogBinding!!.rvBlog.adapter = adapter
    }
}