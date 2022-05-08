package com.feadca.protectora.ui.content

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feadca.protectora.R
import com.feadca.protectora.adapters.GraphicAdapter
import com.feadca.protectora.databinding.FragmentGraphicsBinding
import com.feadca.protectora.model.Graphic
import com.feadca.protectora.viewmodel.GraphicsViewModel

class GraphicsFragment : Fragment(R.layout.fragment_graphics) {
    // Enlace con las vistas
    private var fragmentGraphicsBinding: FragmentGraphicsBinding? = null

    // Adaptador que usaremos en el RecyclerView de los gráficos
    private lateinit var adapter: GraphicAdapter

    // Variable que contiene la referencia al ViewModel
    private lateinit var graphicViewModel: GraphicsViewModel

    // Variable que contendrá nuestra lista de gráficos
    private val graphicList = mutableListOf<Graphic>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graphics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Indicamos el fichero que contiene el ViewModel
        graphicViewModel = ViewModelProvider(this)[GraphicsViewModel::class.java]

        // Actualizamos la vinculación a la vista
        val binding = FragmentGraphicsBinding.bind(view)
        fragmentGraphicsBinding = binding

        // Llamamos a la función que prepara nuestro RecyclerView
        initRecyclerView()

        // Llamamos a la función del viewmodel encargada de obtener los gráficos
        graphicViewModel.getGraphics()

        // Observamos el LiveData de los gráficos para actualizar el listado
        graphicViewModel.graphicsLD.observe(viewLifecycleOwner) {
            graphicList.clear() // Vaciamos la lista
            graphicList.addAll(it) // Añadimos todos los elementos del live data
            adapter?.notifyDataSetChanged() // Notificamos de cambios para cargar de nuevo el RecyclerView
        }
    }

    // Función encargada de inicializar nuestro RecyclerView
    private fun initRecyclerView() {
        adapter = GraphicAdapter(graphicList)
        fragmentGraphicsBinding!!.rvGraphics.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        fragmentGraphicsBinding!!.rvGraphics.adapter = adapter
    }
}