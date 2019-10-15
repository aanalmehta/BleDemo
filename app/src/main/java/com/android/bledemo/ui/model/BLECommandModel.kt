package com.android.bledemo.ui.model


import com.android.bledemo.ble.BLECommandManager

class BLECommandModel(
    var bytes: ByteArray?,
    var characteristicType: BLECommandManager.CharacteristicType?
)
