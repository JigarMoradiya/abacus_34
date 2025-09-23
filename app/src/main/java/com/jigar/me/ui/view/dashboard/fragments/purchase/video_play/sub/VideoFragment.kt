package com.jigar.me.ui.view.dashboard.fragments.purchase.video_play.sub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.jigar.me.R
import com.jigar.me.databinding.FragmentPurchasePreviewVideoBinding
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VideoFragment : Fragment() {

    private var assetFileName: String = "video_free_mode.mp4"
    private var _binding: FragmentPurchasePreviewVideoBinding? = null
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null
    private var assetPackManager: AssetPackManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchasePreviewVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @UnstableApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAssetPack()
        setupInstallTime()
    }

    @UnstableApi
    private fun setupInstallTime() {
        val videoUri = "asset:///$assetFileName".toUri()
        binding.playerView.defaultArtwork = ContextCompat.getDrawable(requireContext(), R.drawable.placeholder)
        binding.playerView.setKeepContentOnPlayerReset(true)

        // Initialize Media3 ExoPlayer
        player = ExoPlayer.Builder(requireContext()).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = false   // start paused (important!)

            addListener(object : Player.Listener {
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    if (_binding == null) return
                    if (isLoading) binding.progressBar.show() else binding.progressBar.hide()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (_binding == null) return
                    if (playbackState == Player.STATE_READY) {
                        binding.progressBar.hide()
                        binding.playerView.show()
                    }
                }
            })

            prepare()
        }
        binding.playerView.player = player
    }

    fun playVideo() {
        player?.playWhenReady = true
    }

    fun pauseVideo() {
        player?.playWhenReady = false
    }

    override fun onPause() {
        super.onPause()
        pauseVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _binding = null
    }

    private fun setupAssetPack() {
        if (assetPackManager == null) {
            assetPackManager = AssetPackManagerFactory.getInstance(this.requireContext())
        }
    }

    companion object {
        fun newInstance(fileName: String): VideoFragment {
            val fragment = VideoFragment()
            fragment.assetFileName = fileName
            return fragment
        }
    }
}