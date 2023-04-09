package id.niteroomcreation.mncvideorecordfilter.presentation.base

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.daasuu.gpuv.camerarecorder.CameraRecordListener
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorderBuilder
import com.daasuu.gpuv.camerarecorder.LensFacing
import id.niteroomcreation.mncvideorecordfilter.databinding.AMainBinding
import id.niteroomcreation.mncvideorecordfilter.presentation.MainActivity
import id.niteroomcreation.mncvideorecordfilter.presentation.custom.CameraView
import id.niteroomcreation.mncvideorecordfilter.util.LogHelper
import kotlinx.coroutines.Runnable

/**
 * Created by Septian Adi Wijaya on 09/04/2023.
 * please be sure to add credential if you use people's code
 */
open class BaseCamera : AppCompatActivity() {

    companion object {
        val TAG = BaseCamera::class.simpleName
    }

    lateinit var pref: SharedPreferences

    private lateinit var binding: AMainBinding

    private var cameraView: CameraView? = null
    private var gpuCameraRecorder: GPUCameraRecorder? = null
    private var lensFacing: LensFacing = LensFacing.BACK
    private var toggleSwitchCameraFace: Boolean = false

    var permissionGranted: Boolean = false


    protected var cameraWidth = 1280
    protected var cameraHeight = 720
    protected var videoWidth = 720
    protected var videoHeight = 720

    fun onCreateActivity(binding: AMainBinding) {
        this.binding = binding

        supportActionBar?.hide()

        binding.actionRecord.setOnClickListener { view ->
            LogHelper.e(TAG, "record")
        }

        binding.actionCaptureImage.setOnClickListener { view ->
            LogHelper.e(TAG, "capture image")
        }

        binding.actionCameraFaceSwitch.setOnClickListener { view ->
            LogHelper.e(TAG, "switch camera")
            //release camera each time switch face
            releaseCamera()

            if (lensFacing == LensFacing.BACK) {
                lensFacing = LensFacing.FRONT
            } else {
                lensFacing = LensFacing.BACK
            }

            toggleSwitchCameraFace = true
        }

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (!pref.getBoolean("permission", false))
            checkPermission()
        else setupCamera()
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        // request camera permission if it has not been grunted.
        // request camera permission if it has not been grunted.
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), MainActivity.CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }

    fun setupCamera() {
        runOnUiThread(object : Runnable {
            override fun run() {
                binding.wrapView.removeAllViews()
                cameraView = CameraView(applicationContext)
                cameraView?.setListener(object : CameraView.TouchListener {
                    override fun onTouch(event: MotionEvent, w: Int, h: Int) {
                        if (gpuCameraRecorder == null) return
                        gpuCameraRecorder?.changeManualFocusPoint(event.x, event.y, w, h)
                    }
                })
                binding.wrapView.addView(cameraView)
            }
        })

        gpuCameraRecorder = GPUCameraRecorderBuilder(this, cameraView)
            .cameraRecordListener(object : CameraRecordListener {
                override fun onGetFlashSupport(flashSupport: Boolean) {
                    LogHelper.e(TAG, "is flash supported on device? $flashSupport")

                    //result check for flash is support or not,
                }

                override fun onRecordComplete() {
                    LogHelper.e(TAG, "record already completed")

                    //what to do when record completed

                }

                override fun onRecordStart() {
                    LogHelper.e(TAG, "record start")

                    runOnUiThread {
                        binding.rvListFilter.visibility = View.GONE
                    }
                }

                override fun onError(exception: Exception) {
                    LogHelper.e(TAG, exception.message)
                }

                override fun onCameraThreadFinish() {
                    if (toggleSwitchCameraFace)
                        runOnUiThread {
                            setupCamera()
                        }
                    toggleSwitchCameraFace = true
                }

                override fun onVideoFileReady() {
                    LogHelper.e(TAG, "video file already ready a.k.a stored on storage")
                }

            })
            .videoSize(videoWidth, videoHeight)
            .cameraSize(cameraWidth, cameraHeight)
            .lensFacing(lensFacing)
            .build()
    }

    override fun onStop() {
        super.onStop()
        releaseCamera()
    }

    private fun releaseCamera() {
        if (cameraView != null)
            cameraView?.onPause()

        if (gpuCameraRecorder != null) {
            gpuCameraRecorder?.stop()
            gpuCameraRecorder?.release()
            gpuCameraRecorder = null
        }

        if (cameraView != null) {
            binding.wrapView.removeView(cameraView)
            cameraView = null
        }
    }
}