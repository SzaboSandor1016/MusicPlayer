package com.example.features.playlists.presentation.fragment

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
import com.example.core.common.values.AUTO_PLAYLIST_IDS
import com.example.core.common.values.FAVORITES_NAME
import com.example.core.common.values.RECENT_NAME
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.SongOptionsDialogHelper
import com.example.core.ui.adapter.DefaultSongAdapter
import com.example.features.playlists.presentation.R
import com.example.features.playlists.presentation.databinding.FragmentSelectedPlaylistBinding
import com.example.features.playlists.presentation.mappers.toSongInfoUIModel
import com.example.features.playlists.presentation.models.PlaylistPlaylistsPresentationModel
import com.example.features.playlists.presentation.models.SelectedPlaylistPlaylistsPresentationModel
import com.example.features.playlists.presentation.models.SongPlaylistsPresentationModel
import com.example.features.playlists.presentation.viewmodel.ViewModelPlaylists
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSelectedPlaylist.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSelectedPlaylist : Fragment() {
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
         * @return A new instance of fragment FragmentSelectedPlaylist.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSelectedPlaylist().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    private val allPlaylists: ArrayList<PlaylistPlaylistsPresentationModel> = ArrayList()

    private val playlistSongs: ArrayList<SongPlaylistsPresentationModel> = ArrayList()

    private lateinit var adapterPlaylistSongs: DefaultSongAdapter

    private val viewModelPlaylists: ViewModelPlaylists by viewModel<ViewModelPlaylists>( ownerProducer = { requireActivity() })

    private var _binding: FragmentSelectedPlaylistBinding? = null
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

        _binding = FragmentSelectedPlaylistBinding.inflate(
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

        this.adapterPlaylistSongs = DefaultSongAdapter()

        binding.playlistSongsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.playlistSongsRecyclerView.adapter = this.adapterPlaylistSongs

        this.adapterPlaylistSongs.setOnClickListener { songId ->

            viewModelPlaylists.getSelectedPlaylistId()?.let { playlistId ->

                viewModelPlaylists.setMusicSource(
                    playlistId,
                    songId
                )
            }
        }

        this.adapterPlaylistSongs.setOnMoreOptionsClickListener { view, songId ->

            val options = mutableListOf<Pair<Int, () -> Unit>>()

            options.addAll(
                listOf(
                    com.example.core.ui.R.string.add_to_playlist to {

                        PlaylistDialogHelper.showPlaylistSelectDialog(
                            com.example.core.ui.R.string.add_to_playlist,
                            com.example.core.ui.R.string.choose_playlist,
                            requireContext(),
                            allPlaylists,
                            idSelector = { it.id },
                            labelSelector = { it.label },
                            onPlaylistSelected = {
                                viewModelPlaylists.addToPlaylist(
                                    playlistId = it,
                                    songId = songId
                                )
                            }
                        )
                    },
                    com.example.core.ui.R.string.remove_from_playlist to {

                        val song = playlistSongs.first { it.msId == songId }

                        updatePlaylistSongs(playlistSongs.minus(song))

                        viewModelPlaylists.removeFromPlaylist(
                            playlistId = song.playlistId,
                            songId = songId
                        )
                    },
                    com.example.core.ui.R.string.add_to_next_up to {

                        viewModelPlaylists.addToUpNext(songId)
                    },
                    com.example.core.ui.R.string.add_to_queue to  {

                        viewModelPlaylists.addToQueue(songId)
                    }
                )
            )

            SongOptionsDialogHelper.showSongOptionsDialog(
                requireContext(),
                view,
                options
            )
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlaylists.selectedPlaylistState.collect {

                    it?.let {

                        setSelectedPlaylistInfo(it)
                    }?: findNavController().navigate(R.id.action_fragmentSelectedPlaylist_to_fragmentPlaylists)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlaylists.allPlaylistsState.collect {

                    updateSelectablePlaylists(it)
                }
            }
        }

        binding.back.setOnClickListener { l ->

            viewModelPlaylists.unselectPlaylist()
        }

        binding.more.setOnClickListener { l ->

            l?.let { view ->

                viewModelPlaylists.getSelectedPlaylistId()?.let { playlistId ->

                    //TODO if more options are needed use this one below
                    /*val options = mutableListOf<Pair<Int, () -> Unit>>()

                    if (playlist.id !in AUTO_PLAYLIST_IDS) {
                        options.add(com.example.core.ui.R.string.delete_playlist to {
                            viewModelPlaylists.deletePlaylist(playlist.id)

                            viewModelPlaylists.unselectPlaylist()
                        })
                    }

                    SongOptionsDialogHelper.showSongOptionsDialog(
                        requireContext(),
                        view,
                        options = options
                    )*/

                    if (playlistId !in AUTO_PLAYLIST_IDS) {

                        SongOptionsDialogHelper.showSongOptionsDialog(
                            requireContext(),
                            view,
                            options = listOf(
                                com.example.core.ui.R.string.delete_playlist to {
                                    viewModelPlaylists.deletePlaylist(playlistId)

                                    //viewModelPlaylists.unselectPlaylist()
                                }
                            ),
                        )
                    }
                }
            }
        }
    }

    /*private fun setDefaultPlaylistInfo() {

        binding.playlistName.setText("")

        binding.songCount.setText("")

        updatePlaylistSongs(emptyList())
    }*/

    private fun setSelectedPlaylistInfo(playlist: PlaylistPlaylistsPresentationModel) {

        val label = when(playlist.label) {

            FAVORITES_NAME -> com.example.core.ui.R.string.favorites
            RECENT_NAME -> com.example.core.ui.R.string.recent
            else -> Integer.MIN_VALUE
        }

        if (label == Integer.MIN_VALUE) {

            binding.playlistName.setText(playlist.label)
        }else {

            binding.playlistName.setText(label)
        }

        binding.songCount.setText(playlist.songs.size.toString())

        updatePlaylistSongs(playlist.songs)
    }

    private fun updatePlaylistSongs(songs: List<SongPlaylistsPresentationModel>) {

        this.playlistSongs.clear()

        this.playlistSongs.addAll(songs)

        this.adapterPlaylistSongs.submitList(this.playlistSongs.map { it.toSongInfoUIModel() })
    }

    private fun updateSelectablePlaylists(playlists: List<PlaylistPlaylistsPresentationModel>) {

        this.allPlaylists.clear()

        this.allPlaylists.addAll(playlists.filter { it.id !in AUTO_PLAYLIST_IDS })
    }
}