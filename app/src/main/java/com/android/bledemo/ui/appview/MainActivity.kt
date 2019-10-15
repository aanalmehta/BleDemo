package com.android.bledemo.ui.appview

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import com.android.bledemo.R
import com.android.bledemo.appinterface.OkCancelDialogInterface
import com.android.bledemo.base.AppFragmentState
import com.android.bledemo.base.BaseActivity
import com.android.bledemo.ui.viewmodel.DeviceViewModel
import com.android.bledemo.utils.DialogUtils
import com.android.bledemo.utils.PermissionUtils
import com.android.bledemo.utils.PermissionUtils.Companion.REQUEST_CODE_LOCATION
import com.android.bledemo.utils.isLocationEnable
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(), PermissionUtils.PermissionResult {

    var deviceViewModel: DeviceViewModel? = null

    override fun contentResId(): Int {
        return R.layout.activity_main
    }

    override fun onCreateView(savedInstanceState: Bundle?) {
        setSupportActionBar(toolbar)

        deviceViewModel = ViewModelProviders.of(this).get(DeviceViewModel::class.java)
        appFragmentManager?.addFragment(AppFragmentState.F_SCAN_DEVICE, null, false)
        checkPermission()
        checkLocationEnable()
    }

    private fun checkPermission() {
        permissionUtils?.setPermissionCallback(this)
        permissionUtils?.checkLocationPermission()
    }

    /**
     * Check location is enable or not. If not then open dialog of enable location.
     */
    private fun checkLocationEnable() {
        if (!isLocationEnable(this)) {
            DialogUtils.okCancelDialog(this, getString(R.string.alert_location), android.R.string.ok, android.R.string.cancel, object :
                OkCancelDialogInterface {
                override fun ok() {
                    val gpsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivityForResult(gpsIntent, REQUEST_CODE_LOCATION)
                }

                override fun cancel() {
                }
            })
        }
    }

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPermissionNoAskAgain() {
    }

    override fun onPermissionNotGranted(requestCode: Int) {
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtils?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Disconnect device
        if (deviceViewModel?.connectedDevice?.value != null)
            for (device in deviceViewModel!!.connectedDevice.value!!.first.values) {
                deviceViewModel?.bleManger?.disconnect(device)
            }
    }
}
