package id.niteroomcreation.mncvideorecordfilter.presentation.camera

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.niteroomcreation.mncvideorecordfilter.databinding.AMainBinding
import id.niteroomcreation.mncvideorecordfilter.presentation.base.BaseCamera
import id.niteroomcreation.mncvideorecordfilter.util.Constant

class CameraActivity : BaseCamera() {

    companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 88888
    }

    private lateinit var binding: AMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = PreferenceManager.getDefaultSharedPreferences(this)

        onCreateActivity(binding)

        videoWidth = 720
        videoHeight = 1280
        cameraWidth = 1280
        cameraHeight = 720
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(
                        binding.root,
                        "permission has been granted.",
                        Snackbar.LENGTH_LONG
                    ).show()

                    pref.edit().putBoolean(Constant.PERMISSION_KEY, true).apply()
                    setupCamera()
                } else {
                    Snackbar.make(
                        binding.root,
                        "[WARN] permission is not granted.",
                        Snackbar.LENGTH_SHORT
                    ).show()

                    pref.edit().putBoolean(Constant.PERMISSION_KEY, false).apply()
                }
            }
        }
    }
}