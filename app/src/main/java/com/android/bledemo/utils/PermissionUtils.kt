package com.android.bledemo.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class PermissionUtils(private val context: Context) : ActivityCompat.OnRequestPermissionsResultCallback {
    private var permissionGranted: PermissionResult? = null


    /**
     * Add Multiple Permission in list and pass in the multi check permission dialog
     * @param permissionList list of permission
     */
    private fun checkPermissions(permissionList: ArrayList<String>): Array<String?> {
        val arrPermission = ArrayList<String>()

        for (i in permissionList.indices) {
            val permission = permissionList[i]
            if (ContextCompat.checkSelfPermission(context,
                            permission) != PackageManager.PERMISSION_GRANTED) {
                arrPermission.add(permission)
            }
        }
        return arrPermission.toTypedArray()
    }


    /**
     * Call where multiple permission require and only pass arrayList of permission.
     *
     * @param permissionList list of permission
     * @param requestCode    Call Back for permission granted or not.
     * @return is permission granted or not.
     */
    private fun checkMultiplePermissions(permissionList: ArrayList<String>, requestCode: Int): Boolean {
        val arrPermission = checkPermissions(permissionList)
        if (arrPermission.isNotEmpty()) {
            ActivityCompat.requestPermissions(context as Activity,
                    arrPermission,
                    requestCode)
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        var isGrantAllPermission = false
        var isNeverAskDialogClick = false
        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                isGrantAllPermission = true
            }
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, permissions[i])) {
                isNeverAskDialogClick = true
            }
        }

        if (!isGrantAllPermission) {
            permissionGranted?.onPermissionGranted(requestCode)
        } else if (isNeverAskDialogClick) {
            permissionGranted?.onPermissionNoAskAgain()
        } else {
            permissionGranted?.onPermissionNotGranted(requestCode)
        }
    }


    /**
     * Set the interface where callback require.
     * @param permissionGranted This is callback interface.
     */
    fun setPermissionCallback(permissionGranted: PermissionResult) {
        this.permissionGranted = permissionGranted
    }

    /**
     * This is callback interface.
     */
    interface PermissionResult {
        fun onPermissionGranted(requestCode: Int)
        fun onPermissionNoAskAgain()
        fun onPermissionNotGranted(requestCode: Int)
    }


    fun checkLocationPermission(): Boolean {
        val permissionList = arrayListOf(FINE_LOCATION, COARSE_LOCATION)
        return checkMultiplePermissions(permissionList, REQUEST_CODE_LOCATION)
    }

    companion object {
        const val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
        const val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION

        const val REQUEST_CODE_LOCATION = 10003
    }

}