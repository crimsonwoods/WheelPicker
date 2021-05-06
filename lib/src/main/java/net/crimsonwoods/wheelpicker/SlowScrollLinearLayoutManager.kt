package net.crimsonwoods.wheelpicker

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SlowScrollLinearLayoutManager : LinearLayoutManager {
    constructor(
        context: Context
    ) : super(context)

    constructor(
        context: Context,
        reverseLayout: Boolean
    ) : super(
        context,
        VERTICAL,
        reverseLayout
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        @AttrRes defStyleAttr: Int,
        @StyleRes defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State?,
        position: Int
    ) {
        val linearSmoothScroller = SlowLinearSmoothScroller(
            recyclerView.context
        )
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }
}