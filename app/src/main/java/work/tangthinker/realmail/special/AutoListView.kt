package work.tangthinker.realmail.special

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView
import java.util.jar.Attributes

class AutoListView: ListView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, style: Int) : super(context, attributeSet, style)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expendSpec = MeasureSpec.makeMeasureSpec((Int.MAX_VALUE shr 2),  MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expendSpec)
    }

}