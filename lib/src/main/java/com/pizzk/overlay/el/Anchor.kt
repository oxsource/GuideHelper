package com.pizzk.overlay.el

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import com.pizzk.overlay.ConstraintPatch
import kotlin.math.max
import kotlin.math.min

/**
 * 锚点信息类
 */
class Anchor(
    val id: Int,
    val radius: Int,
    val circle: Boolean,
    val outset: Int
) {
    internal var delegate: Delegate = impl

    interface Delegate {
        /**
         * 锚点查找接口，默认使用findViewById，可自定义实现查找匹配
         */
        fun onFind(e: Anchor, parent: ViewGroup): View?

        /**
         * 锚点绘制接口，默认支持绘制圆形和矩形
         */
        fun onDraw(e: Anchor, canvas: Canvas, paint: Paint, rc: RectF)

        /**
         * 提供锚点视图
         */
        fun onMake(context: Context, e: Anchor): View

        /**
         * 伪造锚点布局
         */
        fun onLayout(view: View, cs: ConstraintSet, rc: RectF)
    }

    open class DelegateImpl : Delegate {
        open val csPatch: ConstraintPatch by lazy { ConstraintPatch() }

        override fun onFind(e: Anchor, parent: ViewGroup): View? {
            return parent.findViewById(e.id)
        }

        override fun onDraw(e: Anchor, canvas: Canvas, paint: Paint, rc: RectF) {
            if (e.circle) {
                val radius: Float = when {
                    e.radius < 0 -> min(rc.width(), rc.height()) / 2f
                    e.radius == 0 -> max(rc.width(), rc.height()) / 2f
                    else -> e.radius.toFloat()
                }
                val cx = rc.centerX()
                val cy = rc.centerY()
                rc.left = cx - radius
                rc.right = cx + radius
                rc.top = cy - radius
                rc.bottom = cy + radius
                canvas.drawCircle(cx, cy, radius, paint)
            } else {
                val radius: Float = e.radius.toFloat()
                canvas.drawRoundRect(rc, radius, radius, paint)
            }
        }

        override fun onMake(context: Context, e: Anchor): View = View(context)

        override fun onLayout(view: View, cs: ConstraintSet, rc: RectF) {
            val parent: View = view.parent as View
            val sMargin = rc.left.toInt()
            val bMargin = parent.measuredHeight - rc.bottom.toInt()
            csPatch.with(cs, view.id, ConstraintSet.PARENT_ID)
                .connect(ConstraintSet.START, margin = sMargin)
                .connect(ConstraintSet.BOTTOM, margin = bMargin)
        }
    }

    companion object {
        private val impl: Delegate by lazy { DelegateImpl() }

        fun rect(id: Int, radius: Int = 0, outset: Int = 0): Anchor {
            return Anchor(id, radius, circle = false, outset = outset)
        }

        fun circle(id: Int, radius: Int = 0, outset: Int = 0): Anchor {
            return Anchor(id, radius, circle = true, outset = outset)
        }
    }
}