package com.pizzk.overlay.el

import android.view.View

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

        fun marker(id: Int): Builder {
            markers.add(Marker(id, anchor))
            return this
        }

        fun marker(id: Int, view: View?): Builder {
            val marker = Marker(id, anchor)
            marker.view = view
            markers.add(marker)
            return this
        }

        fun build(): Overlay = Overlay(anchors, markers)
    }

    fun anchor(id: Int, draw: Anchor.Draw) {
        val anchor = anchors.find { it.id == id } ?: return
        anchor.draw = draw
    }

    fun anchor(id: Int, find: Anchor.Find) {
        val anchor = anchors.find { it.id == id } ?: return
        anchor.find = find
    }

    fun marker(id: Int, layout: Marker.Layout) {
        val marker = markers.find { it.id == id } ?: return
        marker.layout = layout
    }

    fun marker(id: Int, make: Marker.Make) {
        val marker = markers.find { it.id == id } ?: return
        marker.make = make
    }
}