package com.pizzk.overlay.el

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.annotation.IdRes

/**
 * 锚点信息类
 */
class Anchor(
    @IdRes
    val id: Int,
    val radius: Int,
    val circle: Boolean,
    val inset: Int
) {
    var draw: Draw = delegateDraw

    /**
     * 锚点绘制接口
     */
    interface Draw {
        fun onDraw(canvas: Canvas, paint: Paint, e: Anchor, rect: RectF)
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

    companion object {
        private val delegateDraw: Draw by lazy { AnchorDraw() }

        /**
         * 构建矩形锚点
         */
        fun rect(@IdRes id: Int, radius: Int = 0, inset: Int = 0): Anchor {
            return Anchor(id, radius, circle = false, inset)
        }

        /**
         * 构建圆形锚点
         */
        fun circle(@IdRes id: Int, inset: Int = 0): Anchor {
            return Anchor(id, radius = 0, circle = true, inset)
        }
    }
}