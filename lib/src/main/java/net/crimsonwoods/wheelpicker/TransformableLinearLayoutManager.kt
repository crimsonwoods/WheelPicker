package net.crimsonwoods.wheelpicker

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.annotation.MainThread
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

/**
 * Base class of custom [LinearLayoutManager] to receive "onScrollChanged" event from RecyclerView.
 */
open class TransformableLinearLayoutManager : LinearLayoutManager {
    private var isDirty = false

    @get:MainThread
    @set:MainThread
    internal lateinit var itemTransformer: ItemTransformer

    @get:MainThread
    @set:MainThread
    internal lateinit var snapHelper: SnapHelper

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

    open fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val snapped = snapHelper.findSnapView(this) ?: return
        val snappedPosition = getPosition(snapped)

        (0 until childCount)
            .mapNotNull { getChildAt(it) }
            .forEach { itemView ->
                val position = getPosition(itemView)
                itemTransformer.transform(itemView, position, snappedPosition)
            }
    }

    override fun onItemsChanged(recyclerView: RecyclerView) {
        super.onItemsChanged(recyclerView)
        isDirty = true
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)

        if (isDirty) {
            val snapped = snapHelper.findSnapView(this) ?: return
            val snappedPosition = getPosition(snapped)

            (0 until childCount)
                .mapNotNull { getChildAt(it) }
                .forEach { itemView ->
                    val position = getPosition(itemView)
                    itemTransformer.transform(itemView, position, snappedPosition)
                }

            isDirty = false
        }
    }
}
