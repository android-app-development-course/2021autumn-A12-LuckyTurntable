package com.example.last_homework

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.widget.EditText
import android.widget.TextView

class CreateTableDialog:Dialog {//新建转盘弹出提示框
    var tvTitle: TextView? = null
    var tvTablename: EditText? = null
    var tvTableintro: EditText? = null
    var btDialogConfirm: TextView? = null //确定按钮可通过外部自定义按钮内容
    var tvDialogCancel: TextView? = null //取消

    constructor(context: Context) : super(context) {
        initView()
    }

    private fun initView() {
        setContentView(R.layout.createdialog)
        setCanceledOnTouchOutside(false)
        tvTitle = findViewById(R.id.tv_title)
        tvTablename = findViewById(R.id.tv_name)
        tvTableintro = findViewById(R.id.tv_intro)
        btDialogConfirm = findViewById(R.id.tv_ok)
        tvDialogCancel = findViewById(R.id.tv_cancel)
    }
    class Builder(val context: Context) {
        var confirmListener: OnConfirmListener? = null
        var cancelListener: OnCancelListener? = null
        var title: String? = null
        var name: String? = null
        var introduction: String? = null
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

        fun setIntroduction(content: String): Builder {
            this.introduction = content
            return this
        }
        fun setName(content: String): Builder {
            this.name = content
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

        fun create(): CreateTableDialog {
            val dialog = CreateTableDialog(context)
            if (!TextUtils.isEmpty(title)) {
                dialog.tvTitle?.text = "新建转盘"
            } else {
                dialog.tvTitle?.text = "新建转盘"//this.title
            }


            dialog.btDialogConfirm?.text = this.btConfirmText ?: "创建"
            if (this.cancelIsVisibility!!) {
                dialog.tvDialogCancel?.text = this.tvCancelText ?: "取消"
            } else {
//                dialog.tvDialogCancel?.visibility = View.GONE
            }

            if (cancelListener != null) {
                dialog.tvDialogCancel?.setOnClickListener { v -> cancelListener!!.onClick(dialog) }
            }
            if (confirmListener != null) {
                dialog.btDialogConfirm?.setOnClickListener {
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