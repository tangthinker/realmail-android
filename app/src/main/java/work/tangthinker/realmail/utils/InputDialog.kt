package work.tangthinker.realmail.utils

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import work.tangthinker.realmail.R

class InputDialog(private val initDialogTitle: String, private val context: Context, private val layoutInflater: LayoutInflater) {

    private var inputDialog : Dialog? = null

    private var dialogTitleView : TextView? = null

    private var editText : EditText? = null

    private var rlConfirm : RelativeLayout? = null

    fun setTitle(dialogTitle: String){
        dialogTitleView?.text = dialogTitle
    }

    fun setInitData(initData: String){
        editText?.setText(initData)
    }

    fun show(onConfirmClick: OnConfirmClick) {
        val dialogView = layoutInflater.inflate(R.layout.layout_edit_dialog, null)
        inputDialog?.let {
            rlConfirm?.setOnClickListener {
                onConfirmClick.onConfirmClick(editText?.text.toString())
                editText?.setText("")
            }
            inputDialog!!.show()
        }?:let {
            inputDialog = Dialog(context, R.style.BottomDialog)
            inputDialog!!.setContentView(dialogView)

            dialogTitleView = dialogView.findViewById<TextView>(R.id.tv_dialog_title)

            dialogTitleView?.text = initDialogTitle
            editText = dialogView.findViewById<EditText>(R.id.et_dialog_input)

            editText?.isFocusable = true
            editText?.isFocusableInTouchMode = true
            editText?.requestFocus()

            rlConfirm = dialogView.findViewById<RelativeLayout>(R.id.rl_dialog_confirm)
            rlConfirm?.setOnClickListener {
                onConfirmClick.onConfirmClick(dialogView.findViewById<EditText>(R.id.et_dialog_input).text.toString())
                editText?.setText("")
            }


            dialogView.findViewById<RelativeLayout>(R.id.rl_dialog_cancel).setOnClickListener {
                dismiss()
            }

            val layoutParams = dialogView.layoutParams
            layoutParams.width = (context.resources.displayMetrics.widthPixels / 1.1).toInt()
            dialogView.layoutParams = layoutParams
            inputDialog!!.setCancelable(false)
            inputDialog!!.setCanceledOnTouchOutside(false)
            inputDialog!!.window?.setGravity(Gravity.CENTER)
            inputDialog!!.show()
        }
    }

    fun dismiss(){
        inputDialog?.dismiss()
    }

    interface OnConfirmClick {
        fun onConfirmClick(inputData: String)
    }



}