package com.pizzk.overlay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;

import com.pizzk.overlay.el.Anchor;
import com.pizzk.overlay.el.Marker;
import com.pizzk.overlay.el.Overlay;

public class OverlayLayout extends ConstraintLayout {
    private final Paint paint = new Paint();
    private final RectF rect = new RectF();
    private int maskColor = Color.TRANSPARENT;
    private Bitmap bitmap = null;

    public OverlayLayout(@NonNull Context context) {
        this(context, null);
    }

    public OverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OverlayLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint.setColor(Color.TRANSPARENT);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);
        setClickable(true);
        setMaskColor(R.color.overlay_mask);
    }

    public void setMaskColor(@ColorRes int colorId) {
        maskColor = ContextCompat.getColor(getContext(), colorId);
    }

    public void setOverlay(Overlay overlay) {
        removeAllViews();
        setOnClickListener(null);
        if (null == overlay) return;
        ViewParent p = getParent();
        if (!(p instanceof ViewGroup)) return;
        ViewGroup view = (ViewGroup) p;
        post(() -> {
            try {
                onChangeLayout(view, overlay);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setVisibility(boolean v) {
        if (v == (View.VISIBLE == getVisibility())) return;
        setVisibility(v ? View.VISIBLE : View.GONE);
    }

    private void onChangeLayout(ViewGroup viewGroup, Overlay overlay) {
        //准备画布

        Bitmap bmp = null != bitmap ? bitmap : Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        bitmap = bmp;
        bmp.eraseColor(Color.TRANSPARENT);
        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(maskColor);
        //计算位置
        int[] vXY = new int[2];
        getLocationOnScreen(vXY);
        int[] vAnchorXY = new int[2];
        for (Anchor e : overlay.anchors) {
            View vAnchor = e.getFind().onFind(viewGroup, e);
            if (null == vAnchor) return;
            //计算宽度及位置
            vAnchor.getLocationOnScreen(vAnchorXY);
            rect.left = vAnchorXY[0] - vXY[0] - 0f;
            rect.top = vAnchorXY[1] - vXY[1] - 0f;
            rect.right = rect.left + vAnchor.getWidth();
            rect.bottom = rect.top + vAnchor.getHeight();
            //镂空样式
            rect.inset(-e.outset, -e.outset);
            //锚点生产及绘制
            e.getDraw().onDraw(canvas, paint, e, rect);
            View anchor = onFakeAnchor(e.id, rect);
            //标记层布局
            for (Marker marker : overlay.markers) {
                if (marker.anchor != e.id) continue;
                onLayoutMarker(viewGroup.getContext(), marker, anchor);
            }
        }
    }

    private View onFakeAnchor(int id, RectF rc) {
        View v = getViewById(id);
        if (null != v) return v;
        View view = new View(getContext());
        view.setId(id);
        addView(view, (int) rc.width(), (int) rc.height());
        ConstraintSet cs = new ConstraintSet();
        cs.clone(this);
        int iid = ConstraintSet.PARENT_ID;
        cs.connect(id, ConstraintSet.START, iid, ConstraintSet.START, (int) rc.left);
        cs.connect(id, ConstraintSet.TOP, iid, ConstraintSet.TOP, (int) rc.top);
        cs.applyTo(this);
        return view;
    }

    @SuppressLint("ResourceType")
    private void onLayoutMarker(Context context, Marker marker, View anchor) {
        View v = marker.getMake().onMake(context, marker);
        if (null == v) return;
        if (v.getId() <= 0) v.setId(marker.id + marker.hashCode());
        addView(v);
        ConstraintSet cs = new ConstraintSet();
        cs.clone(this);
        marker.getLayout().onLayout(cs, v, anchor);
        cs.applyTo(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != bitmap && null != canvas) {
            canvas.drawBitmap(bitmap, 0f, 0f, null);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != bitmap) bitmap.recycle();
        bitmap = null;
    }
}
