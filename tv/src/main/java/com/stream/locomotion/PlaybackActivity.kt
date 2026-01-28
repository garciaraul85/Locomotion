package com.stream.locomotion

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.stream.locomotion.player.PlaybackViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/** Loads [PlaybackVideoFragment]. */
@AndroidEntryPoint
class PlaybackActivity : FragmentActivity() {

    companion object {
        const val EXTRA_STREAM_URL = "extra_stream_url"
        const val EXTRA_TITLE = "extra_title"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        val viewModel = ViewModelProvider(this)[PlaybackViewModel::class.java]
        val offlineBanner = findViewById<android.view.View>(R.id.offline_banner)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isOnline.collect { isOnline ->
                    offlineBanner.visibility = if (isOnline) android.view.View.GONE else android.view.View.VISIBLE
                }
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.playback_container, PlaybackVideoFragment())
                .commit()
        }
    }
}
