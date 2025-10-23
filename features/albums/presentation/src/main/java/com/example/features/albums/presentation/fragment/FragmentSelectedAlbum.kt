package com.example.features.albums.presentation.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.common.values.DEFAULT_ALBUM_NAMES
import com.example.core.common.values.DEFAULT_ARTIST_NAMES
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.SongOptionsDialogHelper
import com.example.core.ui.adapter.DefaultSongAdapter
import com.example.core.ui.model.SongInfoUIModel
import com.example.features.albums.presentation.R
import com.example.features.albums.presentation.databinding.FragmentSelectedAlbumBinding
import com.example.features.albums.presentation.mappers.toSongInfoUIModel
import com.example.features.albums.presentation.model.AlbumAlbumsPresentationModel
import com.example.features.albums.presentation.model.PlaylistInfoAlbumsPresentationModel
import com.example.features.albums.presentation.model.SongAlbumsPresentationModel
import com.example.features.albums.presentation.viewmodel.ViewModelAlbums
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSelectedAlbum.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSelectedAlbum : Fragment() {
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
         * @return A new instance of fragment FragmentSelectedAlbum.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSelectedAlbum().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    private val playlists: ArrayList<PlaylistInfoAlbumsPresentationModel> = ArrayList()

    private val albumSongs = ArrayList<SongInfoUIModel>()

    private lateinit var adapterAlbumSongs: DefaultSongAdapter

    private val viewModelAlbums: ViewModelAlbums by viewModel<ViewModelAlbums>(ownerProducer = { requireActivity()})

    private var _binding: FragmentSelectedAlbumBinding? = null

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

        _binding = FragmentSelectedAlbumBinding.inflate(
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

            viewModelAlbums.getSelectedId()?.let { albumId ->

                viewModelAlbums.setMusicSource(
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
                                viewModelAlbums.addToPlaylist(
                                    playlistId = it,
                                    songId = songId
                                )
                            }
                        )
                    },
                    com.example.core.ui.R.string.add_to_next_up to {

                        viewModelAlbums.addToUpNext(songId)
                    },
                    com.example.core.ui.R.string.add_to_queue to {

                        viewModelAlbums.addToQueue(songId)
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

                viewModelAlbums.selectedAlbumState.collect {

                    it?.let {

                        updateAlbumUIElements(it)
                    }?: findNavController().navigate(R.id.action_fragmentSelectedAlbum_to_fragmentAlbums)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelAlbums.playlistsInfoState.collect {

                    updatePlaylistInfo(it)
                }
            }
        }

        binding.back.setOnClickListener { l ->

            viewModelAlbums.setSelectedId(null)
        }
    }

    private fun updateAlbumUIElements(album: AlbumAlbumsPresentationModel) {

        updateAlbumInfo(
            albumName = album.name,
            songCount = album.songs.size
        )

        updateAlbumSongsList(album.songs)
    }

    private fun updateAlbumInfo(albumName: String, songCount: Int) {

        if (albumName !in DEFAULT_ALBUM_NAMES) {
            binding.albumName.setText(
                albumName
            )
        } else {
            binding.albumName.setText(
                com.example.core.ui.R.string.unknown_album
            )
        }

        binding.songCount.text = songCount.toString()
    }

    private fun updateAlbumSongsList(songs: List<SongAlbumsPresentationModel>) {

        this.albumSongs.clear()

        this.albumSongs.addAll(
            songs.map { it.toSongInfoUIModel() }
        )

        this.adapterAlbumSongs.submitList(this.albumSongs)

        this.adapterAlbumSongs.notifyDataSetChanged()
    }

    private fun updatePlaylistInfo(playlists: List<PlaylistInfoAlbumsPresentationModel>) {

        this.playlists.clear()

        this.playlists.addAll(playlists)
    }
}