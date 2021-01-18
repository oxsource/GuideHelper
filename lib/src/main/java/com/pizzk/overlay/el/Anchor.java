package com.pizzk.overlay.el;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;

public class Anchor {
    public final int id;
    public final int radius;
    public final boolean circle;
    public final int outset;

    private Draw draw = delegateDraw;
    private Find find = delegateFind;

    public Anchor(int id, int radius, boolean circle, int outset) {
        this.id = id;
        this.radius = radius;
        this.circle = circle;
        this.outset = outset;
    }

    public Draw getDraw() {
        return draw;
    }

    public Find getFind() {
        return find;
    }

    public void setDraw(Draw draw) {
        if (null == draw) return;
        this.draw = draw;
    }

    public void setFind(Find find) {
        if (null == find) return;
        this.find = find;
    }

    /**
     * 锚点绘制接口
     */
    public interface Draw {
        public void onDraw(Canvas canvas, Paint paint, Anchor e, RectF rect);
    }

    /**
     * 锚点查找接口，默认使用findViewById，可自定义实现查找匹配
     */
    public interface Find {
        public View onFind(ViewGroup parent, Anchor e);
    }

    /**
     * 默认支持绘制圆形和矩形
     */
    public static class AnchorDraw implements Draw {

        @Override
        public void onDraw(Canvas canvas, Paint paint, Anchor e, RectF rect) {
            if (e.circle) {
                float radius = Math.max(rect.width(), rect.height()) / 2;
                float cx = rect.centerX();
                float cy = rect.centerY();
                rect.left = cx - radius;
                rect.right = cx + radius;
                rect.top = cy - radius;
                rect.bottom = cy + radius;
                canvas.drawCircle(cx, cy, radius, paint);
            } else {
                float radius = e.radius + 0f;
                canvas.drawRoundRect(rect, radius, radius, paint);
            }
        }
    }


    public static class AnchorFind implements Find {

        @Override
        public View onFind(ViewGroup parent, Anchor e) {
            return parent.findViewById(e.id);
        }
    }

    private static final Draw delegateDraw = new AnchorDraw();
    private static final Find delegateFind = new AnchorFind();

    /**
     * 构建矩形锚点
     */
    public static Anchor rect(int id, int radius, int outset) {
        return new Anchor(id, radius, false, outset);
    }

    /**
     * 构建圆形锚点
     */
    public static Anchor circle(int id, int outset) {
        return new Anchor(id, 0, true, outset);
    }
}
