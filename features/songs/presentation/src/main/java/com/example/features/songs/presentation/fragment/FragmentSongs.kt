package com.example.features.songs.presentation.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.SongOptionsDialogHelper
import com.example.core.ui.adapter.DefaultSongAdapter
import com.example.features.songs.presentation.databinding.FragmentSongsBinding
import com.example.features.songs.presentation.mappers.toSongInfoUIModel
import com.example.features.songs.presentation.models.PlaylistInfoSongsPresentationModel
import com.example.features.songs.presentation.models.SongSongsPresentationModel
import com.example.features.songs.presentation.vievmodel.ViewModelSongs
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentSongs.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSongs : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    interface OnPlaylistSelected {
        fun onSelect(playlistId: Long)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentSongs.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSongs().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    private lateinit var adapterSongs: DefaultSongAdapter

    private val songs: ArrayList<SongSongsPresentationModel> = ArrayList()

    private val playlists: ArrayList<PlaylistInfoSongsPresentationModel> = ArrayList()

    private var playlistDialog: AlertDialog? = null

    private val viewModelSongs: ViewModelSongs by viewModel<ViewModelSongs>()

    private var _binding: FragmentSongsBinding? = null

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

        _binding = FragmentSongsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.adapterSongs = DefaultSongAdapter()

        binding.songsRecyclerView.layoutManager = LinearLayoutManager(
            this.context,
            LinearLayoutManager.VERTICAL,
            false)

        binding.songsRecyclerView.adapter = this.adapterSongs

        this.adapterSongs.setOnClickListener { songId ->

            viewModelSongs.setMusicSource(
                songId
            )
        }

        this.adapterSongs.setOnMoreOptionsClickListener { view, songId ->

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
                                viewModelSongs.addToPlaylist(
                                    playlistId = it,
                                    songId = songId
                                )
                            }
                        )
                    },
                    com.example.core.ui.R.string.add_to_next_up to {

                        viewModelSongs.addToUpNext(songId)
                    },
                    com.example.core.ui.R.string.add_to_queue to {

                        viewModelSongs.addToQueue(songId)
                    }
                )
            )

            SongOptionsDialogHelper.showSongOptionsDialog(
                requireContext(),
                view,
                options
            )
        }

        viewModelSongs.syncRoomWithMediaStore()

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSongs.songsState.collect {

                    updateAllSongs(
                        songs = it
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSongs.playlistsInfoState.collect {

                    updatePlaylistInfo(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelSongs.errorSharedFlow.collect {

                    showErrorToast(it)
                }
            }
        }

        binding.searchSong.addTextChangedListener(object: TextWatcher {

            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {
                viewModelSongs.setSearchExpression(p0.toString())
            }

        })
    }

    private fun updateAllSongs(songs: List<SongSongsPresentationModel>) {

        this.songs.clear()

        this.songs.addAll(songs)

        this.adapterSongs.submitList(this.songs.map { it.toSongInfoUIModel() })
    }

    private fun updatePlaylistInfo(playlists: List<PlaylistInfoSongsPresentationModel>) {

        this.playlists.clear()

        this.playlists.addAll(playlists)
    }

    fun showErrorToast(errorCode: Int) {

        val message = when(errorCode) {

            0 -> com.example.core.ui.R.string.song_already_contained
            else -> com.example.core.ui.R.string.nothing
        }

        Snackbar.make(binding.root,message, Snackbar.LENGTH_LONG).show()
    }

    /*fun showPlaylistSelectDialog(title: Int, message: Int, playlists: List<PlaylistInfoSongsPresentationModel>, onPlaylistSelected: OnPlaylistSelected) {

        val view = LayoutSelectPlaylistDialogBinding
            .inflate(LayoutInflater.from(requireContext()))

        //val playlistName: TextInputEditText = view.findViewById(com.example.features.playlists.presentation.R.id.new_playlist_name)

        val adapterPlaylists = AdapterSelectPlaylistRecyclerView(playlists)

        view.selectPlaylistRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        view.selectPlaylistRecyclerView.adapter = adapterPlaylists

        adapterPlaylists.setOnItemClickListener(object : AdapterSelectPlaylistRecyclerView.OnItemClickListener {

            override fun onClick(playlistId: Long) {

                onPlaylistSelected.onSelect(playlistId)

                dismissDialog()
            }
        })

        playlistDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setView(view.root)
            .setNegativeButton(resources.getString(com.example.core.ui.R.string.cancel)) { dialog, which ->

                dismissDialog()
            }
            .show()
    }

    fun dismissDialog() {

        playlistDialog?.dismiss()

        playlistDialog = null
    }*/
}