package com.jigar.me.ui.view.base

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.PlaySound
import com.jigar.me.utils.extensions.toastS
import java.io.IOException


/**
 * Used for handle common methods of activities
 */
abstract class BaseActivity : AppCompatActivity() {
    lateinit var prefManager : AppPreferencesHelper
    var bgPlayer : MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        prefManager = AppPreferencesHelper(this, AppConstants.PREF_NAME)
//        this.setLocale(prefManager.getCustomParam(Constants.appLanguage,"en"))
        super.onCreate(savedInstanceState)
    }

    fun playBackgroundMusic() {
        val volume = prefManager.getCustomParamInt(AppConstants.Settings.Setting_bg_music_volume, AppConstants.Settings.Setting_bg_music_volume_default)
        if (volume > 0){
            bgPlayer = MediaPlayer()
            try {
                val afd = assets.openFd(PlaySound.background_music)
                if (bgPlayer?.isPlaying == true) {
                    bgPlayer?.stop()
                }
                bgPlayer?.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                bgPlayer?.isLooping = true
                bgPlayer?.prepare()
                setVolumeAnsStart(volume)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (bgPlayer?.isPlaying == true){
            bgPlayer?.pause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (bgPlayer?.isPlaying != true){
            bgPlayer?.start()
        }
    }

    fun setMusicVolume(progress: Int) {
        if (progress == 0){
            bgPlayer?.pause()
        }else{
            if (bgPlayer == null){
                playBackgroundMusic()
            }else{
                setVolumeAnsStart(progress)
            }

        }
    }

    private fun setVolumeAnsStart(progress: Int) {
        val volume = (progress.toFloat() / 100f)
        bgPlayer?.setVolume(volume,volume)
        if (bgPlayer?.isPlaying != true){
            bgPlayer?.start()
        }
    }

    fun showLoading() {
        if (progressDialog != null && progressDialog?.isShowing == false) {
            progressDialog?.show()
        } else {
            initProgressDialog()
            progressDialog?.show()
        }
    }

    fun hideLoading() {
        if (progressDialog != null && progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    private var progressDialog: AlertDialog? = null

    open fun initProgressDialog() {
        val inflater = layoutInflater
        val alertLayout: View = inflater.inflate(R.layout.dialog_loading, null)
        val builder1 = AlertDialog.Builder(this)
        builder1.setView(alertLayout)
        builder1.setCancelable(true)
        progressDialog = builder1.create()
        progressDialog?.setCancelable(true)
        progressDialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    // api failure
    fun onFailure(error: String?) {
        error?.let { showToast(it) }
    }

    fun showToast(id : Int){
        toastS(getString(id))
    }
    fun showToast(msg : String){
        toastS(msg)
    }
}