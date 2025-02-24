package com.jigar.me.utils

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {
    val TAG = "FileUtils"
    fun getMimeType(file: File): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(file.path)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
    @SuppressLint("NewApi")
    fun Context.getPath(uri: Uri): String? {
        // check here to KITKAT or new version
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        // DocumentProvider
        // ExternalStorageProvider
        if (isExternalStorageDocument(uri)) {

            Log.d(TAG, "getPath: External Storage")

            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            val fullPath = getPathFromExtSD(split)

            Log.d(TAG, "getPath: External Storage Path: $fullPath")
            return if (fullPath !== "") {
                fullPath
            } else {
                null
            }
        }

        // DownloadsProvider
        if (isDownloadsDocument(uri)) {

            Log.d(TAG, "getPath: From Downloads")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                var cursor: Cursor? = null
                try {
                    cursor = this.contentResolver.query(
                        uri,
                        arrayOf(MediaStore.MediaColumns.DISPLAY_NAME),
                        null,
                        null,
                        null
                    )
                    if (cursor != null && cursor.moveToFirst()) {
                        val fileName = cursor.getString(0)
//                            val path =
//                                Environment.getExternalStorageDirectory().absolutePath
//                                    .toString() + "/Download/" + fileName
//
//                            Log.d(TAG, "getPath: From Download Path: $path")
//
//                            if (!TextUtils.isEmpty(path)) {
//                                return path
//                            }
                    }
                } finally {
                    cursor?.close()
                }
                val id: String = DocumentsContract.getDocumentId(uri)
                Log.d(TAG, "getPath: From Download ID: $id")
                if (!TextUtils.isEmpty(id)) {
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    val contentUriPrefixesToTry =
                        arrayOf(
                            "content://downloads/public_downloads",
                            "content://downloads/my_downloads"
                        )
                    for (contentUriPrefix in contentUriPrefixesToTry) {
                        return try {
                            val contentUri = ContentUris.withAppendedId(
                                Uri.parse(contentUriPrefix),
                                java.lang.Long.valueOf(id)
                            )
                            getDataColumn(this, contentUri, null, null)
                        } catch (e: NumberFormatException) {
                            //In Android 8 and Android P the id is not a number
                            uri.path!!.replaceFirst("^/document/raw:".toRegex(), "")
                                .replaceFirst("^raw:".toRegex(), "")
                        }
                    }
                }
            }
            else {
                val id = DocumentsContract.getDocumentId(uri)
                if (id.startsWith("raw:")) {
                    return id.replaceFirst("raw:".toRegex(), "")
                }
                var contentUri : Uri? = null
                try {
                    contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
                if (contentUri != null) {
                    return getDataColumn(this, contentUri!!, null, null)
                }
            }
        }


        // MediaProvider
        if (isMediaDocument(uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":").toTypedArray()
            val type = split[0]
            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }
            selection = "_id=?"
            selectionArgs = arrayOf(split[1])
            return getDataColumn(
                this, contentUri, selection,
                selectionArgs
            )
        }
        if (isGoogleDriveUri(uri)) {
            return getDriveFilePath(uri)
        }
        if (isWhatsAppFile(uri)) {
            return getFilePathForWhatsApp(uri)
        }
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            }
            if (isGoogleDriveUri(uri)) {
                return getDriveFilePath(uri)
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                // return getFilePathFromURI(context,uri);

                var path:String?=null

                GlobalScope.launch(Dispatchers.IO) {
                    path = copyFileToInternalStorage(uri, "userfiles")
                }

                Thread.sleep(1000)
                path

                // return getRealPathFromURI(context,uri);
            } else {
                getDataColumn(this, uri, null, null)
            }
        }
        if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    private fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }

    private fun getPathFromExtSD(pathData: Array<String>): String {
        val type = pathData[0]
        val relativePath = "/" + pathData[1]
        var fullPath = ""

        // on my Sony devices (4.4.4 & 5.1.1), `type` is a dynamic string
        // something like "71F8-2C0A", some kind of unique id per storage
        // don't know any API that can get the root path of that storage based on its id.
        //
        // so no "primary" type, but let the check here for other devices
        if ("primary".equals(type, ignoreCase = true)) {
            fullPath =
                Environment.getExternalStorageDirectory().toString() + relativePath
            if (fileExists(fullPath)) {
                return fullPath
            }
        }

        // Environment.isExternalStorageRemovable() is `true` for external and internal storage
        // so we cannot relay on it.
        //
        // instead, for each possible path, check if file exists
        // we'll start with secondary storage as this could be our (physically) removable sd card
        fullPath = System.getenv("SECONDARY_STORAGE") + relativePath
        if (fileExists(fullPath)) {
            return fullPath
        }
        fullPath = System.getenv("EXTERNAL_STORAGE") + relativePath
        return if (fileExists(fullPath)) {
            fullPath
        } else fullPath
    }

    private fun Context.getDriveFilePath(uri: Uri): String? {
        val returnCursor: Cursor =
            contentResolver.query(uri, null, null, null, null)!!
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        val size = java.lang.Long.toString(returnCursor.getLong(sizeIndex))
        val file = File(getCacheDir(), name)
        try {
            val inputStream: InputStream? = getContentResolver().openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()

            //int bufferSize = 1024;
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream?.read(buffers).also { read = it!! } != -1) {
                outputStream.write(buffers, 0, read)
            }
            Log.e("File Size", "Size " + file.length())
            inputStream?.close()
            outputStream.close()
            Log.e("File Path", "Path " + file.path)
            Log.e("File Size", "Size " + file.length())
        } catch (e: Exception) {
            Log.e("Exception", e.message!!)
        }
        return file.path
    }

    /***
     * Used for Android Q+
     * @param uri
     * @param newDirName if you want to create a directory, you can set this variable
     * @return
     */
    private suspend fun Context.copyFileToInternalStorage(
        uri: Uri,
        newDirName: String
    ): String? {

        var returnCursor:Cursor?=null
        var path:String?=null

        Log.d(TAG, "copyFileToInternalStorage: Triggered")

        try {

            withContext(Dispatchers.IO) {
                returnCursor = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE), null, null, null)!!
                /*
                 * Get the column indexes of the data in the Cursor,
                 *     * move to the first row in the Cursor, get the data,
                 *     * and display it.
                 * */
                val nameIndex = returnCursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = returnCursor?.getColumnIndex(OpenableColumns.SIZE)
                returnCursor?.moveToFirst()
                val name = returnCursor?.getString(nameIndex!!)
                val size = returnCursor?.getLong(sizeIndex!!).toString()
                val output: File
                if (newDirName != "") {
                    val dir = File(filesDir.toString() + "/" + newDirName)
                    if (!dir.exists()) {
                        dir.mkdir()
                    }
                    output = File(filesDir.toString() + "/" + newDirName + "/" + name)
                    Log.d(TAG, "copyFileToInternalStorage: ${output.absolutePath}")
                } else {
                    output = File(filesDir.toString() + "/" + name)
                    Log.d(TAG, "copyFileToInternalStorage: Else: ${output.absolutePath}")
                }
                try {
                    val inputStream: InputStream? = contentResolver.openInputStream(uri)
                    val outputStream = FileOutputStream(output)
                    var read = 0
                    val bufferSize = 1024
                    val buffers = ByteArray(bufferSize)
                    while (inputStream?.read(buffers).also { read = it!! } != -1) {
                        outputStream.write(buffers, 0, read)
                    }
                    inputStream?.close()
                    outputStream.close()
                } catch (e: Exception) {
                    Log.e("Exception", e.message!!)
                }
                path = output.path
            }

            Log.d(TAG, "copyFileToInternalStorage: Path: $path")
            return path
        } finally {
            returnCursor?.close()
        }
    }

    private fun Context.getFilePathForWhatsApp(uri: Uri): String? {
        var path:String?=null
        GlobalScope.launch(Dispatchers.IO) {
            path = copyFileToInternalStorage(uri, "whatsapp")
        }
        return path
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun isWhatsAppFile(uri: Uri): Boolean {
        return "com.whatsapp.provider.media" == uri.authority
    }

    private fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage" == uri.authority || "com.google.android.apps.docs.storage.legacy" == uri.authority
    }
}