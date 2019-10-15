package com.android.bledemo.base

import com.android.bledemo.ui.appview.device.ScanDeviceFragment
import com.android.bledemo.ui.appview.command.HeartRateFragment

enum class AppFragmentState(fragmentClass: Class<out AppFragment>) {

    F_SCAN_DEVICE(ScanDeviceFragment::class.java),
    F_HEART_RATE(HeartRateFragment::class.java);

    var fragment: Class<out AppFragment>? = fragmentClass

    companion object {
        fun getValue(value: Class<*>): AppFragmentState {
            return values().firstOrNull { it.fragment == value }
                    ?: F_SCAN_DEVICE// not found
        }
    }

}
