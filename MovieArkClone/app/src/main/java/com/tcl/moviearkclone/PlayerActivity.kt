package com.tcl.moviearkclone

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.ui.PlayerView

class PlayerActivity : AppCompatActivity() {

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        playerView = findViewById(R.id.playerView)

        val url = intent.getStringExtra("url") ?: return
        val title = intent.getStringExtra("title") ?: "Player"

        player = ExoPlayer.Builder(this).build().also { exo ->
            playerView.player = exo
            val uri = Uri.parse(url)
            val mediaItem = MediaItem.fromUri(uri)
            if (url.contains(".m3u8", ignoreCase = true)) {
                val dataSourceFactory = DefaultDataSource.Factory(this)
                val hlsSource = HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
                exo.setMediaSource(hlsSource)
            } else {
                exo.setMediaItem(mediaItem)
            }
            exo.prepare()
            exo.playWhenReady = true
        }
        supportActionBar?.title = title
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
