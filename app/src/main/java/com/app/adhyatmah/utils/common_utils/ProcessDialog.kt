package com.app.adhyatmah.utils.common_utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.app.adhyatmah.R

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
                progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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
        } catch (e: IllegalArgumentException) {
            // Handle or log or ignore
        } catch (e: Exception) {
            // Handle or log or ignore
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
/*object ProcessDialog {
    private var progressDialog: Dialog? = null

    // Show the dialog
    fun showDialog(context: Context?, isDialog: Boolean) {
        if (isDialog) {
            if (context != null) {
                start(context)
            }
        }
    }

    // Dismiss the dialog
    fun dismissDialog(isDialog: Boolean) {
        if (isDialog) {
            dismiss()
        }
    }

    // Start the progress dialog
    fun start(context: Context) {
        if (!isShowing()) {
            if (!(context as Activity).isFinishing) {
                progressDialog = Dialog(context)
                progressDialog?.setCancelable(false)
                progressDialog?.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                // Set the content view to the custom layout with ProgressBar
                progressDialog?.setContentView(R.layout.view_progress_dialog)

                // Find the ProgressBar and update its drawable
                val progressBar: ProgressBar = progressDialog!!.findViewById(R.id.progressCircular)

                // Set the indeterminate drawable (with larger radius)
                progressBar.setIndeterminateDrawable(context.resources.getDrawable(R.drawable.progress_bar_indeterminate, null))

                progressDialog?.show()
            }
        }
    }

    // Dismiss the progress dialog
    fun dismiss() {
        try {
            if (progressDialog != null && progressDialog!!.isShowing) {
                val progressBar: ProgressBar = progressDialog!!.findViewById(R.id.progressCircular)

                // After completion, update the ProgressBar to the default drawable with 50dp corner radius
                progressBar.setIndeterminateDrawable(progressDialog!!.context.resources.getDrawable(R.drawable.progress_bar_default, null))

                progressDialog!!.dismiss()
            }
        } catch (e: IllegalArgumentException) {
            // Handle or log or ignore
        } catch (e: Exception) {
            // Handle or log or ignore
        } finally {
            progressDialog = null
        }
    }

    // Check if the dialog is showing
    fun isShowing(): Boolean {
        return progressDialog != null && progressDialog!!.isShowing
    }
}*/
