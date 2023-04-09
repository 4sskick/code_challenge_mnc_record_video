package id.niteroomcreation.mncvideorecordfilter.util

import android.content.ContentValues
import android.content.Context
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.egl.filter.GlRGBFilter
import id.niteroomcreation.mncvideorecordfilter.presentation.custom.CameraFilter
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

    fun filterColor(colorName: String, gpuCameraRecorder: GPUCameraRecorder?) {
        when (colorName) {
            CameraFilter.NORMAL.name -> {
                gpuCameraRecorder?.setFilter(GlFilter())
            }
            CameraFilter.RED.name -> {
                var rgbFilter = GlRGBFilter()
                rgbFilter.setBlue(0f)
                rgbFilter.setGreen(0f)
                gpuCameraRecorder?.setFilter(rgbFilter)
            }
            CameraFilter.GREEN.name -> {
                var rgbFilter = GlRGBFilter()
                rgbFilter.setBlue(0f)
                rgbFilter.setRed(0f)
                gpuCameraRecorder?.setFilter(rgbFilter)
            }
            CameraFilter.BLUE.name -> {

                var rgbFilter = GlRGBFilter()
                rgbFilter.setRed(0f)
                rgbFilter.setGreen(0f)
                gpuCameraRecorder?.setFilter(rgbFilter)
            }
        }
    }
}