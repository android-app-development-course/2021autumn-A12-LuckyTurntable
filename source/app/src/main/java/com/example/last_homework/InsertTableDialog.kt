package com.example.last_homework

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.insertdialog.*

class InsertTableDialog:Dialog {//插入数据弹出框
    var insertTitle: TextView? = null
    var insertData: EditText? = null
    var insertTitleDialogConfirm: TextView? = null //确定按钮可通过外部自定义按钮内容
    var insertTitleDialogCancel: TextView? = null //取消

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        setContentView(R.layout.insertdialog)
        setCanceledOnTouchOutside(false)
        insertTitle = findViewById(R.id.insert_title)
        insertData = findViewById(R.id.insert_data)
        insertTitleDialogConfirm = findViewById(R.id.insert_ok)
        insertTitleDialogCancel = findViewById(R.id.insert_cancel)
    }
    class Builder(val context: Context) {
        var confirmListener: OnConfirmListener? = null
        var cancelListener: OnCancelListener? = null
        var data: String? = null
        var title:String? = null
        var btConfirmText: String? = null
        var tvCancelText: String? = null
        var cancelIsVisibility: Boolean? = true

        fun setOnConfirmListener(confirmListener: OnConfirmListener): Builder {
            this.confirmListener = confirmListener
            return this
        }

        fun setOnCancelListener(cancelListener: OnCancelListener): Builder {
            this.cancelListener = cancelListener
            return this
        }

        fun setTitle(content: String): Builder {
            this.title = content
            return this
        }

        fun setData(content: String): Builder {
            this.data = data
            return this
        }

        // 点击确定按钮的文字
        fun setConfirmText(btConfirmText: String): Builder {
            this.btConfirmText = btConfirmText
            return this
        }

        //取消按钮的文字
        fun setCancelText(tvCancelText: String): Builder {
            this.tvCancelText = tvCancelText
            return this
        }

        fun setCancelIconIsVisibility(cancelIsVisibility: Boolean): Builder {
            this.cancelIsVisibility = cancelIsVisibility
            return this
        }

        fun create(): InsertTableDialog {
            val dialog = InsertTableDialog(context)
            if (!TextUtils.isEmpty(title)) {
                dialog.insertTitle?.text = "添加数据"
            } else {
                dialog.insertTitle?.text = "添加数据"
            }
            dialog.insertTitleDialogConfirm?.text = this.btConfirmText ?: "确定"
            if (this.cancelIsVisibility!!) {
                dialog.insertTitleDialogCancel?.text = this.tvCancelText ?: "取消"
            } else {
//                dialog.tvDialogCancel?.visibility = View.GONE
            }

            if (cancelListener != null) {
                dialog.insertTitleDialogCancel?.setOnClickListener { v -> cancelListener!!.onClick(dialog) }
            }
            if (confirmListener != null) {
                dialog.insertTitleDialogConfirm?.setOnClickListener {
                        v -> confirmListener!!.onClick(dialog)
                }
            }
            return dialog
        }

    }

    // 点击弹窗取消按钮回调
    interface OnCancelListener {
        fun onClick(dialog: Dialog)
    }

    // 点击弹窗跳转回调
    interface OnConfirmListener {
        fun onClick(dialog: Dialog)
    }

}