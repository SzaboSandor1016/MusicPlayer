package com.example.features.artists.presentation.fragment

import android.os.Bundle
import android.util.Log
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
import com.example.features.artists.presentation.R
import com.example.features.artists.presentation.databinding.FragmentArtistsBinding
import com.example.features.artists.presentation.mappers.toGridItem
import com.example.features.artists.presentation.model.ArtistArtistsPresentationModel
import com.example.features.artists.presentation.viewmodel.ViewModelArtists
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentArtists.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentArtists : Fragment() {
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
         * @return A new instance of fragment FragmentArtists.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentArtists().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }
    private val gridSpanCount = 2

    private val oneColumn = gridSpanCount / 1

    private  val twoColumns = gridSpanCount / 2

    private val artists = ArrayList<GridItem>()

    private lateinit var adapterArtists: AdapterDefaultGridRecyclerView

    private val viewModelArtists: ViewModelArtists by viewModel<ViewModelArtists>(ownerProducer = { requireActivity()})
    private var _binding: FragmentArtistsBinding? = null

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

        _binding = FragmentArtistsBinding.inflate(
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

        this.adapterArtists = AdapterDefaultGridRecyclerView()

        val layoutManager = GridLayoutManager(
            requireContext(),
            gridSpanCount,
            GridLayoutManager.VERTICAL,
            false
        )

        layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                return when(adapterArtists.getItemList().elementAtOrNull(position)){
                    is GridItem.PlaylistHeader -> oneColumn
                    is GridItem.Item -> twoColumns
                    is GridItem.SongHeader -> oneColumn
                    is GridItem.SongItem -> oneColumn
                    null -> oneColumn
                }
            }
        }

        binding.artists.layoutManager = layoutManager

        binding.artists.adapter = this.adapterArtists

        this.adapterArtists.notifyList(this.artists)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelArtists.allArtistsState.collect {

                    updateArtists(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelArtists.selectedArtistState.collect {

                    Log.d("selected_artist", "non_null")

                    if (it != null) {
                        Log.d("selected_artist_if", "non_null")
                        findNavController().navigate(R.id.action_fragmentArtists_to_fragmentSelectedArtist)
                    }
                }
            }
        }
    }

    private fun updateArtists(artists: List<ArtistArtistsPresentationModel>) {

        this.artists.clear()

        this.artists.addAll(
            artists.map { artist ->

                artist.toGridItem(
                    action = {

                        viewModelArtists.setSelectedArtistId(it)
                        Log.d("selected_artist", "non_null ${it}")
                    }
                )
            }
        )

        this.adapterArtists.notifyList(this.artists.toList())

        this.adapterArtists.notifyDataSetChanged()
    }
}