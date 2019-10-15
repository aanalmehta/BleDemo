package com.android.bledemo.base

import androidx.lifecycle.LifecycleObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class CustomCoroutineScope : LifecycleObserver, CoroutineScope {


    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    fun getCoroutineScope(): CoroutineScope {
        return this
    }

}