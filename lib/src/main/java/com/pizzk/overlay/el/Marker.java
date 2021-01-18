package com.pizzk.overlay.el;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.constraintlayout.widget.ConstraintSet;

public class Marker {
    public final int id;
    public final int anchor;

    public Marker(int id, int anchor) {
        this.id = id;
        this.anchor = anchor;
    }

    private Make make = delegateMake;
    private Layout layout = delegateLayout;
    private View view = null;

    public Make getMake() {
        return make;
    }

    public Layout getLayout() {
        return layout;
    }

    public View getView() {
        return view;
    }

    public void setMake(Make make) {
        if (null == make) return;
        this.make = make;
    }

    public void setLayout(Layout layout) {
        if (null == layout) return;
        this.layout = layout;
    }

    public void setView(View view) {
        this.view = view;
    }

    public interface Make {
        View onMake(Context context, Marker marker);
    }

    public interface Layout {
        void onLayout(ConstraintSet cs, View marker, View anchor);
    }

    /**
     * 提供Marker视图
     */
    public static class MarkerMake implements Make {

        @Override
        public View onMake(Context context, Marker marker) {
            if (null != marker.view) return marker.view;
            return LayoutInflater.from(context).inflate(marker.id, null);
        }
    }

    /**
     * Marker在OverlayLayout中的默认布局实现
     */
    public static class MarkerLayout implements Layout {
        private ConstraintSet cs = null;
        private View marker = null;
        private View anchor = null;

        @Override
        public void onLayout(ConstraintSet cs, View marker, View anchor) {
            this.cs = cs;
            this.marker = marker;
            this.anchor = anchor;
        }

        /**
         * 约束布局connect方法封装
         */
        public void connect(int startSide, int endSide, int margin) {
            if (null == cs || null == marker || null == anchor) return;
            cs.connect(marker.getId(), startSide, anchor.getId(), endSide, margin);
        }

        public void connect(int side, int margin) {
            connect(side, side, margin);
        }

        /**
         * 设置Marker中子元素控件点击事件
         */
        public void setClickListener(int id, View.OnClickListener l) {
            if (null == marker) return;
            View v = marker.findViewById(id);
            if (null != v) v.setOnClickListener(l);
        }

        /**
         * 设置父视图(OverlayLayout)点击事件
         */
        public void setParentClickListener(View.OnClickListener l) {
            if (null == marker) return;
            ViewParent p = marker.getParent();
            if (!(p instanceof View)) return;
            View v = (View) p;
            v.setOnClickListener(l);
        }
    }

    private static final Layout delegateLayout = new MarkerLayout();
    private static final Make delegateMake = new MarkerMake();

    public static View iv(Context context, @DrawableRes int res) {
        try {
            ImageView view = new ImageView(context);
            view.setImageResource(res);
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
