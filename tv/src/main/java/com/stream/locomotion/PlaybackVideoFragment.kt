package com.stream.locomotion

import android.net.Uri
import android.os.Bundle
import androidx.core.content.IntentCompat
import androidx.leanback.app.VideoSupportFragment
import androidx.leanback.app.VideoSupportFragmentGlueHost
import androidx.leanback.media.MediaPlayerAdapter
import androidx.leanback.media.PlaybackTransportControlGlue
import androidx.leanback.widget.PlaybackControlsRow

/** Handles video playback with media controls. */
class PlaybackVideoFragment : VideoSupportFragment() {

    private lateinit var mTransportControlGlue: PlaybackTransportControlGlue<MediaPlayerAdapter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val movie = IntentCompat.getSerializableExtra(
            requireActivity().intent,
            DetailsActivity.MOVIE,
            Movie::class.java
        )
        val fallbackUrl = requireActivity().intent.getStringExtra(PlaybackActivity.EXTRA_STREAM_URL)
        val title = requireActivity().intent.getStringExtra(PlaybackActivity.EXTRA_TITLE) ?: "Locomotion"
        val description = movie?.description ?: ""
        val videoUrl = movie?.videoUrl ?: fallbackUrl
        if (videoUrl.isNullOrBlank()) {
            return
        }

        val glueHost = VideoSupportFragmentGlueHost(this@PlaybackVideoFragment)
        val playerAdapter = MediaPlayerAdapter(context)
        playerAdapter.setRepeatAction(PlaybackControlsRow.RepeatAction.INDEX_NONE)

        mTransportControlGlue = PlaybackTransportControlGlue(getActivity(), playerAdapter)
        mTransportControlGlue.host = glueHost
        mTransportControlGlue.title = title
        mTransportControlGlue.subtitle = description
        mTransportControlGlue.playWhenPrepared()

        playerAdapter.setDataSource(Uri.parse(videoUrl))
    }

    override fun onPause() {
        super.onPause()
        mTransportControlGlue.pause()
    }
}
