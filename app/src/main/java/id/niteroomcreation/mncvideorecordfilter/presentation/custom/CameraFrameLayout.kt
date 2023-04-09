package id.niteroomcreation.mncvideorecordfilter.presentation.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created by Septian Adi Wijaya on 09/04/2023.
 * please be sure to add credential if you use people's code
 */
class CameraFrameLayout : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = measuredWidth
        setMeasuredDimension(width, width / 9 * 16)
    }
}