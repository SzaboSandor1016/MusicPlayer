package com.example.core.ui

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.core.ui.adapter.AdapterSelectPlaylistRecyclerView
import com.example.core.ui.databinding.LayoutSelectPlaylistDialogBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PlaylistDialogHelper {

    fun <T> showPlaylistSelectDialog(
        title: Int,
        message: Int,
        context: Context,
        playlists: List<T>,
        idSelector: (T) -> Long,
        labelSelector: (T) -> String,
        onPlaylistSelected: (Long) -> Unit) {

        val view = LayoutSelectPlaylistDialogBinding
            .inflate(LayoutInflater.from(context))

        //val playlistName: TextInputEditText = view.findViewById(com.example.features.playlists.presentation.R.id.new_playlist_name)

        val adapterPlaylists = AdapterSelectPlaylistRecyclerView(
            playlistsInfo = playlists,
            idSelector = idSelector,
            labelSelector = labelSelector
        )

        view.selectPlaylistRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        view.selectPlaylistRecyclerView.adapter = adapterPlaylists

        val playlistDialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setView(view.root)
            .setNegativeButton(context.resources.getString(com.example.core.ui.R.string.cancel)) { dialog, which ->

                dialog.dismiss()
            }
            .show()

        adapterPlaylists.setOnItemClickListener(object : AdapterSelectPlaylistRecyclerView.OnItemClickListener {

            override fun onClick(playlistId: Long) {

                onPlaylistSelected(playlistId)

                playlistDialog.dismiss()
            }
        })
    }
}

object SongOptionsDialogHelper {

    fun showSongOptionsDialog(
        context: Context,
        anchorView: View, // view to show above / near
        options: List<Pair<Int, () -> Unit>>
    ) {

        // Build the dialog
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Inflate a simple vertical LinearLayout
        val container = LinearLayout(context).apply {

            orientation = LinearLayout.VERTICAL

            setPadding(32, 32, 32, 32)

            layoutParams = LinearLayout.LayoutParams(

                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Create buttons dynamically
        options.forEach { (label, action) ->

            val button = MaterialButton(
                context,
                null,
                R.attr.song_options_style_attribute
            ).apply {

                setText(label)
                setOnClickListener {
                    action()

                    dialog.dismiss()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ).apply {
                    topMargin = 4
                    bottomMargin = 4
                }
            }
            container.addView(button)
        }

        dialog.setContentView(container)

        val location = IntArray(2)
        anchorView.getLocationOnScreen(location)
        dialog.window?.apply {

            val params = this.attributes

            params?.width = LinearLayout.LayoutParams.WRAP_CONTENT
            params?.gravity = Gravity.TOP or Gravity.END
            params?.x = location[0]
            params?.y = location[1] - anchorView.height
            this.attributes = params
            setDimAmount(0f)

        }

        // Optional: show the dialog near the clicked view
        dialog.show()

        // You could adjust the position using dialog.window?.attributes if needed
    }
}

object EqualizerDialogHelper {

    fun showEqualizerDialog(
        context: Context,
        presets: List<Pair<String, (Int) -> Unit>>,
        sliders: List<Pair<String, (Int, Int) -> Unit>>
    ) {

    }
}