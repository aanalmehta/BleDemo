package com.android.bledemo.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.bledemo.R
import com.android.bledemo.utils.PermissionUtils


abstract class BaseActivity : AppCompatActivity() {
    var permissionUtils: PermissionUtils? = null
        private set

    var appFragmentManager: AppFragmentManager? = null
        private set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(contentResId())

        permissionUtils = PermissionUtils(this)
        appFragmentManager = AppFragmentManager(this, containerId = R.id.container)

         onCreateView(savedInstanceState)
    }

    protected abstract fun contentResId(): Int
    protected abstract fun onCreateView(savedInstanceState: Bundle?)
}
