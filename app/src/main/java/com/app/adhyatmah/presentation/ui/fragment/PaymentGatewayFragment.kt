package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentPaymentGatewayBinding
import com.app.adhyatmah.utils.base.BaseFragment

class PaymentGatewayFragment : BaseFragment<FragmentPaymentGatewayBinding>() {

    private var paymentUrl = ""
    private var successUrl = ""
    private var orderId = ""

    override fun setLayout(): Int = R.layout.fragment_payment_gateway

    override fun initView(savedInstanceState: Bundle?) {
        paymentUrl = arguments?.getString("paymentUrl").toString()
        successUrl = arguments?.getString("successUrl").toString()
        orderId = arguments?.getString("order_id").toString()

        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openPaymentUrlInWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openPaymentUrlInWebView() {
        val webView = binding.webView
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webChromeClient = object : WebChromeClient() {
            private var lastUrl: String? = null

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                val current = view?.url ?: return
                if (current != lastUrl) {
                    Log.e("RazorpayWebProgress", "URL changed: $current")
                    lastUrl = current
                }

                if (newProgress == 100) {
                    when {
                        current.contains(successUrl, true) -> {
                            redirectToCongrats()
                        }
                    }
                }
            }
        }

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false
                Log.d("RazorpayWebIntercept", url)
                if (url.contains(successUrl, true)) {
                    redirectToCongrats()
                    return true
                }

                return false
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?,
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Toast.makeText(requireContext(), "$description", Toast.LENGTH_SHORT).show()
            }
        }

        Log.e("RazorpayWebLoad", "Loading payment URL: $paymentUrl")
        webView.loadUrl(paymentUrl)
    }

    private fun redirectToCongrats() {
        val navController = findNavController()

        // Prevent crash if already navigated
        if (navController.currentDestination?.id != R.id.paymentGatewayFragment) {
            return
        }

        val bundle = Bundle()
        bundle.putString("order_id", orderId)

        navController.navigate(
            R.id.action_paymentGatewayFragment_to_congratulationFragment,
            bundle
        )
    }
}