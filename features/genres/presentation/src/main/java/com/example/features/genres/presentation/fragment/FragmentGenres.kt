package com.example.features.genres.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.core.ui.grid.adapter.AdapterDefaultGridRecyclerView
import com.example.core.ui.grid.model.GridItem
import com.example.features.genres.presentation.R
import com.example.features.genres.presentation.databinding.FragmentGenresBinding
import com.example.features.genres.presentation.mappers.toGridItem
import com.example.features.genres.presentation.model.GenreGenresPresentationModel
import com.example.features.genres.presentation.viewmodel.ViewModelGenres
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentGenres.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentGenres : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentGenres.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentGenres().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }
    private val gridSpanCount = 2

    private val oneColumn = gridSpanCount / 1

    private val twoColumns = gridSpanCount / 2

    private val albums = ArrayList<GridItem>()

    private lateinit var adapterGenres: AdapterDefaultGridRecyclerView

    private val viewModelGenres: ViewModelGenres by viewModel<ViewModelGenres>(ownerProducer = { requireActivity()})
    private var _binding: FragmentGenresBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGenresBinding.inflate(
            inflater,
            container,
            false
        )
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapterGenres = AdapterDefaultGridRecyclerView()

        val layoutManager = GridLayoutManager(
            requireContext(),
            gridSpanCount,
            GridLayoutManager.VERTICAL,
            false
        )

        layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {

            override fun getSpanSize(position: Int): Int {

                return when(adapterGenres.getItemList().getOrNull(position)) {
                    is GridItem.PlaylistHeader -> oneColumn
                    is GridItem.Item -> twoColumns
                    is GridItem.SongHeader -> oneColumn
                    is GridItem.SongItem -> oneColumn
                    null -> gridSpanCount
                }
            }
        }

        binding.genres.layoutManager = layoutManager

        binding.genres.adapter = this.adapterGenres

        this.adapterGenres.notifyList(this.albums)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelGenres.allGenresState.collect {

                    updateGenreList(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelGenres.selectedGenreState.collect {

                    it?.let {

                        findNavController().navigate(R.id.action_fragmentGenres_to_fragmentSelectedGenre)
                    }
                }
            }
        }
    }

    private fun updateGenreList(albums: List<GenreGenresPresentationModel>) {

        this.albums.clear()

        this.albums.addAll(
            albums.map {
                it.toGridItem(
                    action = { albumId ->
                        viewModelGenres.setSelectedGenreId(albumId)
                    },
                    actionAll = { albumId ->

                        viewModelGenres.setMusicSource(
                            albumId
                        )
                    }
                )
            }
        )

        this.adapterGenres.notifyList(this.albums.toList())

        this.adapterGenres.notifyDataSetChanged()
    }
}