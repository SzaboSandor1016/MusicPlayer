package com.example.musicplayer

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.features.player.presentation.fragment.FragmentPlayer
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.viewmodel.ViewModelMain
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private val viewModelMain: ViewModelMain by viewModel<ViewModelMain>()
    private lateinit var binding: ActivityMainBinding

    companion object {

        private const val BASE_ALBUM_ART_URI = "content://media/external/audio/albumart"
        private const val PERMISSIONS_REQUEST_READ_MEDIA_AUDIO = 1
    }

    override fun onStart() {
        super.onStart()

        viewModelMain.syncRoomWithMediaStore()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_MEDIA_AUDIO
            )
        }

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            override fun onTabSelected(p0: TabLayout.Tab?) {
                val index = p0?.position

                if (index != null) {

                    handleTabSelect(
                        index = index
                    )
                    viewModelMain.selectTab(
                        index = index
                    )
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

        })


        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.selectedTabIndexState.collect {

                    if (it != binding.tabLayout.selectedTabPosition) {

                        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(it))
                    }
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.allAlbumsState.collect { albums ->

                    albums.map {

                        val uri = ContentUris.withAppendedId(
                            /*MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,*/BASE_ALBUM_ART_URI.toUri(),
                            it.id
                        )

                        Glide.with(
                            this@MainActivity
                        ).load(
                            uri
                        ).diskCacheStrategy(
                            DiskCacheStrategy.ALL
                        ).onlyRetrieveFromCache(
                            false
                        ).preload()
                    }
                }
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)

        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.isFitToContents = true

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        initPlayerFragment()

        //binding.playerView.setPlayer(mediaController)
    }

    override fun onStop() {

        super.onStop()
    }

    private fun handleTabSelect(index: Int) {

        val request = when(index) {

            0 -> NavDeepLinkRequest.Builder.fromUri(
            "android-app://features/songs".toUri()
            ).build()
            1 -> NavDeepLinkRequest.Builder.fromUri(
                "android-app://features/playlists".toUri()
            ).build()
            2 -> NavDeepLinkRequest.Builder.fromUri(
                "android-app://features/artists".toUri()
            ).build()
            3 -> NavDeepLinkRequest.Builder.fromUri(
                "android-app://features/albums".toUri()
            ).build()
            else -> NavDeepLinkRequest.Builder.fromUri(
                "android-app://features/genres".toUri()
            ).build()
        }

        findNavController(R.id.nav_host_fragment_content_main).navigate(request)
    }

    private fun initPlayerFragment() {
        val tag = "Player_FRAGMENT"

        val fragment = supportFragmentManager.findFragmentByTag(tag) ?: FragmentPlayer.Companion.newInstance()

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.bottomSheetContainer.id, fragment, tag)
        }
    }
}