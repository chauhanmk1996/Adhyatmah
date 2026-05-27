package com.app.adhyatmah.presentation.ui.fragment

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.FragmentPaymentGatewayBinding
import com.app.adhyatmah.payment.message_sms.sms_request.SMSRequest
import com.app.adhyatmah.payment.payment_verify_request.PaymentVerifyRequest
import com.app.adhyatmah.presentation.ui.viewmodel.PaymentViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

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

        // -------------------------------------------------
        // 🔥 Critical: Detect JS redirects using ChromeClient
        // -------------------------------------------------
        webView.webChromeClient = object : WebChromeClient() {

            private var lastUrl: String? = null

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                val current = view?.url ?: return

                // log URL when it changes
                if (current != lastUrl) {
                    Log.e("RazorpayWebProgress", "URL changed: $current")
                    lastUrl = current
                }

                // when page fully loads, check if success URL reached
                if (newProgress == 100) {
                    when {
                        current.contains(successUrl, true) -> {
                            redirectToCongrats()
                        }
                    }
                }
            }
        }

        // ----------------------------
        // WebViewClient (still needed)
        // ----------------------------
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false

                Log.d("RazorpayWebIntercept", url)

                // this may NOT trigger for JS redirect, but keep it
                if (url.contains(successUrl, true)) {
                    redirectToCongrats()
                    return true
                }

                return false   // allow WebView to load normally
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Toast.makeText(requireContext(), "Error: $description", Toast.LENGTH_SHORT).show()
            }
        }

        Log.e("RazorpayWebLoad", "Loading payment URL: $paymentUrl")
        webView.loadUrl(paymentUrl)
    }

    private fun redirectToCongrats() {
        val navController = findNavController()

        // Prevent crash if already navigated
        if (navController.currentDestination?.id != R.id.paymentGatewayFragment) {
            Log.e("NAV", "Skipping navigation, already at: ${navController.currentDestination?.label}")
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

/*class PaymentGatewayFragment : BaseFragment<FragmentPaymentGatewayBinding>() {

    private val paymentViewModel by activityViewModels<PaymentViewModel>()
    private lateinit var webView: WebView
    var url = ""
    var order_id=""
    var paymentLink = ""
    var reference = ""
    var token = ""

    override fun setLayout(): Int {
        return R.layout.fragment_payment_gateway
    }

    override fun initView(savedInstanceState: Bundle?) {
        url = arguments?.getString("authUrl").toString()
        order_id = arguments?.getString("order_id").toString()
        paymentLink = arguments?.getString("link").toString()
        Log.d("TAG", "fsfs: $order_id")
        Log.d("TAG", "initView: ${paymentLink}")
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }
        setObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webView)
        openWebUrl()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openWebUrl() {
        val webView = binding.webView

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Log.d("TAG", "shouldOverrideUrlLoading: $url")
                if (url != null && url.contains("/payment/callback")) {
                    val uri = Uri.parse(url)
                    reference = uri.getQueryParameter("reference").toString()
                    Log.d("TAG", "shouldOverrideUrlLoading: $reference and ${uri.getQueryParameter("reference").toString()}")
                    verifyPayment()
                    return true
                }
                return false // Allow all other URLs to load
            }
             override fun onPageFinished(view: WebView?, url: String?) {
             super.onPageFinished(view, url)
             Log.d("TAG", "onPageFinished: $url")

             // Check if the URL contains the payment success parameters
             *//*if (url != null && url.contains("razorpay_payment_link_status=paid")) {
                 // Extract the razorpay_payment_id if needed (you can use it for verification or logging)
                 val uri = Uri.parse(url)
                 val razorpayPaymentId = uri.getQueryParameter("razorpay_payment_id")
                 val razorpaySignature = uri.getQueryParameter("razorpay_signature")
                 // Log the parameters for debugging
                 Log.d("TAG", "razorpay_payment_id: $razorpayPaymentId")
                 Log.d("TAG", "razorpay_signature: $razorpaySignature")
                 redirectToCongratsPage()
                 // Now, redirect to the congratulation page
//                val req = PaymentSuccessClearOrderRequest(token)
//                 paymentViewModel.paymentSuOrderClearAPIs(req)
//                 getSMS()
             }*//*
         }
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Toast.makeText(
                    requireContext(),
                    "Error loading page: $description",
                    Toast.LENGTH_LONG
                ).show()
            }
        }





        webView.loadUrl(url)
    }


    private fun redirectToCongratsPage() {
        val bundle = Bundle()
        bundle.putString("order_id", order_id)
        findNavController().navigate(R.id.action_paymentGatewayFragment_to_congratulationFragment,bundle)
    }



    private fun verifyPayment() {
        if (!reference.isNullOrEmpty()) {
            val request = PaymentVerifyRequest(reference)
             paymentViewModel.paymentVerify(request)
        }
    }

    private fun getSMS() {
        val request = SMSRequest()
        request.text = "Dear Customer, your payment of GHS 100.00 was successful. Thank you for shopping with us. If you have any questions, please contact support."
        val destinations = listOf("+233530316660")
        request.destinations = destinations
        request.sender = "PRIMEGROUP"
        paymentViewModel.hitSMSApi(request)
    }

    private fun setObserver() {
        paymentViewModel.getPaymentVerifyResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            getSMS()
                            Log.d("TAG", "setObserver1: $data")
                          //  navigateToPaymentSuccessScreen(reference)
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                        }
                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        paymentViewModel.getSMSApiResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            Log.d("TAG", "setObserver: $data")
                            redirectToCongratsPage()
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                        }
                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        paymentViewModel.getPaymentSuOrderClearRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            getSMS()
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                        }
                        401 -> {
                            Log.e("TAG", "Unauthorized access")
                        }
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }
}*/


/*
    private fun openWebUrls() {

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Toast.makeText(requireContext(), "Loading..", Toast.LENGTH_SHORT).show()
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Toast.makeText(
                    requireContext(),
                    "Error loading page: $description",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        webView.loadUrl(url)

    }

 }
*/

