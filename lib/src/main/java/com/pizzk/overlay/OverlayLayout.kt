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
        setVisibility(null != overlay)
        overlay ?: return
        val view: ViewGroup = parent as? ViewGroup ?: return
        //@formatter:off
        post { try { react(view, overlay) } catch (e: Exception) { e.printStackTrace() } }
        //@formatter:on
    }

    private fun setVisibility(v: Boolean) {
        if (v == (View.VISIBLE == visibility)) return
        visibility = if (v) View.VISIBLE else View.GONE
    }

    private fun react(viewGroup: ViewGroup, overlay: Overlay) {
        //准备画布
        bitmap = bitmap ?: Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val bmp = bitmap ?: return
        bmp.eraseColor(Color.TRANSPARENT)
        val canvas = Canvas(bmp)
        canvas.drawColor(maskColor)
        //计算位置
        val xy = IntArray(2)
        getLocationOnScreen(xy)
        val left = xy[0].toFloat()
        val top = xy[1].toFloat()
        overlay.anchors.forEach { e: Anchor ->
            val vFind: View = e.delegate.onFind(e, viewGroup) ?: return@forEach
            //计算宽度及位置
            vFind.getLocationOnScreen(xy)
            rect.left = xy[0] - left
            rect.top = xy[1] - top
            rect.right = rect.left + vFind.width
            rect.bottom = rect.top + vFind.height
            //镂空样式
            val outset = e.outset.toFloat()
            rect.inset(-outset, -outset)
            //锚点生产及绘制
            e.delegate.onDraw(e, canvas, paint, rect)
            val vAnchor = onSetupAnchor(e, rect)
            //标记层布局
            val markers = overlay.markers.filter { it.anchor == e.id }
            markers.forEach { marker: Marker -> onSetupMarker(marker, vAnchor) }
        }
    }

    private fun onSetupAnchor(e: Anchor, rc: RectF): View {
        val id = e.id + e.hashCode()
        val vExist: View? = getViewById(id)
        if (null != vExist) return vExist
        val view = e.delegate.onMake(context, e)
        view.id = id
        addView(view, rc.width().toInt(), rc.height().toInt())
        val cs = ConstraintSet()
        cs.clone(this)
        e.delegate.onLayout(view, cs, rc)
        cs.applyTo(this)
        return view
    }

    private fun onSetupMarker(e: Marker, vAnchor: View) {
        val id = e.id + e.hashCode()
        val vExist: View? = getViewById(id)
        if (null != vExist) return
        val view: View = e.delegate.onMake(context, e) ?: return
        view.id = id
        addView(view)
        val cs = ConstraintSet()
        cs.clone(this)
        e.delegate.onLayout(cs, view, vAnchor)
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