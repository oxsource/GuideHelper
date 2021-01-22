package com.pizzk.overlay.el

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.Space
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintSet
import com.pizzk.overlay.ConstraintPatch

class Marker(
    val id: Int,
    val anchor: Int
) {
    internal var delegate: Delegate = impl
    internal var view: View? = null

    interface Delegate {
        /**
         * 提供Marker视图
         */
        fun onMake(context: Context, marker: Marker): View?

        /**
         * Marker在OverlayLayout中的默认布局实现
         */
        fun onLayout(cs: ConstraintSet, marker: View, anchor: View)
    }

    open class DelegateImpl : Delegate {
        open val csPatch: ConstraintPatch by lazy { ConstraintPatch() }

        private var marker: View? = null

        override fun onMake(context: Context, marker: Marker): View? {
            val view: View? = marker.view
            if (null != view) return view
            return LayoutInflater.from(context).inflate(marker.id, null)
        }

        override fun onLayout(cs: ConstraintSet, marker: View, anchor: View) {
            this.marker = marker
            csPatch.with(cs, marker.id, anchor.id)
        }

        /**
         * 设置Marker中子元素控件点击事件
         */
        fun setClickListener(@IdRes vararg ids: Int, l: View.OnClickListener?) {
            val marker: View = marker ?: return
            ids.map { marker.findViewById<View>(it) }
                .filterNotNull()
                .forEach { it.setOnClickListener(l) }
        }

        /**
         * 设置父视图(OverlayLayout)点击事件
         */
        fun setParentClickListener(l: View.OnClickListener?) {
            val marker: View = marker ?: return
            val parent = marker.parent as? View ?: return
            parent.setOnClickListener(l)
        }
    }

    companion object {
        private val impl: Delegate by lazy { DelegateImpl() }

        fun iv(context: Context, @DrawableRes res: Int): View? {
            return try {
                val view = ImageView(context)
                view.setImageResource(res)
                view
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}