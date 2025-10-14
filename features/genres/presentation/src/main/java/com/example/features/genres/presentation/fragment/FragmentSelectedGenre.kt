package com.example.features.genres.presentation.fragment

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
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.SongOptionsDialogHelper
import com.example.core.ui.adapter.DefaultSongAdapter
import com.example.core.ui.model.SongInfoUIModel
import com.example.features.genres.presentation.R
import com.example.features.genres.presentation.databinding.FragmentSelectedGenreBinding
import com.example.features.genres.presentation.mappers.toSongInfoUIModel
import com.example.features.genres.presentation.model.GenreGenresPresentationModel
import com.example.features.genres.presentation.model.PlaylistInfoGenresPresentationModel
import com.example.features.genres.presentation.model.SongGenresPresentationModel
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
 * Use the [FragmentSelectedGenre.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentSelectedGenre : Fragment() {
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
         * @return A new instance of fragment FragmentSelectedGenre.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentSelectedGenre().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }
    private val playlists: ArrayList<PlaylistInfoGenresPresentationModel> = ArrayList()

    private val genreSongs = ArrayList<SongInfoUIModel>()

    private lateinit var adapterGenreSongs: DefaultSongAdapter

    private val viewModelGenres: ViewModelGenres by viewModel<ViewModelGenres>(ownerProducer = { requireActivity()})

    private var _binding: FragmentSelectedGenreBinding? = null

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
        _binding = FragmentSelectedGenreBinding.inflate(
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

        adapterGenreSongs = DefaultSongAdapter()

        binding.genreSongs.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.genreSongs.adapter = adapterGenreSongs

        adapterGenreSongs.setOnClickListener { songId ->

            viewModelGenres.getSelectedGenreId()?.let { genreId ->

                viewModelGenres.setMusicSource(
                    genreId,
                    songId
                )
            }
        }

        adapterGenreSongs.setOnMoreOptionsClickListener { view, songId ->

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
                                viewModelGenres.addToPlaylist(
                                    playlistId = it,
                                    songId = songId
                                )
                            }
                        )
                    },
                    com.example.core.ui.R.string.add_to_next_up to {

                        viewModelGenres.addToUpNext(songId)
                    },
                    com.example.core.ui.R.string.add_to_queue to  {

                        viewModelGenres.addToQueue(songId)
                    }
                )
            )

            SongOptionsDialogHelper.showSongOptionsDialog(
                requireContext(),
                view,
                options
            )
        }

        adapterGenreSongs.submitList(this.genreSongs)

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelGenres.selectedGenreState.collect {

                    it?.let {

                        updateGenreUIElements(it)
                    }?: findNavController().navigate(R.id.action_fragmentSelectedGenre_to_fragmentGenres)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelGenres.playlistsInfoState.collect {

                    updatePlaylistInfo(it)
                }
            }
        }

        binding.back.setOnClickListener { l ->

            viewModelGenres.setSelectedGenreId(null)
        }
    }

    private fun updateGenreUIElements(genre: GenreGenresPresentationModel) {

        updateGenreInfo(
            albumName = genre.name,
            songCount = genre.songs.size
        )

        updateGenreSongsList(genre.songs)
    }

    private fun updateGenreInfo(albumName: String, songCount: Int) {

        binding.genreName.text = albumName

        binding.songCount.text = songCount.toString()
    }

    private fun updateGenreSongsList(songs: List<SongGenresPresentationModel>) {

        this.genreSongs.clear()

        this.genreSongs.addAll(
            songs.map { it.toSongInfoUIModel() }
        )

        this.adapterGenreSongs.submitList(this.genreSongs)

        this.adapterGenreSongs.notifyDataSetChanged()
    }

    private fun updatePlaylistInfo(playlists: List<PlaylistInfoGenresPresentationModel>) {

        this.playlists.clear()

        this.playlists.addAll(playlists)
    }
}