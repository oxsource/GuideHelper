package com.pizzk.overlay

import androidx.constraintlayout.widget.ConstraintSet

class ConstraintPatch {
    private var cs: ConstraintSet? = null
    private var startId: Int = -1
    private var endId: Int = -1

    fun with(cs: ConstraintSet, startId: Int, endId: Int): ConstraintPatch {
        this.cs = cs
        this.startId = startId
        this.endId = endId
        return this
    }

    fun connect(startSide: Int, endSide: Int = startSide, margin: Int = 0): ConstraintPatch {
        val cs = this.cs ?: return this
        cs.connect(startId, startSide, endId, endSide, margin)
        return this
    }
}