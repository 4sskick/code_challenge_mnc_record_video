package id.niteroomcreation.mncvideorecordfilter.presentation

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import id.niteroomcreation.mncvideorecordfilter.databinding.AMainBinding
import id.niteroomcreation.mncvideorecordfilter.presentation.base.BaseCamera

@AndroidEntryPoint
class MainActivity : BaseCamera() {

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
                    Toast.makeText(
                        this@MainActivity,
                        "permission has been granted.",
                        Toast.LENGTH_SHORT
                    ).show()

                    permissionGranted = true
                    pref.edit().putBoolean("permission", permissionGranted).apply()

                    setupCamera()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "[WARN] permission is not granted.",
                        Toast.LENGTH_SHORT
                    ).show()

                    permissionGranted = false
                    pref.edit().putBoolean("permission", permissionGranted).apply()
                }
            }
        }
    }
}