    package com.prestosa.permissionactivity

    import android.Manifest
    import android.app.Activity
    import android.content.Context
    import android.content.Intent
    import android.content.pm.PackageManager
    import android.location.LocationListener
    import android.location.LocationManager
    import android.os.Bundle
    import android.provider.MediaStore
    import android.widget.Toast
    import androidx.activity.result.contract.ActivityResultContracts
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.app.ActivityCompat
    import androidx.core.content.ContextCompat
    import com.prestosa.permissionactivity.databinding.ActivityPermissionHandlerBinding

    class PermissionHandlerActivity : AppCompatActivity() {
        private lateinit var binding: ActivityPermissionHandlerBinding
        private var locationListener: LocationListener? = null

        private val CAMERA_PERMISSION_REQUEST = 100
        private val LOCATION_PERMISSION_REQUEST = 101
        private val STORAGE_PERMISSION_REQUEST = 102

        private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
              Toast.makeText(this, "CAMERA SUCCESSFULLY LAUNCH.", Toast.LENGTH_LONG).show()
            }
            else
            {
                Toast.makeText(this, "CAMERA FAIL TO LAUNCH.", Toast.LENGTH_LONG).show()
            }

        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityPermissionHandlerBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.cameraButton.setOnClickListener {
                if (!checkPermission(Manifest.permission.CAMERA)) {
                    requestPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST)
                } else {
                    handleCameraPermissionGranted()
                }
            }

            // Implement similar logic for other buttons (Location and Storage)
            binding.locationButton.setOnClickListener {
                if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST)
                }
                else{
                    handleLocationPermissionGranted()
                }

            }

            binding.storageButton.setOnClickListener {
                if (!checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_REQUEST)
                }
                else{

                    accessStorage()
                }
            }
        }

        private fun checkPermission(permission: String): Boolean {
            return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        private fun requestPermission(permission: String, requestCode: Int) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }

        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)

            when (requestCode) {
                CAMERA_PERMISSION_REQUEST -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Camera Permission Granted!", Toast.LENGTH_LONG).show()
                    }

                }
                LOCATION_PERMISSION_REQUEST -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Location Permission Granted!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "To determine your location, location permission should be granted.", Toast.LENGTH_LONG).show()
                    }
                }
                STORAGE_PERMISSION_REQUEST -> {
                    if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Storage Permission Granted!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, "To access storage, storage permission should be granted.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        private fun handleCameraPermissionGranted() {
            if (!checkPermission(Manifest.permission.CAMERA)) {
                requestPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_REQUEST)
            } else {
                launchCamera()
            }
        }

        private fun launchCamera() {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                takePictureLauncher.launch(takePictureIntent)
            } else {
                Toast.makeText(this, "No camera app found!", Toast.LENGTH_LONG).show()
            }
        }


        private fun handleLocationPermissionGranted() {
            if (!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION_PERMISSION_REQUEST)
            } else {
                retrieveLocation()
            }
        }

        private fun retrieveLocation() {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationListener = LocationListener { location ->
                val latitude = location.latitude
                val longitude = location.longitude

                val locationString = "Latitude: $latitude, Longitude: $longitude"
                Toast.makeText(this, locationString, Toast.LENGTH_SHORT).show()
                locationListener?.let { locationManager.removeUpdates(it) }
            }

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            locationListener?.let { locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, it) }
        }



        private val fileManagerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedFileUri = result.data?.data
                // Handle the selected file from the result in the data intent
                if (selectedFileUri != null) {
                    Toast.makeText(this, "File Manager Successfully Launched.", Toast.LENGTH_LONG).show()
                }
            }
        }

        // Inside accessStorage method:
        private fun accessStorage() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // Set the MIME type or specific file types as needed
            fileManagerLauncher.launch(intent)
        }
    }
