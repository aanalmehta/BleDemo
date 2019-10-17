package com.android.bledemo.ui.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.android.bledemo.R.id.btnScanStart
import com.android.bledemo.R.id.btnScanStop
import com.android.bledemo.R.id.btnServices
import com.android.bledemo.appinterface.CharacteristicsChangeInterface
import com.android.bledemo.ble.BLEAppInterface
import com.android.bledemo.ble.BLEManager
import com.android.bledemo.ui.model.BLEDeviceModel
import com.android.bledemo.utils.*
import com.android.bledemo.utils.CommandConstant.findUUID
import com.android.bledemo.utils.CommandConstant.getCharacteristicProperty
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class DeviceViewModel(application: Application) : AndroidViewModel(application), BLEAppInterface {
    val bleDeviceList = MutableLiveData<ArrayList<BLEDeviceModel>>()
    var bleManger: BLEManager? = null
    var isDeviceConnected = MutableLiveData<Boolean>()
    val commandResult = MutableLiveData<String>()
    val serviceFoundOfDevice: MutableLiveData<BLEDeviceModel> = MutableLiveData()
    var characteristicsChangeInterface: CharacteristicsChangeInterface? = null

    var connectedDevice: MutableLiveData<Pair<HashMap<String, BLEDeviceModel>, String>> = MutableLiveData()
    var selectedMacAddress = MutableLiveData<String>()
    var connectionState = MutableLiveData<String>()
    var listServiceCharacteristics = MutableLiveData<String>()

    init {
        bleManger = BLEManager()
        bleManger?.init(getApplication(), this)
        bleDeviceList.postValue(ArrayList())
        connectedDevice.postValue(Pair(HashMap(), ""))
        selectedMacAddress.postValue("")
    }

    fun stopScanningDevices() {
        bleManger?.stopScanDevices()
    }

    fun onClick(view: View) {
        when (view.id) {
            btnScanStart -> {
                bleManger?.startScanDevices(connectedDevices = connectedDevice.value?.first)
            }
            btnScanStop -> {
                stopScanningDevices()
            }
            btnServices -> {
                var list = ""
                val bleModel = serviceFoundOfDevice.value
                val servicesList = bleModel?.bluetoothGatt?.services
                for (service in servicesList!!) {
                    list += "Service: \n    UUID: ${service.uuid}\n " +
                            "   Name: ${findUUID(service.uuid.toString())}\n\n"
                    list += "Characteristics: \n"
                    for (characteristic in service.characteristics) {
                        list += "   UUID: ${characteristic.uuid} \n"
                        list += "   Name: ${findUUID(characteristic.uuid.toString())} \n"
                        list += "   Property: ${getCharacteristicProperty(characteristic.properties) }\n\n"
                    }
                    list += "\n \n"
                }
                /*bleModel.bluetoothGatt?.readCharacteristic(
                    servicesList[5].getCharacteristic(
                    UUID.fromString(CommandConstant.MANUFACTURER_NAME_CHARACTERISTIC_UUID)))*/
                Log.d("AANAL", "AANAL = $list")
                listServiceCharacteristics.postValue(list)
            }
        }
    }

    override fun addDevice(bleDeviceModel: BLEDeviceModel) {
        bleDeviceList.postValue(bleDeviceList.value)
    }

    /**
     * If device is added already then reconnect the same device using MAC Address
     */
    override fun deviceFound(device: BLEDeviceModel) {
        bleManger?.connectDevice(device)
        bleManger?.stopScanDevices()
    }

    /**
     *  Set connected device list
     */
    override fun setDeviceList(deviceList: ArrayList<BLEDeviceModel>) {
        bleDeviceList.postValue(deviceList)
    }

    /**
     *  Change device connect or disconnect state
     */
    override fun connectionStatus(bleDeviceModel: BLEDeviceModel, status: BLEManager.ConnectionStatus) {
        if (bleDeviceList.value!!.contains(bleDeviceModel)) {
            val index = bleDeviceList.value!!.indexOf(bleDeviceModel)
            if (index > -1) {
                val device = bleDeviceList.value!![index]
                device.connectionState = status.status
                bleDeviceList.postValue(bleDeviceList.value)

                Log.d("AANAL", "AANAL => ${bleDeviceModel.bluetoothGatt?.device?.name}")

                connectedDevice.value!!.first[bleDeviceModel.bluetoothGatt!!.device.address] = bleDeviceModel
                connectedDevice.postValue(Pair(connectedDevice.value!!.first, bleDeviceModel.bluetoothGatt!!.device.address))
            }
        }
        if (status.status == AppConstant.BLE_STATE_CONNECT || status.status == AppConstant.BLE_STATE_DISCONNECT) {
            ProgressUtils.getInstance(getApplication()).close()
        }
    }

    /*
    * Call when services discovered
    */
    override fun foundServices(device: BLEDeviceModel) {
        serviceFoundOfDevice.postValue(device)
    }

    override fun setCommandResult(value: String) {
        if (characteristicsChangeInterface != null) {
            characteristicsChangeInterface?.onChanged(value)
        }
    }

    override fun readCharacteristics(value: ByteArray?) {
        Log.d("AANAL", "AANAL => ${Arrays.toString(value)}")
    }
}