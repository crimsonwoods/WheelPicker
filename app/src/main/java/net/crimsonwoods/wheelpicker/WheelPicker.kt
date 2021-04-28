package net.crimsonwoods.wheelpicker

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.RecyclerView

class WheelPicker : RecyclerView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, @AttrRes defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}
