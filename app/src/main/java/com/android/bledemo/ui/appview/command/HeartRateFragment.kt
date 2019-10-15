package com.android.bledemo.ui.appview.command

import android.bluetooth.BluetoothGattDescriptor
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.android.bledemo.R
import com.android.bledemo.appinterface.CharacteristicsChangeInterface
import com.android.bledemo.base.AppFragment
import com.android.bledemo.databinding.FragmentHeartRateBinding
import com.android.bledemo.ui.appview.MainActivity
import com.android.bledemo.ui.model.BLEDeviceModel
import com.android.bledemo.ui.viewmodel.DeviceViewModel
import com.android.bledemo.utils.AppConstant
import com.android.bledemo.utils.AppConstant.BLE_DESCRIPTOR_WRITE_DELAY
import com.android.bledemo.utils.ProgressUtils
import kotlinx.android.synthetic.main.fragment_heart_rate.*
import kotlinx.coroutines.CoroutineScope



class HeartRateFragment : AppFragment(), CharacteristicsChangeInterface {

    private var animation: Animation? = null
    private var deviceViewModel: DeviceViewModel? = null
    private var bleDevice: BLEDeviceModel? = null
    private var connectHandler: Handler? = null
    private var fragmentScope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        connectHandler = Handler(Looper.getMainLooper())
        animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        deviceViewModel = (activity as MainActivity).deviceViewModel
        deviceViewModel!!.characteristicsChangeInterface = this
        val binding: FragmentHeartRateBinding = DataBindingUtil.inflate(LayoutInflater.from(container!!.context), R.layout.fragment_heart_rate, container, false)
        binding.lifecycleOwner = this
        binding.deviceModel = deviceViewModel

        deviceViewModel?.commandResult?.postValue("-")

        if (arguments != null) {
            bleDevice = arguments!!.getParcelable(AppConstant.BUNDLE_BLE_DEVICE)
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentScope = getFragmentScope(this)

        deviceViewModel?.commandResult?.observe(this, Observer {

        })

        deviceViewModel?.bleDeviceList?.observe(this, Observer {

        })

        deviceViewModel?.connectedDevice?.observe(this, Observer {
            val device: BLEDeviceModel? = it!!.first[it.second]
            if (device != null) {
                // called when device is disconnected
                if (device.connectionState == AppConstant.BLE_STATE_CONNECT) {

                    if (deviceViewModel!!.selectedMacAddress.value == "") {
                        var noConnected = true
                        for (deviceModel in it.first.values) {
                            if (deviceModel.connectionState == AppConstant.BLE_STATE_DISCONNECT) {
                                noConnected = true
                                break
                            }
                        }
                        if (noConnected) {
                            stopAnimation()
                            deviceViewModel?.isDeviceConnected?.postValue(false)
                            deviceViewModel?.connectionState?.postValue(getString(R.string.lbl_device_disconnected, device.bluetoothGatt?.device?.name))
                            deviceViewModel?.commandResult?.postValue("-")
                            deviceViewModel?.bleManger?.startScanDevices()
                        }
                    } else {
                        if (it.second == deviceViewModel?.selectedMacAddress?.value) {
                            stopAnimation()
                            deviceViewModel?.isDeviceConnected?.postValue(false)
                            deviceViewModel?.connectionState?.postValue(getString(R.string.lbl_device_disconnected, device.bluetoothGatt?.device?.name))
                            deviceViewModel?.commandResult?.postValue("-")
                            deviceViewModel?.bleManger?.startScanDevices()
                        }
                    }
                    ProgressUtils.getInstance(activity!!).close()
                    // called when device is connected
                } else if (device.connectionState == AppConstant.BLE_STATE_DISCONNECT) {
                    Log.d("AANAL", "AANAL ${device.selectedCharacteristic}")
                    if (device.selectedCharacteristic != "") {
                        if (device.hashMapGattCharacteristic[device.selectedCharacteristic]!!.descriptors.size != 0) {
                            Handler().postDelayed({
                                if (context != null) {
                                    for (descriptor in device.hashMapGattCharacteristic[device.selectedCharacteristic]!!.descriptors) {
                                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                                        device.bluetoothGatt?.writeDescriptor(descriptor)
                                        deviceViewModel?.isDeviceConnected?.postValue(true)
                                        startAnimation()
                                        deviceViewModel?.connectionState?.postValue(getString(R.string.lbl_device_connected, device.bluetoothGatt?.device?.name))
                                        ProgressUtils.getInstance(activity!!).close()
                                    }
                                }
                            }, BLE_DESCRIPTOR_WRITE_DELAY)
                        } else {
                            ProgressUtils.getInstance(activity!!).close()
                        }
                    } else {
                        ProgressUtils.getInstance(activity!!).close()
                    }
                }
            }
        })
    }

    override fun onChanged(value: String) {
        activity?.runOnUiThread {
            deviceViewModel?.commandResult?.value = value
        }
    }

    private fun startAnimation() {
        animation?.repeatCount = Animation.INFINITE
        ivHeartRate.startAnimation(animation)
    }

    private fun stopAnimation() {
        ivHeartRate.clearAnimation()
    }
}

