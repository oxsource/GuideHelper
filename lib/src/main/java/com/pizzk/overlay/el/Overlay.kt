package com.pizzk.overlay.el

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

class Overlay(val anchors: List<Anchor>, val markers: List<Marker>) {

    class Builder {
        private val anchors: MutableList<Anchor> = mutableListOf()
        private val markers: MutableList<Marker> = mutableListOf()
        private var anchor: Int = 0

        fun anchor(v: Anchor): Builder {
            anchor = v.id
            anchors.add(v)
            return this
        }

        fun marker(v: Marker): Builder {
            markers.add(v)
            return this
        }

        fun marker(@LayoutRes id: Int): Builder {
            markers.add(Marker(id, anchor))
            return this
        }

        fun build(): Overlay = Overlay(anchors, markers)
    }

    fun anchor(@IdRes id: Int, draw: Anchor.Draw) {
        val anchor = anchors.find { it.id == id } ?: return
        anchor.draw = draw
    }

    fun anchor(@IdRes id: Int, find: Anchor.Find) {
        val anchor = anchors.find { it.id == id } ?: return
        anchor.find = find
    }

    fun marker(@LayoutRes id: Int, layout: Marker.Layout) {
        val marker = markers.find { it.id == id } ?: return
        marker.layout = layout
    }
}