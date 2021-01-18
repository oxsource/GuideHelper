package com.pizzk.overlay;

import com.pizzk.overlay.el.Overlay;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class OverlayAdapter {
    private final List<Overlay> overlays = new LinkedList<>();
    private OverlayLayout view = null;

    public OverlayAdapter with(OverlayLayout v) {
        view = v;
        return this;
    }

    public OverlayAdapter overlays(List<Overlay> v) {
        overlays.clear();
        if (null == v) return this;
        overlays.addAll(v);
        return this;
    }

    public OverlayAdapter overlays(Overlay... vs) {
        if (null == vs || vs.length <= 0) return this;
        return overlays(Arrays.asList(vs));
    }

    public boolean next() {
        if (null == view) return false;
        if (overlays.isEmpty()) {
            view.setOverlay(null);
            view.setVisibility(false);
            return false;
        }
        view.setVisibility(true);
        view.setOverlay(overlays.remove(0));
        return true;
    }

    public void finish() {
        overlays.clear();
        next();
    }
}
