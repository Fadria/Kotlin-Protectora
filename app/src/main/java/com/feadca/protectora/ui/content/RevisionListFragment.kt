package com.feadca.protectora.ui.content

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.feadca.protectora.R
import com.feadca.protectora.adapters.BlogEntryAdapter
import com.feadca.protectora.adapters.RevisionAdapter
import com.feadca.protectora.databinding.FragmentArticleBinding
import com.feadca.protectora.databinding.FragmentBlogBinding
import com.feadca.protectora.databinding.FragmentRevisionListBinding
import com.feadca.protectora.model.BlogEntry
import com.feadca.protectora.model.Revision

class RevisionListFragment : Fragment(R.layout.fragment_revision_list) {
    // Enlace con las vistas
    private var fragmentRevisionListBinding: FragmentRevisionListBinding? = null

    // Adaptador que usaremos en el RecyclerView de las revisiones
    private lateinit var adapter: RevisionAdapter

    // Variable que contendrá nuestra lista de revisiones
    private var revisionList = mutableListOf<Revision>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_revision_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Actualizamos la vinculación a la vista
        val binding = FragmentRevisionListBinding.bind(view)
        fragmentRevisionListBinding = binding

        // Llamamos a la función que prepara nuestro RecyclerView
        initRecyclerView()

        // Obtenemos el listado de revisiones del animal
        val arguments = arguments
        val revisions: Array<out Revision>? = arguments!!.getParcelableArray("revisionList") as Array<out Revision>?

        revisionList.clear() // Vaciamos la lista
        revisionList.addAll(revisions!!.toMutableList()) // Añadimos todos los elementos
        adapter?.notifyDataSetChanged() // Notificamos de cambios para cargar de nuevo el RecyclerView
    }

    // Función encargada de inicializar nuestro RecyclerView
    private fun initRecyclerView() {
        adapter = RevisionAdapter(revisionList)
        fragmentRevisionListBinding!!.rvRevision.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        fragmentRevisionListBinding!!.rvRevision.adapter = adapter
    }
}