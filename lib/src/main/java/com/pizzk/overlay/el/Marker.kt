package com.pizzk.overlay.el

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintSet

class Marker(
    val id: Int,
    val anchor: Int
) {
    internal var make: Make = delegateMake
    internal var layout: Layout = delegateLayout
    internal var view: View? = null

    interface Make {
        fun onMake(context: Context, marker: Marker): View?
    }

    interface Layout {
        fun onLayout(cs: ConstraintSet, marker: View, anchor: View)
    }

    /**
     * 提供Marker视图
     */
    open class MarkerMake : Make {
        override fun onMake(context: Context, marker: Marker): View? {
            val view: View? = marker.view
            if (null != view) return view
            return LayoutInflater.from(context).inflate(marker.id, null)
        }
    }

    /**
     * Marker在OverlayLayout中的默认布局实现
     */
    open class MarkerLayout : Layout {
        private var cs: ConstraintSet? = null
        private var marker: View? = null
        private var anchor: View? = null

        override fun onLayout(cs: ConstraintSet, marker: View, anchor: View) {
            this.cs = cs
            this.marker = marker
            this.anchor = anchor
        }

        /**
         * 约束布局connect方法封装
         */
        fun connect(startSide: Int, endSide: Int, margin: Int = 0) {
            val cs: ConstraintSet = cs ?: return
            val marker: View = marker ?: return
            val anchor: View = anchor ?: return
            cs.connect(marker.id, startSide, anchor.id, endSide, margin)
        }

        fun connect(side: Int, margin: Int = 0) {
            connect(side, side, margin)
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
        private val delegateLayout: Layout by lazy { MarkerLayout() }
        private val delegateMake: Make by lazy { MarkerMake() }

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