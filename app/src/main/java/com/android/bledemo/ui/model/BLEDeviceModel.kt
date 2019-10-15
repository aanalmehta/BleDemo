package com.android.bledemo.ui.model

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.graphics.drawable.Drawable
import android.os.Parcelable
import com.android.bledemo.utils.AppConstant
import com.android.bledemo.utils.CommandConstant
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize

data class BLEDeviceModel(val bleDevice: BluetoothDevice) : Parcelable {

    @IgnoredOnParcel
    var bluetoothGatt: BluetoothGatt? = null

    @IgnoredOnParcel
    var connectionState: String = AppConstant.BLE_STATE_CONNECT

    @IgnoredOnParcel
    val hashMapGattCharacteristic = HashMap<String, BluetoothGattCharacteristic>()

    @IgnoredOnParcel
    var selectedService: String = CommandConstant.BLE_SERVICE_UUID

    @IgnoredOnParcel
    var selectedCharacteristic: String = CommandConstant.BLE_CHARACTERISTIC_UUID

    @IgnoredOnParcel
    var manufacturerId: Int = 0

    @IgnoredOnParcel
    var imageManufacturer: Drawable? = null
}
