package com.jigar.me.ui.view.jetpack.abacus_base.components.withcanvas

import androidx.compose.ui.unit.Density
import com.jigar.me.ui.view.jetpack.abacus_base.AbacusDimensionModel


// ─────────────────────────────────────────────────────────────
// Geometry helper for Canvas-based Abacus
// ─────────────────────────────────────────────────────────────

data class AbacusCanvasGeometry(
    val columnCentersX: List<Float>,
    val rowTop: List<Float>,
    val rowBottom: List<Float>,
    val beadWidthPx: Float,
    val beadHeightPx: Float,
    val beamHeightPx: Float,
    val columnSpacesPx: Float,
    val extraSpacePx: Float
) {
    fun findColumn(x: Float): Int? {
        val colWidth = beadWidthPx + (columnSpacesPx * 2f)
        if (colWidth <= 0f) return null
        val index = (x / colWidth).toInt()
        return if (index in columnCentersX.indices) index else null
    }

    fun findRow(y: Float): Int? {
        for (i in rowTop.indices) {
            if (y >= rowTop[i] && y <= rowBottom[i]) return i
        }
        return null
    }

    companion object {
        fun build(dim: AbacusDimensionModel, numberOfColumns: Int, density: Density): AbacusCanvasGeometry = with(density) {

            val beadW = dim.beadWidth.toPx()
            val beadH = dim.beadHeight.toPx()
            val beamH = dim.beamHeight.toPx()
            val spacing = dim.columnSpaces.toPx()
            val extra = dim.extraSpace.toPx()

            // Column centers
            val colWidth = beadW + spacing * 2
            val centers = List(numberOfColumns) { i ->
                spacing + beadW / 2 + i * colWidth
            }

            // -------------------------
            // VERTICAL ROW POSITIONS
            // -------------------------

            val rowTop = FloatArray(7)
            val rowBottom = FloatArray(7)

            var y = extra

            // Row 0 (upper bead 1)
            rowTop[0] = y
            rowBottom[0] = y + beadH
            y = rowBottom[0]

            // Row 1 (upper bead 2)
            rowTop[1] = y
            rowBottom[1] = y + beadH
            y = rowBottom[1]

            // Beam before row 2 — Center the beam
            val beamTop = y
            val bead2Top = beamTop + beamH

            // Row 2 (first lower bead)
            rowTop[2] = bead2Top
            rowBottom[2] = bead2Top + beadH

            y = rowBottom[2]

            // Row 3–6 (remaining lower beads)
            for (i in 3..6) {
                rowTop[i] = y
                rowBottom[i] = y + beadH
                y = rowBottom[i]
            }

            return AbacusCanvasGeometry(
                columnCentersX = centers,
                rowTop = rowTop.toList(),
                rowBottom = rowBottom.toList(),
                beadWidthPx = beadW,
                beadHeightPx = beadH,
                beamHeightPx = beamH,
                columnSpacesPx = spacing,
                extraSpacePx = extra
            )
        }
    }

}