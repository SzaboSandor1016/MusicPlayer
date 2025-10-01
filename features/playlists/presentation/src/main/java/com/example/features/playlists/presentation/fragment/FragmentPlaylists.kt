package com.example.features.playlists.presentation.fragment

import android.content.Context
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.common.values.AUTO_PLAYLIST_IDS
import com.example.core.common.values.FAVORITES_ID
import com.example.core.common.values.FAVORITES_NAME
import com.example.core.common.values.RECENT_ID
import com.example.core.common.values.RECENT_NAME
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.SongOptionsDialogHelper
import com.example.core.ui.adapter.DefaultSongAdapter
import com.example.core.ui.grid.adapter.AdapterDefaultGridRecyclerView
import com.example.core.ui.grid.model.GridItem
import com.example.features.playlists.presentation.R
import com.example.features.playlists.presentation.databinding.FragmentPlaylistsBinding
import com.example.features.playlists.presentation.mappers.toGridItem
import com.example.features.playlists.presentation.mappers.toSongInfoUIModel
import com.example.features.playlists.presentation.models.PlaylistPlaylistsPresentationModel
import com.example.features.playlists.presentation.models.SelectedPlaylistPlaylistsPresentationModel
import com.example.features.playlists.presentation.models.SongPlaylistsPresentationModel
import com.example.features.playlists.presentation.viewmodel.ViewModelPlaylists
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPlaylists.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPlaylists : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    interface OnPlaylistCreated {
        fun onCreated(playlistName: String)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentPlaylists.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentPlaylists().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    val gridSpanCount = 2

    val oneColumn = gridSpanCount / 1

    val twoColumn = gridSpanCount / 2

    private var playlistDialog: AlertDialog? = null

    private val playlistSongs: ArrayList<SongPlaylistsPresentationModel> = ArrayList()

    private lateinit var adapterPlaylistSongs: DefaultSongAdapter

    private val allPlaylistsGrid: ArrayList<GridItem> = ArrayList()

    private lateinit var adapterAllPlaylists: AdapterDefaultGridRecyclerView

    private val allPlaylists: ArrayList<PlaylistPlaylistsPresentationModel> = ArrayList()

    /*private lateinit var adapterAutoPlaylists: AdapterPlaylistsRecyclerView*/

    private var selectedPlaylist: SelectedPlaylistPlaylistsPresentationModel =
        SelectedPlaylistPlaylistsPresentationModel.Default

    private val viewModelPlaylists: ViewModelPlaylists by inject<ViewModelPlaylists>()

    private var _binding: FragmentPlaylistsBinding? = null

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

        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        this.adapterAllPlaylists = AdapterDefaultGridRecyclerView()

        this.adapterAllPlaylists.notifyList(allPlaylistsGrid)

        val layoutManager = GridLayoutManager(
            requireContext(),
            gridSpanCount,
            LinearLayoutManager.VERTICAL,
            false
        )

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when(adapterAllPlaylists.getItemList().getOrNull(position)) {
                    is GridItem.Header -> oneColumn
                    is GridItem.Item -> twoColumn
                    null -> oneColumn
                }
            }

        }

        binding.playlistsRecyclerView.layoutManager = layoutManager

        binding.playlistsRecyclerView.adapter = this.adapterAllPlaylists

        /*this.adapterAllPlaylists.setOnClickListener(object: AdapterPlaylistsRecyclerView.OnClickListener {

            override fun onClick(playlist: PlaylistPlaylistsPresentationModel) {

                viewModelPlaylists.selectPlaylist(
                    playlist
                )
            }
        })*/

        /*this.adapterAutoPlaylists = AdapterPlaylistsRecyclerView(
            this.autoPlaylists
        )

        binding.autoPlaylistsRecyclerView.layoutManager = GridLayoutManager(
            requireContext(),
            2,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.autoPlaylistsRecyclerView.adapter = this.adapterAutoPlaylists

        this.adapterAutoPlaylists.setOnClickListener(object: AdapterPlaylistsRecyclerView.OnClickListener {

            override fun onClick(playlist: PlaylistPlaylistsPresentationModel) {

                viewModelPlaylists.selectPlaylist(
                    playlist
                )
            }
        })*/

        this.adapterPlaylistSongs = DefaultSongAdapter()

        binding.playlistSongsRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.playlistSongsRecyclerView.adapter = this.adapterPlaylistSongs

        this.adapterPlaylistSongs.setOnClickListener { songId ->

            (selectedPlaylist as? SelectedPlaylistPlaylistsPresentationModel.Selected)?.playlist?.let { playlist ->

                val song = playlist.songs.first { it.id == songId }

                val index = playlistSongs.indexOf(song)

                viewModelPlaylists.setMusicSource(
                    playlist.id,
                    index
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

                        val song = playlistSongs.first { it.id == songId }

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

                viewModelPlaylists.allPlaylistsState.collect {

                    updatePlaylists(it)

                    updateSelectablePlaylists(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlaylists.selectedPlaylistState.collect {

                    selectedPlaylist = it

                    when(it) {

                        is SelectedPlaylistPlaylistsPresentationModel.Default ->  {

                            Log.d("playlist_fragment", "not selected")

                            handlePlaylistSelect(false)

                            setDefaultPlaylistInfo()
                        }
                        is SelectedPlaylistPlaylistsPresentationModel.Selected -> {

                            Log.d("playlist_fragment", "selected")

                            handlePlaylistSelect(true)

                            setSelectedPlaylistInfo(
                                it.playlist
                            )
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlaylists.errorSharedFlow.collect {

                    showErrorToast(it)
                }
            }
        }

        binding.back.setOnClickListener { l ->

            viewModelPlaylists.unselectPlaylist()
        }

        binding.more.setOnClickListener { l ->

            l?.let { view ->

                (selectedPlaylist as? SelectedPlaylistPlaylistsPresentationModel.Selected)?.playlist?.let { playlist ->

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

                    if (playlist.id !in AUTO_PLAYLIST_IDS) {

                        SongOptionsDialogHelper.showSongOptionsDialog(
                            requireContext(),
                            view,
                            options = listOf(
                                com.example.core.ui.R.string.delete_playlist to {
                                    viewModelPlaylists.deletePlaylist(playlist.id)

                                    viewModelPlaylists.unselectPlaylist()
                                }
                            ),
                        )
                    }
                }
            }
        }
    }

    fun showNewPlaylistDialog(title: Int,message: Int, onPlaylistCreated: OnPlaylistCreated) {

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.layout_add_playlist, null, false)

        val playlistName: TextInputEditText = view!!.findViewById(R.id.new_playlist_name)

        playlistDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setView(view)
            .setNegativeButton(resources.getString(com.example.core.ui.R.string.cancel)) { dialog, which ->

                dismissDialog()
            }
            .setPositiveButton(resources.getString(com.example.core.ui.R.string.ok)) { dialog, which ->

                val name = playlistName.getText().toString().trim()

                onPlaylistCreated.onCreated(name)
            }
            .show()
    }

    fun dismissDialog() {

        playlistDialog?.dismiss()

        playlistDialog = null
    }

    /*private fun updateAllPlaylists(playlists: List<PlaylistPlaylistsPresentationModel>) {

        this.allPlaylistsGrid.clear()

        this.allPlaylistsGrid.addAll(
            playlists
        )

        this.adapterAllPlaylists.notifyDataSetChanged()
    }*/

    private fun updateSelectablePlaylists(playlists: List<PlaylistPlaylistsPresentationModel>) {

        this.allPlaylists.clear()

        this.allPlaylists.addAll(playlists.filter { it.id !in AUTO_PLAYLIST_IDS })
    }

    private fun updatePlaylists(playlists: List<PlaylistPlaylistsPresentationModel>) {

        val gridList = mutableListOf<GridItem>()

        gridList.addAll(createAutoGridList(playlists))

        gridList.addAll(createAllGridList(playlists))

        this.allPlaylistsGrid.clear()

        this.allPlaylistsGrid.addAll(gridList)

        this.adapterAllPlaylists.notifyList(this.allPlaylistsGrid)

        this.adapterAllPlaylists.notifyDataSetChanged()
    }

    private fun createAutoGridList(playlists: List<PlaylistPlaylistsPresentationModel>): List<GridItem> {

        val list = mutableListOf<GridItem>()

        list.add(GridItem.Header(
            action = null,
            titleId = com.example.core.ui.R.string.created_automatically,
            actionId = null
        ))

        list.addAll(playlists.filter { it.id in AUTO_PLAYLIST_IDS }.map { playlist ->
            playlist.toGridItem(
                getLabelString(playlist.label),
                action = {
                    viewModelPlaylists.selectPlaylist(
                        it
                    )
                },
                action1 = {
                    viewModelPlaylists.setMusicSource(
                        it
                    )
                }
            )
        })

        return list
    }

    private fun createAllGridList(playlists: List<PlaylistPlaylistsPresentationModel>): List<GridItem> {

        val list = mutableListOf<GridItem>()

        list.add(GridItem.Header(
            action = {
                showNewPlaylistDialog(
                    com.example.core.ui.R.string.create_playlist,
                    com.example.core.ui.R.string.choose_playlist_name,
                    object: OnPlaylistCreated {

                        override fun onCreated(playlistName: String) {

                            viewModelPlaylists.insertNewPlaylist(
                                playlistName = playlistName
                            )
                        }
                    }
                )
            },
            titleId = com.example.core.ui.R.string.all_playlists,
            actionId = com.example.core.ui.R.string.create_playlist
        ))

        list.addAll(playlists.filter { it.id !in AUTO_PLAYLIST_IDS }.map { playlist ->
            playlist.toGridItem(
                getLabelString(playlist.label),
                action = {
                    viewModelPlaylists.selectPlaylist(
                        it
                    )
                },
                action1 = {
                    viewModelPlaylists.setMusicSource(
                        it
                    )
                }
            )
        })

        return list
    }

    private fun handlePlaylistSelect(selected: Boolean) {

        if (selected) {

            binding.selectedPlaylistLayout.visibility = View.VISIBLE

            binding.playlistsLayout.visibility = View.GONE
        } else {

            binding.selectedPlaylistLayout.visibility = View.GONE

            binding.playlistsLayout.visibility = View.VISIBLE
        }
    }

    private fun setDefaultPlaylistInfo() {

        binding.playlistName.setText("")

        binding.songCount.setText("")

        updatePlaylistSongs(emptyList())
    }

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

    fun showErrorToast(errorCode: Int) {

        val message = when(errorCode) {

            0 -> com.example.core.ui.R.string.song_already_contained
            else -> com.example.core.ui.R.string.nothing
        }

        Snackbar.make(binding.root,message, Snackbar.LENGTH_LONG).show()
    }

    private fun getLabelString(label: String): String {

        return when(label) {
            FAVORITES_NAME -> resources.getString(com.example.core.ui.R.string.favorites)
            RECENT_NAME -> resources.getString(com.example.core.ui.R.string.recent)
            else -> label
        }
    }
}