package id.niteroomcreation.mncvideorecordfilter.util

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Septian Adi Wijaya on 09/04/2023.
 * please be sure to add credential if you use people's code
 */
object CommonUtil {

    fun provideFilePath(): String {
        return provideExternalFolder().absolutePath +
                "/" + SimpleDateFormat("yyyyMM_dd-HHmmss").format(Date()) +
                "_mnc_video_filter.mp4"
    }

    private fun provideExternalFolder(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES)
    }

    fun storedOnGallery(context: Context, filePath: String) {
        var contentValues = ContentValues(2)
        contentValues.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        contentValues.put(MediaStore.Video.Media.DATA, filePath)

        //adds to the gallery
        context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

        //trigger system to scan to listing the recent file
        val file = File(filePath)
        MediaScannerConnection.scanFile(context, arrayOf(file.toString()), null, null)
    }
}