package com.android.bledemo.base

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

class DataBindingAdapter {
    companion object {
        @JvmStatic
        @BindingAdapter("setImage")
        fun setImage(view: ImageView, drawable: Drawable) {
            view.setImageDrawable(drawable)
        }
    }
}