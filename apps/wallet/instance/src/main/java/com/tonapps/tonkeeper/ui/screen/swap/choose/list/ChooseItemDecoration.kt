package com.tonapps.tonkeeper.ui.screen.swap.choose.list

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.tonapps.uikit.color.UIKitColor
import com.tonapps.uikit.color.resolveColor
import uikit.extensions.dp
import com.tonapps.uikit.color.R as UIKitR

class ChooseItemDecoration : RecyclerView.ItemDecoration() {
    private val radius = 30f
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val topRect = floatArrayOf(radius, radius, radius, radius, 0f, 0f, 0f, 0f)
    private val bottomRect = floatArrayOf(0f, 0f, 0f, 0f, radius, radius, radius, radius)

    init {
        paint.color = Color.parseColor("#1D2633")
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            val totalCount = parent.adapter?.itemCount ?: 0
            val rect = RectF(
                view.left.toFloat(),
                view.top.toFloat(),
                view.right.toFloat(),
                view.bottom.toFloat()
            )
            if (position == 0) {
                val path = Path()
                val radii = topRect
                path.addRoundRect(rect, radii, Path.Direction.CW)
                c.drawPath(path, paint)
            } else if (position == totalCount - 1) {
                val path = Path()
                val radii = bottomRect
                path.addRoundRect(rect, radii, Path.Direction.CW)
                c.drawPath(path, paint)
                drawDivider(canvas = c, view = view)
            } else {
                c.drawRect(rect, paint)
                drawDivider(canvas = c, view = view)
            }
        }
    }

    private fun drawDivider(canvas: Canvas, view: View) {
        val dividerPaint = Paint().apply {
            strokeWidth = .5f.dp
            color = view.context.resolveColor(UIKitColor.separatorCommonColor)
        }
        val left = view.left + 16.dp.toFloat()
        val top = view.top.toFloat()
        val right = view.right.toFloat()
        val bottom = view.top.toFloat() + 4
        canvas.drawRect(
            left,
            top,
            right,
            bottom,
            dividerPaint
        )
    }
}