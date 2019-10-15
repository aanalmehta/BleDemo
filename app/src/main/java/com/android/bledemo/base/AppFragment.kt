package com.android.bledemo.base

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope


/**
 * Base class for all the fragments used, manages common feature needed in the most of the fragments
 */
abstract class AppFragment : Fragment() {

    protected fun getFragmentScope(fragment: Fragment): CoroutineScope {
        val localScopeApiHandle = CustomCoroutineScope()
        fragment.lifecycle.addObserver(localScopeApiHandle)
        return localScopeApiHandle.getCoroutineScope()
    }

}
