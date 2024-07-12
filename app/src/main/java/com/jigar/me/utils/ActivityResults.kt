package com.jigar.me.utils

import android.Manifest
import android.R.attr.bitmap
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.DIRECTORY_DCIM
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
import android.provider.MediaStore.MediaColumns.*
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.jigar.me.BuildConfig
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*


// permission

fun hasPermission(context: Context, permission:String): Boolean {
    return (ActivityCompat.checkSelfPermission(context,permission)== PackageManager.PERMISSION_GRANTED)
}
fun Context.checkPermissions(type: String,launcherPermission: ActivityResultLauncher<Array<String>>) : Boolean {
        var permission = true
        val listPermissionsNeeded = ArrayList<String>()
        when (type) {
            //these types are only for the AppSync Thing

            Constants.NOTIFICATION_PERMISSION -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notificationPermission =  ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)
                    if (notificationPermission != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            launcherPermission.launch(listPermissionsNeeded.toArray(Array(listPermissionsNeeded.size) { "it = $it" }))
            permission = false
        }
        return permission
    }
