package work.tangthinker.realmail.utils

import android.os.CountDownTimer
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView

class ButtonCountDownTimer(private val relativeLayout: RelativeLayout, private val textView: TextView) : CountDownTimer(60000, 1000) {
    override fun onTick(millisUntilFinished: Long) {
        relativeLayout.isClickable = false
        relativeLayout.isEnabled = false
        textView.text = "${millisUntilFinished / 1000}s"
    }

    override fun onFinish() {
        textView.text = "获取验证码"
        relativeLayout.isClickable = true
        relativeLayout.isEnabled = true
        cancel()
    }
}