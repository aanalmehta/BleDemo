package com.android.bledemo.utils

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import com.android.bledemo.R


class ProgressUtils {

    fun show() {
        if (progressDialog == null || progressDialog?.isShowing == false) {
            progressDialog = ProgressDialog(context)
            progressDialog?.setCancelable(false)
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.isIndeterminate = true
            progressDialog?.setCanceledOnTouchOutside(false)
            progressDialog?.setProgressStyle(android.R.attr.progressBarStyleSmall)
            progressDialog?.show()
            progressDialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            progressDialog?.setContentView(R.layout.layout_progressdialog)
        }
    }

    fun close() {
        try {
            if (progressDialog != null && progressDialog?.isShowing == true) {
                progressDialog?.dismiss()
                progressDialog = null
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            progressDialog = null
        }
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null
        @SuppressLint("StaticFieldLeak")
        private var instance: ProgressUtils? = null
        private var progressDialog: ProgressDialog? = null

        fun getInstance(ctx: Context): ProgressUtils {

            context = ctx

            if (instance == null) {
                instance = ProgressUtils()
            }

            return instance as ProgressUtils
        }
    }


}
