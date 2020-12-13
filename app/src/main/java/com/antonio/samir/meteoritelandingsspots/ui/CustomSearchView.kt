package com.antonio.samir.meteoritelandingsspots.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.SearchView

class CustomSearchView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : SearchView(context, attrs, defStyleAttr) {

    var savedQuery = ""

    override fun onActionViewCollapsed() {
        super.onActionViewCollapsed()
        setQuery(savedQuery, false)
    }

    override fun onActionViewExpanded() {
        super.onActionViewExpanded()
        setQuery(savedQuery, false)
    }

}