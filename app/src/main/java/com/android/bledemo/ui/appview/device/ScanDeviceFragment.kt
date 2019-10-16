package com.android.bledemo.ui.appview.device

import android.bluetooth.BluetoothGatt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.android.bledemo.R
import com.android.bledemo.base.AppFragment
import com.android.bledemo.base.AppFragmentState
import com.android.bledemo.databinding.FragmentScanDeviceBinding
import com.android.bledemo.ui.adapter.DeviceListAdapter
import com.android.bledemo.ui.appview.MainActivity
import com.android.bledemo.ui.model.BLEDeviceModel
import com.android.bledemo.ui.viewmodel.DeviceViewModel
import com.android.bledemo.utils.AppConstant
import com.android.bledemo.utils.CommandConstant
import kotlinx.android.synthetic.main.fragment_scan_device.*


class ScanDeviceFragment : AppFragment() {
    private var deviceViewModel: DeviceViewModel? = null
    private var adapter: DeviceListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        deviceViewModel = (activity as MainActivity).deviceViewModel
        val binding: FragmentScanDeviceBinding = DataBindingUtil.inflate(LayoutInflater.from(container!!.context), R.layout.fragment_scan_device, container, false)
        binding.lifecycleOwner = this
        binding.deviceModel = deviceViewModel

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        deviceViewModel?.bleDeviceList?.observe(this, Observer<ArrayList<BLEDeviceModel>> {
            adapter?.setData(it)
        })

        deviceViewModel?.serviceFoundOfDevice?.observe(this, Observer<BLEDeviceModel> {
            val fragment = (activity as MainActivity).appFragmentManager?.getFragmentByID()
            if (fragment is ScanDeviceFragment && it!!.connectionState == AppConstant.BLE_STATE_DISCONNECT) {
                it.bluetoothGatt?.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                val bundle = Bundle()
                bundle.putString(AppConstant.BUNDLE_FRAG_SERVICE_FROM, AppConstant.BUNDLE_FROM_DEVICE_SCAN)
                bundle.putParcelable(AppConstant.BUNDLE_BLE_DEVICE, it)

                it.selectedService = CommandConstant.HEART_RATE_SERVICE_UUID
                it.selectedCharacteristic = CommandConstant.HEART_RATE_CHARACTERISTIC_UUID
                (context as MainActivity).appFragmentManager?.addFragment(
                    AppFragmentState.F_HEART_RATE, bundle, false)

                deviceViewModel?.stopScanningDevices()
            }
        })
        setListData()

    }

    private fun setListData() {
        adapter = DeviceListAdapter(activity!!, deviceViewModel!!)
        rcvDeviceList.setHasFixedSize(true)
        rcvDeviceList.adapter = adapter
    }

}