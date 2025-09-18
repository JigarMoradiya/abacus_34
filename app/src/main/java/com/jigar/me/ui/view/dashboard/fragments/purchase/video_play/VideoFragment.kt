package com.jigar.me.ui.view.dashboard.fragments.purchase.video_play

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.jigar.me.databinding.FragmentPurchasePreviewVideoBinding
import com.jigar.me.utils.extensions.hide
import com.jigar.me.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class VideoFragment : Fragment() {

    private var assetFileName: String = "video_free_mode.mp4"
    private var _binding: FragmentPurchasePreviewVideoBinding? = null
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPurchasePreviewVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Adjust aspect ratio dynamically
        val constraintSet = ConstraintSet().apply {
            clone(binding.conVideo)
            if (assetFileName == "video_free_mode.mp4") {
                setDimensionRatio(binding.playerView.id, "1920:882")
            } else {
                setDimensionRatio(binding.playerView.id, "1920:954")
            }
        }
        constraintSet.applyTo(binding.conVideo)

        // Copy video from assets to cache (ExoPlayer cannot play directly from assets)
        val file = File(requireContext().cacheDir, assetFileName)
        if (!file.exists()) {
            requireContext().assets.open(assetFileName).use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
        }
        val videoUri = Uri.fromFile(file)

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(requireContext()).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ONE

            addListener(object : Player.Listener {
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    if (_binding == null) return
                    if (isLoading) {
                        binding.progressBar.show()
                    } else {
                        binding.progressBar.hide()
                    }
                }
            })

            prepare()
            playWhenReady = true
        }

        binding.playerView.player = player
    }

    override fun onDestroyView() {
        super.onDestroyView()
        player?.release()
        player = null
        _binding = null
    }

    companion object {
        fun newInstance(fileName: String): VideoFragment {
            val fragment = VideoFragment()
            fragment.assetFileName = fileName
            return fragment
        }
    }
}
