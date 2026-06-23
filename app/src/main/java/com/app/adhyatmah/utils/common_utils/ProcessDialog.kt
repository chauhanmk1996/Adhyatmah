package com.app.adhyatmah.utils.common_utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import com.app.adhyatmah.R
import androidx.core.graphics.drawable.toDrawable

object ProcessDialog {
    private var progressDialog: Dialog? = null

    fun showDialog(context: Context?, isDialog: Boolean) {
        if (isDialog) {
            if (context != null) {
                start(context)
            }
        }
    }

    fun dismissDialog(isDialog: Boolean) {
        if (isDialog) {
            dismiss()
        }
    }

    fun start(context: Context) {
        if (!isShowing()) {
            if (!(context as Activity).isFinishing) {
                progressDialog = Dialog(context)
                progressDialog?.setCancelable(false)
                progressDialog?.window!!.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
                progressDialog?.setContentView(R.layout.view_progress_dialog)
                progressDialog?.show()
            }
        }
    }

    fun dismiss() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        } finally {
            progressDialog = null
        }
    }

    fun isShowing(): Boolean {
        return if (progressDialog != null) {
            progressDialog!!.isShowing
        } else {
            false
        }
    }
}