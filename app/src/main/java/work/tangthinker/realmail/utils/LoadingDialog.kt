package work.tangthinker.realmail.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import work.tangthinker.realmail.R

class LoadingDialog(private val context: Context, private val layoutInflater: LayoutInflater) {

    private var loadingDialog: Dialog? = null

    fun show() {
        val dialogView = layoutInflater.inflate(R.layout.layout_process_loading, null)
        loadingDialog?.let {
            loadingDialog!!.show()
        }?:let {
            loadingDialog = Dialog(context, R.style.BottomDialog)
            loadingDialog!!.setContentView(dialogView)
            val layoutParams = dialogView.layoutParams
            layoutParams.width = (context.resources.displayMetrics.widthPixels / 1.8).toInt()
            layoutParams.height = context.resources.displayMetrics.widthPixels / 3
            dialogView.layoutParams = layoutParams
            loadingDialog!!.setCancelable(false)
            loadingDialog!!.setCanceledOnTouchOutside(false)
            loadingDialog!!.window?.setGravity(Gravity.CENTER)
            loadingDialog!!.show()
        }
    }

    fun dismiss(){
        loadingDialog?.dismiss()
    }

}