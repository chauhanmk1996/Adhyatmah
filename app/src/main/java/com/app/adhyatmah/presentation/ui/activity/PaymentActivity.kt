package com.app.adhyatmah.presentation.ui.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.ActivityPaymentBinding
import com.app.adhyatmah.utils.base.BaseActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class PaymentActivity : BaseActivity<ActivityPaymentBinding>(),PaymentResultListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
     startPayment(this)
    }
    fun startPayment(activity: Activity) {
        val checkout = Checkout()

        checkout.setKeyID("rzp_test_399ynICiSYdYTF") // Replace with your Razorpay public key
        //  checkout.open(activity, options)

        try {
            val options = JSONObject()
            options.put("name", "Adhyatmah Bharat")
            options.put("description", "Payment for order Recharge Coins")
            options.put("image", "https://your-logo-url.com/logo.png") // Optional
            options.put("theme.color", R.color.theme)
            options.put("currency", "INR")
            options.put("amount", "50000") // Amount in paise (₹500.00)

            val prefill = JSONObject()
            prefill.put("email", "test@example.com")
            prefill.put("contact", "9876543210")
            options.put("prefill", prefill)

            checkout.open(activity, options)

        } catch (e: Exception) {
            e.printStackTrace()
            Log.i("TAG", "startPayment: "+e.message)
            Toast.makeText(activity, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onPaymentSuccess(paymentId: String?) {
        // This will be called on successful payment
        Log.d("PaymentActivity", "Payment Success. Payment ID: $paymentId")
        Toast.makeText(this, "Payment Successful! Payment ID: $paymentId", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(errorCode: Int, errorDescription: String?) {
// This will be called on payment failure
        Log.e("PaymentActivity", "Payment Failed. Error Code: $errorCode, Description: $errorDescription")
        Toast.makeText(this, "Payment Failed: $errorDescription", Toast.LENGTH_LONG).show()
    }

}