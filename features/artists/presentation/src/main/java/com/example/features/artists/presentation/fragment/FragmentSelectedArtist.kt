package com.example.features.artists.presentation.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.SongOptionsDialogHelper
import com.example.core.ui.grid.adapter.AdapterDefaultGridRecyclerView
import com.example.core.ui.grid.model.GridItem
import com.example.features.artists.presentation.R
import com.example.features.artists.presentation.databinding.FragmentSelectedArtistBinding
import com.example.features.artists.presentation.mappers.toGridItem
import com.example.features.artists.presentation.model.AlbumArtistsPresentationModel
import com.example.features.artists.presentation.model.ArtistArtistsPresentationModel
import com.example.features.artists.presentation.model.PlaylistInfoArtistsPresentationModel
import com.example.features.artists.presentation.model.SongArtistsPresentationModel
import com.example.features.artists.presentation.viewmodel.ViewModelArtists
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSelectedArtist.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSelectedArtist : Fragment() {
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
         * @return A new instance of fragment FragmentSelectedArtist.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSelectedArtist().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }
    private val playlists: ArrayList<PlaylistInfoArtistsPresentationModel> = ArrayList()

    private val gridSpanCount = 2

    private val oneColumn = gridSpanCount / 1

    private  val twoColumns = gridSpanCount / 2

    private val albums = ArrayList<GridItem>()

    private lateinit var adapterArtistAlbums: AdapterDefaultGridRecyclerView

    private val viewModelArtists: ViewModelArtists by viewModel<ViewModelArtists>(ownerProducer = { requireActivity()})

    private var _binding: FragmentSelectedArtistBinding? = null

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

        _binding = FragmentSelectedArtistBinding.inflate(
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

        this.adapterArtistAlbums = AdapterDefaultGridRecyclerView()

        val layoutManager = GridLayoutManager(
            requireContext(),
            gridSpanCount,
            GridLayoutManager.VERTICAL,
            false
        )

        layoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {

                return when(adapterArtistAlbums.getItemList().elementAtOrNull(position)){
                    is GridItem.PlaylistHeader -> oneColumn
                    is GridItem.Item -> twoColumns
                    is GridItem.SongHeader -> oneColumn
                    is GridItem.SongItem -> oneColumn
                    null -> oneColumn
                }
            }
        }

        binding.albums.layoutManager = layoutManager

        binding.albums.adapter = this.adapterArtistAlbums

        this.adapterArtistAlbums.notifyList(this.albums)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelArtists.selectedArtistState.collect { artist ->

                    artist?.let {

                        Log.d("selected_artist_fragment", it.albums.size.toString())

                        assembleList(it)
                    }?: findNavController().navigate(R.id.action_fragmentSelectedArtist_to_fragmentArtists)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelArtists.selectedAlbumState.collect {

                    it?.let {

                        findNavController().navigate(R.id.action_fragmentSelectedArtist_to_fragmentSelectedArtistAlbum)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelArtists.playlistsInfoState.collect {

                    updatePlaylistInfo(it)
                }
            }
        }

        binding.back.setOnClickListener { l ->

            viewModelArtists.setSelectedArtistId(null)
        }
    }

    private fun assembleList(artist: ArtistArtistsPresentationModel) {

        val gridList = mutableListOf<GridItem>()

        gridList.addAll(
            assembleArtistAlbums(artist.albums)
        )

        gridList.addAll(assembleArtistSongs(artist.songs))

        this.albums.clear()

        this.albums.addAll(gridList)

        this.adapterArtistAlbums.notifyList(this.albums.toList())

        this.adapterArtistAlbums.notifyDataSetChanged()
    }

    private fun assembleArtistSongs(songs: List<SongArtistsPresentationModel>): List<GridItem> {


        return mutableListOf(
            GridItem.SongHeader(
                action = null,
                titleId = com.example.core.ui.R.string.artist_songs,
                actionId = null
            )
        ).plus(songs.map { song ->

            song.toGridItem(
                action = {

                    viewModelArtists.setMusicSourceWithArtistSongs(it)
                },
                actionAll = { song, view ->

                    val options = mutableListOf<Pair<Int, () -> Unit>>()

                    options.addAll(
                        listOf(
                            com.example.core.ui.R.string.add_to_playlist to {

                                PlaylistDialogHelper.showPlaylistSelectDialog(
                                    com.example.core.ui.R.string.add_to_playlist,
                                    com.example.core.ui.R.string.choose_playlist,
                                    requireContext(),
                                    playlists,
                                    idSelector = { it.id },
                                    labelSelector = { it.label },
                                    onPlaylistSelected = {
                                        viewModelArtists.addToPlaylist(
                                            playlistId = it,
                                            songId = song
                                        )
                                    }
                                )
                            },
                            com.example.core.ui.R.string.add_to_next_up to {

                                viewModelArtists.addToUpNext(song)
                            },
                            com.example.core.ui.R.string.add_to_queue to {

                                viewModelArtists.addToQueue(song)
                            }
                        )
                    )

                    SongOptionsDialogHelper.showSongOptionsDialog(
                        requireContext(),
                        anchorView = view,
                        options = options,
                    )
                }
            )
        })
    }

    private fun assembleArtistAlbums(artists: List<AlbumArtistsPresentationModel>): List<GridItem> {


        return mutableListOf(
            GridItem.PlaylistHeader(
                action = null,
                titleId = com.example.core.ui.R.string.albums,
                actionId = null
            )
        ).plus(artists.map { artist ->

            artist.toGridItem(
                action = {

                    viewModelArtists.setSelectedAlbumId(it)
                },
                actionAll = {

                    viewModelArtists.setMusicSource(
                        it
                    )
                }
            )
        })
    }

    private fun updatePlaylistInfo(playlists: List<PlaylistInfoArtistsPresentationModel>) {

        this.playlists.clear()

        this.playlists.addAll(playlists)
    }
}