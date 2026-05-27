package com.app.adhyatmah.presentation.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.app.adhyatmah.R
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class RazorpayPaymentActivity : AppCompatActivity(), PaymentResultListener {

    private var orderId: String? = null
    private var amount: Int = 0
    private var currency: String? = null
    private var keyId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_payment_gateway)

        orderId = intent.getStringExtra("orderId")
        amount = intent.getIntExtra("amount", 0)
        currency = intent.getStringExtra("currency")
        keyId = intent.getStringExtra("keyId")

        openRazorpayCheckout()
    }

    private fun openRazorpayCheckout() {
        try {
            val checkout = Checkout()
            checkout.setKeyID(keyId)

            val options = JSONObject()
            options.put("name", getString(R.string.app_name))
            options.put("description", "Order Payment")
            options.put("order_id", orderId)
            options.put("currency", currency ?: "INR")
            options.put("amount", amount)

            val prefill = JSONObject()
            prefill.put("email", "test@gmail.com")
            prefill.put("contact", "9999999999")
            options.put("prefill", prefill)

            checkout.open(this, options)

        } catch (e: Exception) {
            val intent = Intent()
            intent.putExtra("paymentSuccess", false)
            intent.putExtra("error", e.message)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun onPaymentSuccess(paymentId: String?) {
        Log.i("TAG", "onPaymentSuccess: $paymentId")
        val intent = Intent()
        intent.putExtra("paymentSuccess", true)
        intent.putExtra("paymentId", paymentId)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Log.i("TAG", "onPaymentError: $response")
        val intent = Intent()
        intent.putExtra("paymentSuccess", false)
        intent.putExtra("error", response)
        setResult(RESULT_OK, intent)
        finish()
    }
}

