package com.pizzk.overlay.app

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pizzk.overlay.*
import com.pizzk.overlay.el.Anchor
import com.pizzk.overlay.el.Marker
import com.pizzk.overlay.el.Overlay

class MainActivity : AppCompatActivity() {
    private val adapter: OverlayAdapter = OverlayAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val rv: RecyclerView = findViewById(R.id.vRecycler)
        rv.layoutManager = LinearLayoutManager(baseContext)
        rv.adapter = ListAdapter()
        //
        val vOverlay: OverlayLayout = findViewById(R.id.vOverlay)
        vOverlay.setMaskColor(R.color.overlay_mask)
        vOverlay.setVisibility(false)
        adapter.with(vOverlay)
        findViewById<View>(R.id.tv1).setOnClickListener {
            buildMultiOverlay()
            adapter.next()
        }
        findViewById<View>(R.id.tv2).setOnClickListener {
            buildMultiAnchor()
            adapter.next()
        }
    }

    private fun buildMultiOverlay() {
        val overlay1 = Overlay.Builder()
            .anchor(Anchor.rect(R.id.tv1, radius = 10, outset = 10))
            .marker(R.layout.tv1_marker)
            .build()
        overlay1.marker(R.layout.tv1_marker, m1Layout)
        val overlay2 = Overlay.Builder()
            .anchor(Anchor.circle(R.id.tv2, outset = 30))
            .marker(R.layout.tv2_marker)
            .build()
        overlay2.marker(R.layout.tv2_marker, m2Layout)
        adapter.overlays(listOf(overlay1, overlay2))
    }

    private fun buildMultiAnchor() {
        val overlay = Overlay.Builder()
            .anchor(Anchor.rect(R.id.tv1, radius = 10, outset = 5))
            .marker(R.layout.tv1_marker)
            .anchor(Anchor.circle(R.id.tv2, outset = 20))
            .marker(R.layout.tv2_marker)
            //
            .anchor(Anchor.rect(R.id.vRecycler, radius = 10, outset = 5))
            .marker(R.id.vRecycler, Marker.iv(baseContext, R.mipmap.ic_launcher))
            .build()
        overlay.marker(R.layout.tv1_marker, m1Layout)
        overlay.marker(R.layout.tv2_marker, m2Layout)
        overlay.marker(R.id.vRecycler, object : Marker.MarkerLayout() {
            override fun onLayout(cs: ConstraintSet, marker: View, anchor: View) {
                super.onLayout(cs, marker, anchor)
                connect(ConstraintSet.START)
                connect(ConstraintSet.END)
                connect(ConstraintSet.BOTTOM, ConstraintSet.TOP, 10)
            }
        })
        //特殊情况：从RecyclerView中获取定位子元素，不能使用使用常规的findViewById
        overlay.anchor(R.id.vRecycler, object : Anchor.Find {
            override fun onFind(parent: ViewGroup, e: Anchor): View? {
                val v: ViewGroup? = findViewById(e.id)
                return v?.getChildAt(2)
            }
        })
        adapter.overlays(overlay)
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

    private class ListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        private val colors: List<Int> = listOf(Color.RED, Color.GREEN, Color.BLUE)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val v = View(parent.context)
            val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200)
            v.layoutParams = lp
            return object : RecyclerView.ViewHolder(v) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.setBackgroundColor(colors[position % colors.size])
        }

        override fun getItemCount(): Int = 4
    }
}