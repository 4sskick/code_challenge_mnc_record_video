package id.niteroomcreation.mncvideorecordfilter.presentation.custom

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * Created by Septian Adi Wijaya on 09/04/2023.
 * please be sure to add credential if you use people's code
 */
class CameraView : GLSurfaceView, View.OnTouchListener {

    private lateinit var listener: TouchListener

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    fun setListener(listener: TouchListener) {
        this.listener = listener
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val actionMasked: Int = event.actionMasked
        if (actionMasked != MotionEvent.ACTION_DOWN)
            return false

        if (listener != null) {
            listener.onTouch(event, v.width, v.height)
        }
        return false
    }

    interface TouchListener {
        fun onTouch(event: MotionEvent, w: Int, h: Int)
    }
}