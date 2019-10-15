package com.android.bledemo.utils

import android.widget.Toast
import androidx.annotation.IntDef
import androidx.annotation.StringRes
import com.android.bledemo.App

object ToastUtils {

    private fun shortToast(text: String) {
        show(text, Toast.LENGTH_SHORT)
    }

    fun longToast(text: String) {
        show(text, Toast.LENGTH_LONG)
    }

    private fun makeToast(text: String, @ToastLength length: Int): Toast {
        return Toast.makeText(App.instance!!, text, length)
    }

    private fun show(text: String, @ToastLength length: Int) {
        makeToast(text, length).show()
    }

    @IntDef(Toast.LENGTH_LONG, Toast.LENGTH_SHORT)
    private annotation class ToastLength
}