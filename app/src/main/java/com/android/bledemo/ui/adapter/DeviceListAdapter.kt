package com.android.bledemo.ui.adapter

import android.bluetooth.BluetoothGatt
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.android.bledemo.BR
import com.android.bledemo.R
import com.android.bledemo.base.AppFragmentState
import com.android.bledemo.ui.appview.MainActivity
import com.android.bledemo.ui.model.BLEDeviceModel
import com.android.bledemo.ui.viewmodel.DeviceViewModel
import com.android.bledemo.utils.AppConstant
import com.android.bledemo.utils.ProgressUtils
import kotlinx.android.synthetic.main.item_device_list.view.*
import java.util.*


class DeviceListAdapter(private val context: Context, private val deviceViewModel: DeviceViewModel) : RecyclerView.Adapter<DeviceListAdapter.ViewHolder>() {

    private val list: ArrayList<BLEDeviceModel> = ArrayList()

    fun setData(data: ArrayList<BLEDeviceModel>?) {
        if (data != null) {
            this.list.clear()
            this.list.addAll(data)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(LayoutInflater.from(parent.context), R.layout.item_device_list, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when {
            list[position].manufacturerId == 76 -> // Apple
                list[position].imageManufacturer = ContextCompat.getDrawable(context, R.drawable.ic_laptop_mac)
            list[position].manufacturerId == 65285 -> // HeartRate Device
                list[position].imageManufacturer = ContextCompat.getDrawable(context, R.drawable.ic_favorite)
            list[position].manufacturerId == 117 -> // Samsung Electronics
                list[position].imageManufacturer = ContextCompat.getDrawable(context, R.drawable.ic_bluetooth)
        }
        holder.bind(list[position])
        holder.itemView.btnConnectState.setOnClickListener {
            ProgressUtils.getInstance(context).show()
            if (list[position].connectionState != AppConstant.BLE_STATE_CONNECT) {
                deviceViewModel.bleManger?.disconnect(list[position])
            } else {
                deviceViewModel.bleManger?.connectDevice(list[position])
            }

        }
        holder.itemView.setOnClickListener {
            val device = list[position]
            if (device.connectionState == AppConstant.BLE_STATE_DISCONNECT) {
                device.bluetoothGatt?.requestMtu(35)
                device.bluetoothGatt?.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                val bundle = Bundle()
                bundle.putString(AppConstant.BUNDLE_FRAG_SERVICE_FROM, AppConstant.BUNDLE_FROM_DEVICE_SCAN)
                bundle.putParcelable(AppConstant.BUNDLE_BLE_DEVICE, device)
                (context as MainActivity).appFragmentManager?.addFragment(
                    AppFragmentState.F_HEART_RATE, bundle, false)
            }


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(device: BLEDeviceModel) {
            binding.setVariable(BR.device, device)
            binding.executePendingBindings()
        }
    }
}
