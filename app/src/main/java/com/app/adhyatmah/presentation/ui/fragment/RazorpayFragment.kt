package com.app.adhyatmah.presentation.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.razorpay.Checkout
import android.widget.Toast
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentRazorpayBinding
import com.app.adhyatmah.utils.base.BaseFragment
import com.razorpay.PaymentResultListener
import org.json.JSONObject

class RazorpayFragment : BaseFragment<FragmentRazorpayBinding>(),PaymentResultListener {
    override fun setLayout(): Int {
        return R.layout.fragment_razorpay
    }

    override fun initView(savedInstanceState: Bundle?) {
        Checkout.preload(requireContext())

        startPayment(requireActivity())
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
        Log.d("TAG", "onPaymentSuccess: $paymentId")
        Toast.makeText(requireContext(), "Payment Successful. Payment ID: $paymentId", Toast.LENGTH_LONG).show()

    }

    override fun onPaymentError(code: Int, response: String?) {
        Log.d("TAG", "onPayment Failed: $code   and $response")
        Toast.makeText(requireContext(), "Payment Failed: $response", Toast.LENGTH_LONG).show()


    }
}