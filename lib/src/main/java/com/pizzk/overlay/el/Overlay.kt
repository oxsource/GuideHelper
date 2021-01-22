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

    fun anchor(id: Int, delegate: Anchor.Delegate) {
        val anchor = anchors.find { it.id == id } ?: return
        anchor.delegate = delegate
    }

    fun marker(id: Int, delegate: Marker.Delegate) {
        val marker = markers.find { it.id == id } ?: return
        marker.delegate = delegate
    }
}