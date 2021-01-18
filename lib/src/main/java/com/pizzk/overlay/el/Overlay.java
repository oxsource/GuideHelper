package com.pizzk.overlay.el;

import android.view.View;

import java.util.LinkedList;
import java.util.List;

public class Overlay {
    public final List<Anchor> anchors;
    public final List<Marker> markers;

    public Overlay(List<Anchor> anchors, List<Marker> markers) {
        this.anchors = anchors;
        this.markers = markers;
    }

    public static class Builder {
        private final List<Anchor> anchors = new LinkedList<>();
        private final List<Marker> markers = new LinkedList<>();
        private int anchor = 0;

        public Builder anchor(Anchor v) {
            anchor = v.id;
            anchors.add(v);
            return this;
        }

        public Builder marker(Marker v) {
            markers.add(v);
            return this;
        }

        public Builder marker(int id) {
            markers.add(new Marker(id, anchor));
            return this;
        }

        public Builder marker(int id, View view) {
            Marker marker = new Marker(id, anchor);
            marker.setView(view);
            markers.add(marker);
            return this;
        }

        public Overlay build() {

            return new Overlay(anchors, markers);
        }
    }

    public void anchor(int id, Anchor.Draw draw) {
        for (Anchor anchor : anchors) {
            if (anchor.id != id) continue;
            anchor.setDraw(draw);
            break;
        }
    }

    public void anchor(int id, Anchor.Find find) {
        for (Anchor anchor : anchors) {
            if (anchor.id != id) continue;
            anchor.setFind(find);
            break;
        }
    }

    public void marker(int id, Marker.Layout layout) {
        for (Marker marker : markers) {
            if (marker.id != id) continue;
            marker.setLayout(layout);
            break;
        }
    }

    public void marker(int id, Marker.Make make) {
        for (Marker marker : markers) {
            if (marker.id != id) continue;
            marker.setMake(make);
            break;
        }
    }
}