package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpacesItemDecoration(
        context: Context,
        @DimenRes val verticalMargin: Int? = null,
        @DimenRes val horizontalMargin: Int? = null
) :
        ItemDecoration() {

    private val verticalSpace: Int = verticalMargin?.let { context.resources.getDimensionPixelSize(it) }
            ?: 0
    private val horizontalSpace: Int =
            horizontalMargin?.let { context.resources.getDimensionPixelSize(it) } ?: 0

    override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.left = horizontalSpace
        outRect.right = horizontalSpace
        outRect.bottom = verticalSpace

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = verticalSpace
        } else {
            outRect.top = 0
        }
    }
}