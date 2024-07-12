package com.jigar.me.ui.view.confirm_alerts.bottomsheets

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.jigar.me.R
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.databinding.BottomSheetVoiceControlSettingBinding
import com.jigar.me.utils.Constants
import com.jigar.me.utils.extensions.onClick
import com.jigar.me.utils.extensions.setBottomSheetDialogAttr
import com.jigar.me.utils.extensions.toastS
import java.io.File
import java.util.Locale


class VoiceControllerSetting(
    val activity: Activity,
    listener:VoiceControllerSettingInterface,
    prefManager : AppPreferencesHelper,
    val tts:TextToSpeech
) {

    var dialog: BottomSheetDialog = BottomSheetDialog(activity,R.style.BottomSheetDialog)

    private lateinit var currentLang : Locale
    private lateinit var currentVoiceName : String
    private var langList = mutableListOf<Locale>()
    private var voiceList = mutableListOf<Voice>()

    init {

        val sheetBinding: BottomSheetVoiceControlSettingBinding = BottomSheetVoiceControlSettingBinding.inflate(activity.layoutInflater,null,false)
        dialog.setContentView(sheetBinding.root)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        activity.setBottomSheetDialogAttr(dialog, Constants.bottomSheetWidthBaseOnRatio7)

        val voice = mutableListOf<String>()
        val tvVoicesLabel = sheetBinding.tvVoicesLabel
        val voiceAdapter = ArrayAdapter(activity,R.layout.layout_language_spinner,voice)
        sheetBinding.voiceSelector.adapter = voiceAdapter
        sheetBinding.voiceSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentVoiceName = voiceList[position].name
            }
        }

        val languages = tts.availableLanguages
        val language = mutableListOf<String>()
        langList.clear()
        sheetBinding.tvLanguagesLabel.text = "Languages (${languages.size})"

        val currentLanguage = prefManager.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_LANGUAGE, "")
        val localLanguage = if (currentLanguage.isEmpty()){
            Locale(AppPreferencesHelper.DEFAULT_TTS_LANGUAGE_VALUE)
        }else{
            Gson().fromJson(currentLanguage, Locale::class.java)
        }
        languages.map {
            langList.add(it)
        }
        var lanIndex = 0
        langList.sortBy { it.displayName }
        langList.forEachIndexed { index, locale ->
            if (locale.toLanguageTag().toString().lowercase(Locale.getDefault()) == localLanguage.toString().lowercase(Locale.getDefault()).replace("_","-")){
                lanIndex = index
            }
            language.add(locale.displayName)
        }

        val langAdapter = ArrayAdapter(activity,R.layout.layout_language_spinner,language)
        sheetBinding.languageSelector.adapter = langAdapter

        sheetBinding.languageSelector.setSelection(lanIndex)
        sheetBinding.languageSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    currentLang = langList[position]
                    voiceAdapter.clear()
                    val localVoices = mutableListOf<String>()
                    voiceList.clear()
                    val voices = tts.voices
                    if (voices != null){
                        val localList = voices.filter {
                            it.locale == currentLang
                        }.toList()

                        voiceList.addAll(localList)
                        localList.forEachIndexed { index, voice ->
                            if (voice.name == prefManager.getCustomParam(AppPreferencesHelper.KEY_DEFAULT_TTS_VOICE, AppPreferencesHelper.DEFAULT_TTS_VOICE_VALUE)){
                                sheetBinding.voiceSelector.setSelection(index)
                            }
                            localVoices.add("${voice.name} => ${voice.locale.country}")
                        }
                        tvVoicesLabel.text = "Voices (${localVoices.size})"
                        voiceAdapter.addAll(localVoices)
                    }else{
                        activity.toastS(activity.getString(R.string.in_your_device_voices_not_found))
                    }

            }
        }
        sheetBinding.btnCheckVoice.onClick {
            val pitch = if (sheetBinding.rsPitch.currentValue == 0){
                0f
            } else{
                (sheetBinding.rsPitch.currentValue.toFloat()/10)
            }
            val speed = if (sheetBinding.rsSpeed.currentValue == 0){
                0f
            } else{
                (sheetBinding.rsSpeed.currentValue.toFloat()/10)
            }

            tts.voice = Voice(currentVoiceName, currentLang, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_VERY_HIGH, false, setOf(""))
//        tts.language = localLanguage
            tts.setPitch(pitch)
            tts.setSpeechRate(speed)
            readAgain()
        }
        sheetBinding.btnUpdate.onClick {
            val pitch = if (sheetBinding.rsPitch.currentValue == 0){
                0f
            } else{
                (sheetBinding.rsPitch.currentValue.toFloat()/10)
            }
            val speed = if (sheetBinding.rsSpeed.currentValue == 0){
                0f
            } else{
                (sheetBinding.rsSpeed.currentValue.toFloat()/10)
            }

            listener.updateVoiceSettings(pitch,speed,currentVoiceName,currentLang)
//            TODO Temp
//            saveAudio()
        }

        val currentSpeed = prefManager.getDefaultTTSSpeed()
        val currentPitch = prefManager.getDefaultTTSPitch()
        val pitch = if (currentPitch == 0F){
            0f
        } else{
            currentPitch * 10
        }
        val speed = if (currentSpeed == 0F){
            0f
        } else{
            currentSpeed * 10
        }
        sheetBinding.rsPitch.currentValue = pitch.toInt()
        sheetBinding.rsSpeed.currentValue = speed.toInt()

        dialog.setOnDismissListener {
            if (tts.isSpeaking) tts.stop()
        }
        sheetBinding.tvCancel.onClick {
            dialog.dismiss()
        }

    }

    private fun readAgain(){
        val textToRead = activity.getString(R.string.speak_test)
//        val textToRead = "Welcome to the Abacus Child Learning Application, where mastering math becomes an exciting journey for your child! Application is designed to teach math using the Abacus."
        if (tts.isSpeaking) tts.stop()
        tts.speak(textToRead,TextToSpeech.QUEUE_ADD,null,"id")
    }
    private fun saveAudio(){

//        val textToRead = activity.getString(R.string.speak_test)
        val textToRead = "Welcome to the Abacus Child Learning Application, where mastering math becomes an exciting journey for your child! Application is designed to teach math using the Abacus."
        if (tts.isSpeaking) tts.stop()
        tts.speak(textToRead,TextToSpeech.QUEUE_ADD,null,"id")

        val map = HashMap<String, String>()
        map[TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID] = textToRead
        val bundle = Bundle()
        bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"speak")
        val myDir = File(Environment.getExternalStorageDirectory().absolutePath + "/Download/AbacusAudio")
        if (!myDir.exists()){
            myDir.mkdir()
        }
        val fileName = currentLang.displayName+"_"+currentVoiceName+".wav"
        val path = "${myDir.path}/${fileName}"
//        val sr = tts.synthesizeToFile(textToRead,bundle,File(path),"id")
        val sr = tts.synthesizeToFile(textToRead,map,path)
    }

    fun dismiss(){
        dialog.dismiss()
    }

    fun show(){
        dialog.show()
    }

}

interface VoiceControllerSettingInterface {
    fun updateVoiceSettings(pitch:Float,speed:Float,voice: String,language:Locale)
}