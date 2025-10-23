package com.example.features.artists.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.SongOptionsDialogHelper
import com.example.core.ui.adapter.DefaultSongAdapter
import com.example.core.ui.model.SongInfoUIModel
import com.example.features.artists.presentation.R
import com.example.features.artists.presentation.databinding.FragmentSelectedArtistAlbumBinding
import com.example.features.artists.presentation.mappers.toSongInfoUIModel
import com.example.features.artists.presentation.model.AlbumArtistsPresentationModel
import com.example.features.artists.presentation.model.PlaylistInfoArtistsPresentationModel
import com.example.features.artists.presentation.model.SongArtistsPresentationModel
import com.example.features.artists.presentation.viewmodel.ViewModelArtists
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSelectedArtistAlbum.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSelectedArtistAlbum : Fragment() {
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
         * @return A new instance of fragment FragmentSelectedArtistAlbum.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSelectedArtistAlbum().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }
    private val playlists: ArrayList<PlaylistInfoArtistsPresentationModel> = ArrayList()

    private val albumSongs = ArrayList<SongInfoUIModel>()

    private lateinit var adapterAlbumSongs: DefaultSongAdapter

    private val viewModelArtists: ViewModelArtists by viewModel<ViewModelArtists>(ownerProducer = { requireActivity()})

    private var _binding: FragmentSelectedArtistAlbumBinding? = null

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

        _binding = FragmentSelectedArtistAlbumBinding.inflate(
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

        adapterAlbumSongs = DefaultSongAdapter()

        binding.albumSongs.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.albumSongs.adapter = adapterAlbumSongs

        adapterAlbumSongs.setOnClickListener { songId ->

            viewModelArtists.getSelectedAlbumId()?.let { albumId ->

                viewModelArtists.setArtistAlbumMusicSource(
                    albumId,
                    songId
                )
            }
        }

        adapterAlbumSongs.setOnMoreOptionsClickListener { view, songId ->

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
                                    songId = songId
                                )
                            }
                        )
                    },
                    com.example.core.ui.R.string.add_to_next_up to {

                        viewModelArtists.addToUpNext(songId)
                    },
                    com.example.core.ui.R.string.add_to_queue to {

                        viewModelArtists.addToQueue(songId)
                    }
                )
            )

            SongOptionsDialogHelper.showSongOptionsDialog(
                requireContext(),
                view,
                options
            )
        }

        adapterAlbumSongs.submitList(this.albumSongs)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelArtists.selectedAlbumState.collect {

                    it?.let {

                        updateAlbumUIElements(it)
                    }?: findNavController().navigate(R.id.action_fragmentSelectedArtistAlbum_to_fragmentSelectedArtist)
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

            viewModelArtists.setSelectedAlbumId(null)
        }
    }

    private fun updateAlbumUIElements(album: AlbumArtistsPresentationModel) {

        updateAlbumInfo(
            albumName = album.name,
            songCount = album.songs.size
        )

        updateAlbumSongsList(album.songs)
    }

    private fun updateAlbumInfo(albumName: String, songCount: Int) {

        binding.albumName.text = albumName

        binding.songCount.text = songCount.toString()
    }

    private fun updateAlbumSongsList(songs: List<SongArtistsPresentationModel>) {

        this.albumSongs.clear()

        this.albumSongs.addAll(
            songs.map { it.toSongInfoUIModel() }
        )

        this.adapterAlbumSongs.submitList(this.albumSongs)

        this.adapterAlbumSongs.notifyDataSetChanged()
    }

    private fun updatePlaylistInfo(playlists: List<PlaylistInfoArtistsPresentationModel>) {

        this.playlists.clear()

        this.playlists.addAll(playlists)
    }
}