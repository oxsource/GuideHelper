package com.pizzk.overlay

import com.pizzk.overlay.el.Overlay

class OverlayAdapter {
    private val overlays: MutableList<Overlay> = mutableListOf()
    private var view: OverlayLayout? = null

    fun with(v: OverlayLayout?): OverlayAdapter {
        view = v
        return this
    }

    fun overlays(v: List<Overlay>): OverlayAdapter {
        overlays.clear()
        overlays.addAll(v)
        return this
    }

    fun overlays(vararg vs: Overlay): OverlayAdapter {
        return overlays(vs.toList())
    }

    fun next(): Boolean {
        val view: OverlayLayout = view ?: return false
        if (overlays.isEmpty()) {
            view.setOverlay(null)
            view.setVisibility(false)
            return false
        }
        view.setVisibility(true)
        view.setOverlay(overlays.removeAt(0))
        return true
    }

    fun finish() {
        overlays.clear()
        next()
    }
}