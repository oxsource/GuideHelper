package com.pizzk.overlay.el

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes

/**
 * 锚点信息类
 */
class Anchor(
    @IdRes
    val id: Int,
    val radius: Int,
    val circle: Boolean,
    val outset: Int
) {
    internal var draw: Draw = delegateDraw
    internal var find: Find = delegateFind

    /**
     * 锚点绘制接口
     */
    interface Draw {
        fun onDraw(canvas: Canvas, paint: Paint, e: Anchor, rect: RectF)
    }

    /**
     * 锚点查找接口，默认使用findViewById，可自定义实现查找匹配
     */
    interface Find {
        fun onFind(parent: ViewGroup, @IdRes id: Int): View?
    }

    /**
     * 默认支持绘制圆形和矩形
     */
    open class AnchorDraw : Draw {

        override fun onDraw(canvas: Canvas, paint: Paint, e: Anchor, rect: RectF) {
            if (e.circle) {
                val radius: Float = kotlin.math.max(rect.width(), rect.height()) / 2
                val cx = rect.centerX()
                val cy = rect.centerY()
                rect.left = cx - radius
                rect.right = cx + radius
                rect.top = cy - radius
                rect.bottom = cy + radius
                canvas.drawCircle(cx, cy, radius, paint)
            } else {
                val radius: Float = e.radius.toFloat()
                canvas.drawRoundRect(rect, radius, radius, paint)
            }
        }
    }

    class AnchorFind : Find {
        override fun onFind(parent: ViewGroup, @IdRes id: Int): View? {
            return parent.findViewById(id)
        }
    }

    companion object {
        private val delegateDraw: Draw by lazy { AnchorDraw() }
        private val delegateFind: Find by lazy { AnchorFind() }

        /**
         * 构建矩形锚点
         */
        fun rect(@IdRes id: Int, radius: Int = 0, outset: Int = 0): Anchor {
            return Anchor(id, radius, circle = false, outset)
        }

        /**
         * 构建圆形锚点
         */
        fun circle(@IdRes id: Int, outset: Int = 0): Anchor {
            return Anchor(id, radius = 0, circle = true, outset)
        }
    }
}