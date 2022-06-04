package com.feadca.protectora.ui.content

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.feadca.protectora.R
import com.feadca.protectora.databinding.FragmentArticleBinding
import com.feadca.protectora.databinding.FragmentBlogBinding
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.viewmodel.BlogViewModel

// Fragmento que contendrá un artículo del blog
class ArticleFragment : Fragment(R.layout.fragment_article) {
    // Variable que contendrá la id del artículo, se aplica el valor 0 para definir la variable
    var articleId: Int = 0

    // Enlace con las vistas
    private var fragmentArticleBinding: FragmentArticleBinding? = null

    // Variable que contiene la referencia al ViewModel
    private lateinit var blogViewModel: BlogViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflamos el layout del fragmento
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Actualizamos el valor de la id del artículo
        val arguments = arguments
        articleId = arguments!!.getInt("articleId")

        // Indicamos el fichero que contiene el ViewModel
        blogViewModel = ViewModelProvider(this)[BlogViewModel::class.java]

        // Actualizamos la vinculación a la vista
        val binding = FragmentArticleBinding.bind(view)
        fragmentArticleBinding = binding

        // Llamamos a la función encargada de obtener los datos del artículo
        blogViewModel.loadArticle(articleId)

        // Observamos el LD del artículo para poder mostrar sus datos una vez sea cargado
        blogViewModel.articleLD.observe(viewLifecycleOwner) {
            binding.tvTitle.text = it!!.title
            binding.tvContent.text = it!!.content
            binding.tvDate.text = it!!.date
            binding.tvAuthor.text = it!!.author

            // Cargamos las imágenes del artículo
            Glide.with(requireContext())
                .load(it.image) // Imagen a mostrar
                .placeholder(AppCompatResources.getDrawable(requireContext(), R.drawable.loading)) // Imagen mostrada durante la carga
                .error(AppCompatResources.getDrawable(requireContext(), R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                .into(binding.iwImage) // Indicamos donde serán colocadas las imágenes en la vista

            Glide.with(requireContext())
                .load(it.imageFoot) // Imagen a mostrar
                .placeholder(AppCompatResources.getDrawable(requireContext(), R.drawable.loading)) // Imagen mostrada durante la carga
                .error(AppCompatResources.getDrawable(requireContext(), R.drawable.logo)) // Imagen mostrada en el caso de no poder cargarla
                .into(binding.iwImage2) // Indicamos donde serán colocadas las imágenes en la vista
        }
    }
}