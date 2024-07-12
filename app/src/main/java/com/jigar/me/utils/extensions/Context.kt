 package com.jigar.me.utils.extensions

import android.app.Activity
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Insets
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.DisplayMetrics
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.jigar.me.R
import com.jigar.me.data.local.data.DataProvider
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.utils.AppConstants
import com.jigar.me.utils.Constants
import java.io.File
import java.io.IOException
import java.util.*


 @Suppress("UNCHECKED_CAST")
 fun <T> Context.getSystemServiceAs(serviceName: String) = getSystemService(serviceName) as T

 val Context.downloadManager: DownloadManager
    get() = getSystemServiceAs(Context.DOWNLOAD_SERVICE)

 val Context.layoutInflater: LayoutInflater
    get() = getSystemServiceAs(Context.LAYOUT_INFLATER_SERVICE)

 val Context.clipboardManager: ClipboardManager
    get() = getSystemServiceAs(Context.CLIPBOARD_SERVICE)

 val Context.notificationManager: NotificationManager
    get() = getSystemServiceAs(Context.NOTIFICATION_SERVICE)

 fun Context.toastS(message: String) {
     Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
 }

 fun Context.toastL(message: String) {
     Toast.makeText(this, message, Toast.LENGTH_LONG).show()
 }

 fun Context.setLocale(lan: String) {
     if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
         val myLocale = Locale(lan)
         val res: Resources = resources
         val dm: DisplayMetrics = res.displayMetrics
         val conf: Configuration = res.configuration
         conf.locale = myLocale
         res.updateConfiguration(conf, dm)
     } else {
         val locale = Locale(lan)
         Locale.setDefault(locale)
         val configuration: Configuration = resources.configuration
         configuration.setLocale(locale)
         createConfigurationContext(configuration)
     }
 }

 fun Context.hideKeyboard(view: View) {
     val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
     imm.hideSoftInputFromWindow(view.windowToken, 0)
 }

 fun Context.showKeyboard(view: View) {
     val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
     inputMethodManager!!.showSoftInput(view,0)
 }

 fun Activity.hideKeyboard() {
     val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
     //Find the currently focused view, so we can grab the correct window token from it.
     var view = currentFocus
     //If no view currently has focus, create a new one, just so we can grab a window token from it
     if (view == null) {
         view = View(this)
     }
     imm.hideSoftInputFromWindow(view.windowToken, 0)
 }


 fun Activity.setBottomSheetDialogAttr(bottomSheetDialog: BottomSheetDialog,widthRatio : Int = Constants.bottomSheetWidthBaseOnRatio5, isDraggable : Boolean = true, isfullScreen : Boolean = false,isVertical : Boolean = false) {
     bottomSheetDialog.setOnShowListener { dialog ->
         val bsd = dialog as BottomSheetDialog
         val bottomSheet = bsd.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
         val bottomSheetBehavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet!!)
         if (isfullScreen){
             val layoutParams = bottomSheet.layoutParams
             val windowHeight: Int = getScreenHeight()
             if (layoutParams != null) {
                 layoutParams.height = windowHeight
             }
             bottomSheet.layoutParams = layoutParams
         }
         bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
         bottomSheetBehavior.isDraggable = isDraggable

         bottomSheetBehavior.isHideable = true
         bottomSheetBehavior.peekHeight = 0

         bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
             override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
                 when (newState) {
                     BottomSheetBehavior.STATE_COLLAPSED -> { bsd.cancel() }
                     BottomSheetBehavior.STATE_DRAGGING -> Unit
                     BottomSheetBehavior.STATE_EXPANDED -> Unit
                     BottomSheetBehavior.STATE_HALF_EXPANDED -> Unit
                     BottomSheetBehavior.STATE_HIDDEN -> Unit
                     BottomSheetBehavior.STATE_SETTLING -> Unit
                 }
             }
             override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {}
         })
     }

     val windows = bottomSheetDialog.window
     val colorD = ColorDrawable(Color.TRANSPARENT)
     val insetD = if (isVertical){
         InsetDrawable(colorD, 20.dp, 0, 20.dp, 0)
     }else{
        val width = getScreenWidth() / widthRatio
         InsetDrawable(colorD, width, 0, width, 0)
     }

     windows?.setBackgroundDrawable(insetD)
     if (!isfullScreen){
         val wlp = windows?.attributes
         wlp?.width = WindowManager.LayoutParams.MATCH_PARENT
         wlp?.height = WindowManager.LayoutParams.WRAP_CONTENT
         wlp?.gravity = Gravity.CENTER
         windows?.attributes = wlp
     }
 }

 fun Activity.getScreenWidth(): Int {
     return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         val windowMetrics = this.windowManager.currentWindowMetrics
         val insets: Insets = windowMetrics.windowInsets
             .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
         windowMetrics.bounds.width() - insets.left - insets.right
     } else {
         val displayMetrics = DisplayMetrics()
         this.windowManager.defaultDisplay.getMetrics(displayMetrics)
         displayMetrics.widthPixels
     }
 }
 fun Activity.getScreenHeight(): Int {
     return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
         val windowMetrics = this.windowManager.currentWindowMetrics
         val insets: Insets = windowMetrics.windowInsets
             .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
         windowMetrics.bounds.height() - insets.top - insets.bottom
     } else {
         val displayMetrics = DisplayMetrics()
         this.windowManager.defaultDisplay.getMetrics(displayMetrics)
         displayMetrics.widthPixels
     }
 }

val Context.isNetworkAvailable: Boolean
    get() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val n = cm.activeNetwork
        if (n != null) {
            val nc = cm.getNetworkCapabilities(n)
            return nc?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||
                    nc?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
        }
        return false
    }

fun Context.openMail(prefManager: AppPreferencesHelper) {
    val emailId = prefManager.getCustomParam(AppConstants.RemoteConfig.supportEmail,"")
    val email = Intent(Intent.ACTION_SENDTO,Uri.fromParts(
        "mailto",emailId, null))
//    email.type = "message/rfc822"
    email.putExtra(Intent.EXTRA_EMAIL,arrayOf(emailId.toString()))
    email.putExtra(Intent.EXTRA_SUBJECT, resources.getText(R.string.app_name))
    email.putExtra(Intent.EXTRA_TEXT, "")
//    email.setPackage("com.google.android.gm")
//    if (email.resolveActivity(packageManager)!=null){
        startActivity(Intent.createChooser(email, resources.getString(R.string.app_name)))
//    }else{
//        toastS(getString(R.string.gmail_not_found))
//    }

}
fun Context.openURL(url : String) {
    try {
        val i = Intent(Intent.ACTION_VIEW)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.data = Uri.parse(url)
        startActivity(i)
    } catch (e: Exception) {
     e.printStackTrace()
     toastS(getString(R.string.link_not_support))
    }
}
fun Context.shareIntent() {
//    val txt = getString(R.string.share_text_msg)
//    val msg = txt + "\nhttps://play.google.com/store/apps/details?id=${packageName}"
    val msg = "\uD83D\uDD22\uD83E\uDDEE Elevate Math Skills with \"Abacus Child Learning App\" – Where Math Meets Magic! \uD83D\uDCF1✨" +
            "\n\n" +
            "Unlock the potential of your child's math genius with our immersive Abacus Child Learning App. Experience the enchantment of abacus math and watch your child excel in calculations while having a blast! \uD83E\uDDEE\uD83C\uDF08" +
            "\n\n" +
            "\uD83D\uDD17 Download the App Now on the Google Play Store: https://play.google.com/store/apps/details?id=${packageName}" +
            "\n\n" +
            "\uD83C\uDF89 Whether your child is a beginner or looking to enhance their math prowess, \"Abacus Child Learning Application\" offers:\n" +
            "\n" +
            "- Learn fundamental math concepts with ease\n" +
            "- Develop strong problem-solving skills\n" +
            "- Boost concentration and mental agility\n" +
            "- Engaging abacus lessons for mental math mastery\n" +
            "- Fun and interactive math games and challenges\n" +
            "- Personalized learning to cater to each child's pace\n" +
            "- Confidence-building math activities\n" +
            "\n" +
            "\uD83E\uDDD9\u200D♂️ Make math an exciting adventure with the \"Abacus Child Learning Application.\" Join us in nurturing a love for numbers and setting the stage for academic success.\n" +
            "\n" +
            "Don't miss out – share this remarkable math journey with fellow parents and educators. Let's empower the next generation of math wizards together!"
    val sharingIntent = Intent(Intent.ACTION_SEND)
    sharingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    sharingIntent.type = "text/plain"
    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
    sharingIntent.putExtra(Intent.EXTRA_TEXT, msg)
    startActivity(sharingIntent)
}

 fun Context.openYoutube(url : String = AppConstants.YOUTUBE_URL) {
     val i = Intent(Intent.ACTION_VIEW)
     i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
     i.data = Uri.parse(url)
     startActivity(i)
 }



fun Context.downloadFilePath() : String?{
    val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), getString(R.string.download_folder))
    if (!folder.exists()) {
        folder.mkdir()
    }
//    return getExternalFilesDir("download")?.path
    return folder.path
}

 fun Context.readJsonAsset(fileName: String?): String {
    return if (!fileName.isNullOrEmpty()){
        try {
            val inputStream = assets.open("abacus/$fileName")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (e: IOException) {
            println(e.printStackTrace())
            ""
        }
    }else{""}
}

 fun Context.convert(n: Int): String {
     val tens = DataProvider.getTensList(this)
     val units = DataProvider.getUnitsList(this)
     if (n < 0) {
         return resources.getString(R.string.Minus) + " " + convert(-n)
     }
     if (n < 20) {
         return units[n]
     }
     if (n < 100) {
         return tens[n / 10] + (if (n % 10 != 0) " " else "") + units[n % 10]
     }
     if (n < 1000) {
         return units[n / 100] + " " + resources
             .getString(R.string.Hundred) + (if (n % 100 != 0) " " else "") + convert(n % 100)
     }
     if (n < 100000) {
         return convert(n / 1000) + " " + resources
             .getString(R.string.Thousand) + (if (n % 10000 != 0) " " else "") + convert(n % 1000)
     }
     return if (n < 10000000) {
         convert(n / 100000) + " " + resources
             .getString(R.string.Lakh) + (if (n % 100000 != 0) " " else "") + convert(n % 100000)
     } else convert(n / 10000000) + " " + resources
         .getString(R.string.Crore) + (if (n % 10000000 != 0) " " else "") + convert(n % 10000000)
 }

 fun Activity.setDialogAttr(dialog: AlertDialog, gravity : Int = Gravity.CENTER, dimClear : Boolean = false) {
//     this.window?.statusBarColor = statusBar
     val window = dialog.window
     val colorD = ColorDrawable(Color.TRANSPARENT)
     val insetD = InsetDrawable(colorD, 10.dp, 0, 10.dp, 0)
     window?.setBackgroundDrawable(insetD)
     if (dimClear){
         window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
     }
     window?.attributes?.gravity = gravity
 }