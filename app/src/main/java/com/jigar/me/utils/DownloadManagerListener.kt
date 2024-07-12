package com.jigar.me.utils

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import com.jigar.me.utils.extensions.downloadManager
import com.jigar.me.utils.extensions.toastS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DownloadManagerListener(
    private var context: Context
) {

    private var downloadID: Long = 0
    private var downloading = false
    private var hasUpdated = false
    private val downloadManager = context.downloadManager
    private var listener: DownloadManagerInterface? = null

    fun setListener(listener:  DownloadManagerInterface){
        this.listener = listener
    }

    fun removeListener(){
        this.listener = null
    }

    suspend fun queryDownloadProgress(id: Long): Int{
        return try {
            val query = DownloadManager.Query()
            query.setFilterById(downloadID)
            val c = downloadManager.query(query)
            var download_percentage = 0L
            if (c.moveToFirst()){
                val bytes_downloaded = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)).toLong()
                val bytes_total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)).toLong()
                download_percentage = if (bytes_total > 0) bytes_downloaded * 100L / bytes_total else 0
            }
            download_percentage.toInt()
        }catch (e: Exception){
            e.printStackTrace()
            0
        }
    }

    fun startListening(downloadId: Long){
        this.downloadID = downloadId
        GlobalScope.launch(Dispatchers.IO) {

            do {
                downloading = true
                val c: Cursor
                val query = DownloadManager.Query()
                query.setFilterById(downloadID)
                c = downloadManager.query(query)
                if (c.moveToFirst()) {
                    val status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON))
                    val bytes_downloaded = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)).toLong()
                    val bytes_total = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)).toLong()
                    val download_percentage: Long
                    download_percentage = if (bytes_total > 0) bytes_downloaded * 100L / bytes_total else 0


                    if (status == DownloadManager.STATUS_RUNNING) {
                        downloading = true
//                        val downloadedSize = String.format("%.3fMB", bytes_downloaded * 1.0 / 1024L / 1024L)
//                        val totalSize = String.format("%.2fMB", bytes_total * 1.0 / 1024L / 1024L)
                        withContext(Dispatchers.Main){
                            listener?.updateProgress(download_percentage.toInt(), bytes_downloaded)
                        }
                        if (!hasUpdated){
                            withContext(Dispatchers.Main){
                                listener?.updateFileSize(bytes_total)
                            }
                        }
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        downloading = false
                        withContext(Dispatchers.Main){
                            context.toastS("Downloading fail")
                            listener?.downloadFailed(reason)
                        }
                    } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                        withContext(Dispatchers.Main){
                            context.toastS("Downloading complete")
                            listener?.downloadSuccessful()
                        }
                    } else {
                        downloading = true
                    }

                } else {
                    downloading = false
                    withContext(Dispatchers.Main){
                        listener?.downloadFailed(-1)
                    }
                }
                c.close()
            } while (downloading)

        }
    }

    fun cancelDownload(id: Long){
        downloadManager.remove(id)
        downloading = false
    }

    interface DownloadManagerInterface{
        fun downloadFailed(reason: Int)

        fun downloadSuccessful()

        fun updateProgress(progress: Int, bytesDownloaded: Long)

        fun updateFileSize(totalSize: Long)
    }

}