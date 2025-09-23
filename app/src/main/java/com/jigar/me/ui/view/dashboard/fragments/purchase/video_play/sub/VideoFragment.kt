package com.jigar.me.ui.view.dashboard.fragments.purchase.video_play.sub

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
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
import java.io.File
import java.io.FileOutputStream
import androidx.core.net.toUri
import com.google.android.play.core.assetpacks.AssetPackStateUpdateListener

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
        // Copy asset to cache
//        val file = File(requireContext().cacheDir, assetFileName)
//        file.delete()
//        if (!file.exists()) {
//            requireContext().assets.open(assetFileName).use { inputStream ->
//                FileOutputStream(file).use { outputStream ->
//                    inputStream.copyTo(outputStream)
//                }
//            }
//        }
//        val videoUri = Uri.fromFile(file)

        val videoUri = "asset:///$assetFileName".toUri()
        binding.playerView.defaultArtwork = ContextCompat.getDrawable(requireContext(), R.drawable.placeholder)
        binding.playerView.setKeepContentOnPlayerReset(true)

        // Initialize Media3 ExoPlayer
        player = ExoPlayer.Builder(requireContext()).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_ONE   // loop
            playWhenReady = true                  // auto-play

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

    override fun onPause() {
        super.onPause()
        player?.pause()   // Pause when fragment is not visible
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true   // Resume when fragment becomes visible again
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
        assetPackManager?.registerListener(packStateListener)
    }

    private val packStateListener = AssetPackStateUpdateListener { assetPackState ->
        val text = "\n" + "${assetPackState.name()}"
        Log.e("jigarLogsss","text = "+text)

    }


    companion object {
        fun newInstance(fileName: String): VideoFragment {
            val fragment = VideoFragment()
            fragment.assetFileName = fileName
            return fragment
        }
    }
}
