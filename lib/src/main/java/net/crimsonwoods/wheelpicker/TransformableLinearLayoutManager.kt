package net.crimsonwoods.wheelpicker

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Base class of custom [LinearLayoutManager] to receive "onScrollChanged" event from RecyclerView.
 */
open class TransformableLinearLayoutManager : LinearLayoutManager {
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

    open fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) = Unit
}
