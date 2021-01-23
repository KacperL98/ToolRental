package com.kacper.itemxxx.cameraPermission

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.kacper.itemxxx.R
import com.kacper.itemxxx.helpers.toastCustom

class Permission : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permission)
        supportActionBar?.hide()

        checkForPermission()
    }
    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest
                .permission
                .CAMERA) == PackageManager.PERMISSION_GRANTED){
        }else
            requestThePermission()
    }
    private fun requestThePermission() {

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode== CAMERA_PERMISSION_REQUEST_CODE){

            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else if (isUserPermanentlyDenied()){
                showGoToAppSettingDialog()
            }else
                requestThePermission()
        }
    }
    private fun showGoToAppSettingDialog() {
        AlertDialog.Builder(this)
            .setTitle("Grant Permissions!!")
            .setMessage("We need camera permission to scan QR code. Go to App Setting and manage permission")
            .setPositiveButton("Grant"){_, _ ->
                goToAppSettings()

            }
            .setNegativeButton("Cancel"){ _, _ ->
                toastCustom("We need permission for start this Application.")
                finish()
            }.show()
    }

    private fun goToAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun isUserPermanentlyDenied(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA).not()
        } else {
            return false
        }
    }
    override fun onRestart() {
        super.onRestart()
        checkForPermission()
    }
    companion object{
        private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }
}