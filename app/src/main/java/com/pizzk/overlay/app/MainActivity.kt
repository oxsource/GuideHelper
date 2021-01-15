package com.pizzk.overlay.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import com.pizzk.overlay.*
import com.pizzk.overlay.el.Anchor
import com.pizzk.overlay.el.Marker
import com.pizzk.overlay.el.Overlay

class MainActivity : AppCompatActivity() {
    private val adapter: OverlayAdapter = OverlayAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val vOverlay: OverlayLayout = findViewById(R.id.vOverlay)
        vOverlay.setMaskColor(R.color.overlay_mask)
        vOverlay.setVisibility(false)
        adapter.with(vOverlay)
        findViewById<View>(R.id.tv1).setOnClickListener {
            Toast.makeText(baseContext, "TV1 clicked", Toast.LENGTH_SHORT).show()
            buildMultiOverlay()
            adapter.next()
        }
        findViewById<View>(R.id.tv2).setOnClickListener {
            Toast.makeText(baseContext, "TV2 clicked", Toast.LENGTH_SHORT).show()
            buildMultiAnchor()
            adapter.next()
        }
    }

    private fun buildMultiOverlay() {
        val overlay1 = Overlay.Builder()
            .anchor(Anchor.rect(R.id.tv1, radius = 10, inset = 10))
            .marker(R.layout.tv1_marker)
            .build()
        overlay1.marker(R.layout.tv1_marker, m1Layout)
        val overlay2 = Overlay.Builder()
            .anchor(Anchor.circle(R.id.tv2, inset = 30))
            .marker(R.layout.tv2_marker)
            .build()
        overlay2.marker(R.layout.tv2_marker, m2Layout)
        adapter.overlays(listOf(overlay1, overlay2))
    }

    private fun buildMultiAnchor() {
        val overlay = Overlay.Builder()
            .anchor(Anchor.rect(R.id.tv1, radius = 10, inset = 5))
            .marker(R.layout.tv1_marker)
            .anchor(Anchor.circle(R.id.tv2, inset = 20))
            .marker(R.layout.tv2_marker)
            .build()
        overlay.marker(R.layout.tv1_marker, m1Layout)
        overlay.marker(R.layout.tv2_marker, m2Layout)
        adapter.overlays(listOf(overlay))
    }

    private val m1Layout: Marker.MarkerLayout = object : Marker.MarkerLayout() {
        override fun onLayout(cs: ConstraintSet, marker: View, anchor: View) {
            super.onLayout(cs, marker, anchor)
            connect(ConstraintSet.START)
            connect(ConstraintSet.END)
            connect(ConstraintSet.TOP, ConstraintSet.BOTTOM, 20)
            setClickListener(R.id.btNext) { adapter.next() }
        }
    }

    private val m2Layout: Marker.MarkerLayout = object : Marker.MarkerLayout() {
        override fun onLayout(cs: ConstraintSet, marker: View, anchor: View) {
            super.onLayout(cs, marker, anchor)
            connect(ConstraintSet.START)
            connect(ConstraintSet.END)
            connect(ConstraintSet.TOP, ConstraintSet.BOTTOM, 20)
            setParentClickListener { adapter.next() }
        }
    }
}