package id.niteroomcreation.mncvideorecordfilter.presentation.base

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.daasuu.gpuv.camerarecorder.CameraRecordListener
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorder
import com.daasuu.gpuv.camerarecorder.GPUCameraRecorderBuilder
import com.daasuu.gpuv.camerarecorder.LensFacing
import com.daasuu.gpuv.egl.filter.GlFilter
import com.daasuu.gpuv.egl.filter.GlRGBFilter
import com.google.android.material.snackbar.Snackbar
import id.niteroomcreation.mncvideorecordfilter.databinding.AMainBinding
import id.niteroomcreation.mncvideorecordfilter.presentation.camera.CameraActivity
import id.niteroomcreation.mncvideorecordfilter.presentation.camera.CameraViewModel
import id.niteroomcreation.mncvideorecordfilter.presentation.camera.FilterAdapter
import id.niteroomcreation.mncvideorecordfilter.presentation.camera.FilterListener
import id.niteroomcreation.mncvideorecordfilter.presentation.custom.CameraView
import id.niteroomcreation.mncvideorecordfilter.util.CommonUtil
import id.niteroomcreation.mncvideorecordfilter.util.Constant
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
    private val viewModel: CameraViewModel by viewModels()
    private lateinit var filePath: String

    private var cameraView: CameraView? = null
    private var gpuCameraRecorder: GPUCameraRecorder? = null
    private var lensFacing: LensFacing = LensFacing.BACK
    private var toggleSwitchCameraFace: Boolean = false

    protected var cameraWidth = 1280
    protected var cameraHeight = 720
    protected var videoWidth = 720
    protected var videoHeight = 1280

    fun onCreateActivity(binding: AMainBinding) {
        this.binding = binding

        supportActionBar?.hide()

        binding.actionRecord.setOnClickListener { view ->

            if (binding.actionRecord
                    .text
                    .toString()
                    .lowercase()
                    .contains("record")
            ) {
                //begin record
                //provide file path to save video record
                filePath = CommonUtil.provideFilePath()

                LogHelper.e(TAG, "start record on path $filePath")

                //start record using gpucamerarecorder on specific path
                gpuCameraRecorder?.start(filePath)

                if (gpuCameraRecorder?.isStarted == true) {

                    binding.actionRecord.text = "stop"
                    binding.rvListFilter.visibility = View.GONE
                    binding.actionRecord.backgroundTintList =
                        ContextCompat.getColorStateList(this, android.R.color.holo_orange_light)
                } else
                    Snackbar.make(
                        binding.root,
                        "Recording doesn't started yet!, Internal Error",
                        Snackbar.LENGTH_LONG
                    ).show()
            } else {

                LogHelper.e(TAG, "done recorded stored on path $filePath")

                //stop record
                if (gpuCameraRecorder?.isStarted == true) {
                    gpuCameraRecorder?.stop()
                    binding.actionRecord.text = "Record"
                    binding.rvListFilter.visibility = View.VISIBLE
                    binding.actionRecord.backgroundTintList =
                        ContextCompat.getColorStateList(this, android.R.color.transparent)

                } else
                    Snackbar.make(
                        binding.root,
                        "Recording doesn't started yet!, Internal Error",
                        Snackbar.LENGTH_LONG
                    ).show()
            }
        }

        /*binding.actionCaptureImage.setOnClickListener { view ->
            LogHelper.e(TAG, "capture image")
        }

        binding.actionCameraFaceSwitch.setOnClickListener { view ->
            //release camera each time switch face
            releaseCamera()

            if (lensFacing == LensFacing.BACK) {
                lensFacing = LensFacing.FRONT
            } else {
                lensFacing = LensFacing.BACK
            }

            toggleSwitchCameraFace = true
        }*/

        setupAdapter()

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

    private fun setupAdapter() {

        viewModel.state.observe(this, Observer {
            it.let {
                var adapter = FilterAdapter(it, object : FilterListener {
                    override fun onFilterClicked(filter: String) {
                        when (filter.lowercase()) {
                            "normal" -> {
                                gpuCameraRecorder?.setFilter(GlFilter())
                            }
                            "red" -> {
                                var rgbFilter = GlRGBFilter()
                                rgbFilter.setBlue(0f)
                                rgbFilter.setGreen(0f)
                                gpuCameraRecorder?.setFilter(rgbFilter)
                            }
                            "green" -> {
                                var rgbFilter = GlRGBFilter()
                                rgbFilter.setBlue(0f)
                                rgbFilter.setRed(0f)
                                gpuCameraRecorder?.setFilter(rgbFilter)
                            }
                            "blue" -> {

                                var rgbFilter = GlRGBFilter()
                                rgbFilter.setRed(0f)
                                rgbFilter.setGreen(0f)
                                gpuCameraRecorder?.setFilter(rgbFilter)
                            }
                        }
                    }
                })

                binding.rvListFilter.adapter = adapter
                binding.rvListFilter.layoutManager = LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            }

        });

    }

    override fun onResume() {
        super.onResume()

        if (!pref.getBoolean(Constant.PERMISSION_KEY, false))
            checkPermission()
        else setupCamera()
    }

    private fun checkPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), CameraActivity.CAMERA_PERMISSION_REQUEST_CODE
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
                    CommonUtil.storedOnGallery(context = this@BaseCamera, filePath = filePath)
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
//            .filter(GlFilter())
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