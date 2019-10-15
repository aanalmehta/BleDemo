package com.android.bledemo.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.android.bledemo.R
import java.util.*

// Handling of fragment switch , adding fragment to stack or removing fragment from stack, setting top bar data

class AppFragmentManager(private val activity: AppCompatActivity, private val containerId: Int) {

    private val fragmentManager: FragmentManager = activity.supportFragmentManager
    private var ft: FragmentTransaction? = null

    private val stack = Stack<Fragment>()

    // Common Handling of top bar for all fragments like header name, icon on top bar in case of moving to other fragment and coming back again
    private fun setUp(currentState: AppFragmentState) = when (currentState) {

        AppFragmentState.F_SCAN_DEVICE -> {
            activity.supportActionBar?.title = activity.getString(R.string.title_scan_device)
            activity.supportActionBar?.setHomeButtonEnabled(false)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        }
        AppFragmentState.F_HEART_RATE -> {
            activity.supportActionBar?.setHomeButtonEnabled(true)
            activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            activity.supportActionBar?.title = activity.getString(R.string.title_command_list)
        }
    }

    // Call For Fragment Switch
    private fun <T> addFragmentInStack(fragmentEnum: AppFragmentState, keys: T?, isAnimation: Boolean) {
        ft = fragmentManager.beginTransaction()
        if (isAnimation) {
            ft?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
        }
        val fragment = Fragment.instantiate(this.activity, fragmentEnum.fragment?.name)
        if (keys != null && keys is Bundle) {
            fragment.arguments = keys
        }
        ft?.add(containerId, fragment, fragmentEnum.fragment?.name)
        if (!stack.isEmpty()) {
            stack.lastElement().onPause()
            ft?.hide(stack.lastElement())
        }
        stack.push(fragment)
        ft?.commitAllowingStateLoss()
        setUp(fragmentEnum)
    }

    fun getFragmentByID(): Fragment? {
        return fragmentManager.findFragmentById(containerId)
    }

    fun addFragment(fragmentEnum: AppFragmentState, keys: Any?, isAnimation: Boolean) {
//        val availableFragment = getFragmentByTag(fragmentEnum)
//        if (availableFragment != null) {
//            moveFragmentToTop(fragmentEnum, keys, isAnimation)
//        } else {
            addFragmentInStack(fragmentEnum, keys, isAnimation)
//        }
    }
}
