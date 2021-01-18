package com.pizzk.overlay

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.toRect
import com.pizzk.overlay.el.Anchor
import com.pizzk.overlay.el.Marker
import com.pizzk.overlay.el.Overlay

/**
 * overlay承载显示布局
 */
class OverlayLayout : ConstraintLayout {
    private val paint: Paint = Paint()
    private val rect = RectF()
    private var maskColor: Int = Color.TRANSPARENT
    private var bitmap: Bitmap? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        paint.color = Color.TRANSPARENT
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        paint.flags = Paint.ANTI_ALIAS_FLAG
        setWillNotDraw(false)
        isClickable = true
        setMaskColor(R.color.overlay_mask)
    }

    fun setMaskColor(@ColorRes colorId: Int) {
        maskColor = ContextCompat.getColor(context, colorId)
    }

    fun setOverlay(overlay: Overlay?) {
        removeAllViews()
        setOnClickListener(null)
        overlay ?: return
        val view: ViewGroup = parent as? ViewGroup ?: return
        //@formatter:off
        post { try { onChangeLayout(view, overlay) } catch (e: Exception) { e.printStackTrace() } }
        //@formatter:on
    }

    fun setVisibility(v: Boolean) {
        if (v == (View.VISIBLE == visibility)) return
        visibility = if (v) View.VISIBLE else View.GONE
    }

    private fun onChangeLayout(viewGroup: ViewGroup, overlay: Overlay) {
        //准备画布
        bitmap = bitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bmp = bitmap ?: return
        bmp.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(bmp)
        canvas.drawColor(maskColor)
        //计算位置
        val vXY = IntArray(2)
        getLocationOnScreen(vXY)
        val vAnchorXY = IntArray(2)
        overlay.anchors.forEach { e: Anchor ->
            val vAnchor: View = e.find.onFind(viewGroup, e) ?: return@forEach
            //计算宽度及位置
            vAnchor.getLocationOnScreen(vAnchorXY)
            rect.left = vAnchorXY[0] - vXY[0] - 0f
            rect.top = vAnchorXY[1] - vXY[1] - 0f
            rect.right = rect.left + vAnchor.width
            rect.bottom = rect.top + vAnchor.height
            //镂空样式
            val outset = e.outset.toFloat()
            rect.inset(-outset, -outset)
            //锚点生产及绘制
            e.draw.onDraw(canvas, paint, e, rect)
            val anchor = onFakeAnchor(e.id, rect)
            //标记层布局
            val markers = overlay.markers.filter { it.anchor == e.id }
            markers.forEach { onLayoutMarker(viewGroup.context, it, anchor) }
        }
    }

    private fun onFakeAnchor(id: Int, rc: RectF): View {
        val v: View? = getViewById(id)
        if (null != v) return v
        val view = View(context)
        view.id = id
        addView(view, rc.width().toInt(), rc.height().toInt())
        val cs = ConstraintSet()
        cs.clone(this)
        val iid = ConstraintSet.PARENT_ID
        cs.connect(id, ConstraintSet.START, iid, ConstraintSet.START, rc.left.toInt())
        cs.connect(id, ConstraintSet.TOP, iid, ConstraintSet.TOP, rc.top.toInt())
        cs.applyTo(this)
        return view
    }

    private fun onLayoutMarker(context: Context, marker: Marker, anchor: View) {
        val v: View = marker.make.onMake(context, marker) ?: return
        if (v.id <= 0) v.id = marker.id + marker.hashCode()
        addView(v)
        val cs = ConstraintSet()
        cs.clone(this)
        marker.layout.onLayout(cs, v, anchor)
        cs.applyTo(this)
    }

    override fun onDraw(canvas: Canvas?) {
        bitmap?.let { canvas?.drawBitmap(it, 0f, 0f, null) }
        super.onDraw(canvas)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        bitmap?.recycle()
        bitmap = null
    }
}