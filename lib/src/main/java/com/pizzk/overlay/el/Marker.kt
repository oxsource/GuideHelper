package com.pizzk.overlay.el

import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.constraintlayout.widget.ConstraintSet

class Marker(
    @LayoutRes
    val id: Int,
    @IdRes
    val anchor: Int
) {
    internal var layout: Layout = delegateLayout

    interface Layout {
        fun onLayout(cs: ConstraintSet, marker: View, anchor: View)
    }

    /**
     * Marker在OverlayLayout中的默认布局实现
     */
    open class MarkerLayout : Layout {
        private var cs: ConstraintSet? = null
        private var marker: View? = null
        private var anchor: View? = null

        @CallSuper
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
    }
}