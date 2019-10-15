package com.android.bledemo.utils

import android.app.AlertDialog
import android.content.Context
import androidx.core.content.ContextCompat
import com.android.bledemo.R
import com.android.bledemo.appinterface.OkCancelDialogInterface
import com.android.bledemo.appinterface.OkDialogInterface


object DialogUtils {

    /**
     * Show Default dialog with ok and cancel two buttons.
     *
     * @param context Application/Activity Context for creating dialog.
     * @param message Message of dialog
     * @param callback Callback for button click of dialog
     */
    fun okCancelDialog(context: Context, message: String, titleYes: Int, titleNo: Int, callback: OkCancelDialogInterface) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(context.getString(R.string.app_name))
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(context.getString(titleYes)) { dialog, _ ->
            dialog.dismiss()
            callback.ok()
        }
        builder.setNegativeButton(context.getString(titleNo)) { dialog, _ ->
            dialog.dismiss()
            callback.cancel()
        }
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
        if (!dialog.isShowing)
            dialog.show()
    }

    /**
     * Show Default dialog.
     *
     * @param context Application/Activity Context for creating dialog.
     * @param title   Title of dialog
     * @param message Message of dialog
     * @param callback Callback for button click of dialog
     */
    fun okDialog(context: Context, title: String, message: String, callback: OkDialogInterface) {

        val builder = AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setCancelable(false)
        builder.setPositiveButton(context.getString(android.R.string.ok)) { dialog, _ ->
            dialog.dismiss()
            callback.ok()
        }
        val dialog = builder.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        if (!dialog.isShowing)
            dialog.show()
    }
}
