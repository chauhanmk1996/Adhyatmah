package com.app.adhyatmah.presentation.ui.pandit_ji

import android.app.AlertDialog
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.EMAIL_ID1
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentConfirmBookingBinding
import com.app.adhyatmah.domain.model.booking_payment.BookingPaymentRequest
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.ConfirmBookingViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class ConfirmBookingFragment : BaseFragment<FragmentConfirmBookingBinding>() {

    private val confirmBookingViewModel by activityViewModels<ConfirmBookingViewModel>()
    private var token = ""
    private var emailId = ""

    private var selectedPayment: String? = null  // "full" or "advance"

    override fun setLayout(): Int = R.layout.fragment_confirm_booking

    override fun initView(savedInstanceState: Bundle?) {
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        emailId = Preferences.getStringPreference(requireContext(), EMAIL_ID1).toString()
        Log.d("TAG", "initView: $emailId")

        setUI()
        setupPaymentSelection()

        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.continueBtn.setOnClickListener {
            if (selectedPayment == null) {
                Toast.makeText(requireContext(), "Please select a payment option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            hitPanditjiBooking()
        }

        setObserver()
        setupRecyclerView()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle system back button
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.webView.isVisible) {
                    // Show confirmation popup when WebView is open
                    showPaymentCancelConfirmationDialog()
                } else {
                    // Normal back behavior when WebView is not visible
                    isEnabled = false  // Disable this callback
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    // Also handle your custom back button (img back)
    private fun setupBackButton() {
        binding.backImg.setOnClickListener {
            if (binding.webView.isVisible) {
                showPaymentCancelConfirmationDialog()
            } else {
                findNavController().popBackStack()
            }
        }
    }

    private fun showPaymentCancelConfirmationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Wait! Don't leave yet.")
            .setMessage("Payment in progress. Leaving now may cause transaction failure or duplicate charges. Please stay on this page.")
            .setPositiveButton("Cancel") { _, _ ->
                // User confirmed cancel
                binding.webView.isVisible = false
                binding.continueBtn.isVisible = true
                binding.webView.stopLoading()      // Important: stop any ongoing load
                binding.webView.loadUrl("about:blank") // Clear webview content
                Toast.makeText(requireContext(), "Payment cancelled", Toast.LENGTH_SHORT).show()

                // Now safely go back
//                findNavController().popBackStack()
            }
            .setNegativeButton("Continue Payment") { dialog, _ ->
                dialog.dismiss()  // Stay on payment screen
            }
            .setCancelable(false)  // Prevent dismiss by tapping outside
            .create()

        dialog.show()
    }
    private fun setUI() {
        binding.apply {
            etUserName.text = "${UserPreference.panditjiBookingRequest.firstName} ${UserPreference.panditjiBookingRequest.lastName}"
            etAddress.text = UserPreference.panditjiBookingRequest.address
            totalAmountPrice.text = "${UserPreference.panditjiBookingRequest.gst} %"
            payFullTv.text = "₹ ${UserPreference.panditjiBookingRequest.paymentAmount}"

            val totalAmount = UserPreference.panditjiBookingRequest.paymentAmount?.toDoubleOrNull() ?: 0.0
            val advancePercent = UserPreference.panditjiBookingRequest.advance?.toDoubleOrNull() ?: 0.0
            val advanceAmount = totalAmount * advancePercent / 100
            payAdvanceTv.text = "₹ ${advanceAmount.toInt()}"

            tvTiming.text = "Timing: "+CommonUtils.formatDate(UserPreference.panditjiBookingRequest.dateTime, "yyyy-MM-dd'T'HH:mm:ss'Z'")
            tvPoojaType.text = "Puja Type: "+UserPreference.panditjiBookingRequest.poojaType
            tvDuration.text = "Duration: "+UserPreference.panditjiBookingRequest.duration
            tvDescription.text = UserPreference.panditjiBookingRequest.pujaDescription
            tvPrice.text = "₹ "+UserPreference.panditjiBookingRequest.paymentAmount
            updateSelectedLanguagesText(UserPreference.panditjiBookingRequest.language)
            Glide.with(requireContext())
                .load(UserPreference.panditjiBookingRequest.image)
                .placeholder(R.drawable.pamdit_ji)
                .error(R.drawable.pamdit_ji)
                .into(userImage)
        }
    }
    private fun updateSelectedLanguagesText(language: List<String>?) {
        if (language?.isEmpty() == true) {
            binding.tvLanguages.text = "No language selected"
        } else {
            val formatted = language?.joinToString(", ") { it.replaceFirstChar { c ->
                if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString()
            }}
            binding.tvLanguages.text = "Language(s): "+formatted
        }
    }
    private fun setupPaymentSelection() {
        binding.imgPayFullSelect.setOnClickListener {
            selectedPayment = "full"
            updateSelectionUI()
        }

        binding.imgPayAdvanceSelect.setOnClickListener {
            selectedPayment = "advance"
            updateSelectionUI()
        }
    }

    private fun updateSelectionUI() {
        binding.imgPayFullSelect.setImageResource(
            if (selectedPayment == "full") R.drawable.selected_round_btn else R.drawable.unselected_rounded_btn
        )
        binding.imgPayAdvanceSelect.setImageResource(
            if (selectedPayment == "advance") R.drawable.selected_round_btn else R.drawable.unselected_rounded_btn
        )
    }

    private fun hitPanditjiBooking() {
        // Set selected payment type

        val bookingRequest = UserPreference.panditjiBookingRequest.copy().apply {
            val totalAmount = paymentAmount?.toDoubleOrNull() ?: 0.0
            val advancePercent = advance?.toDoubleOrNull() ?: 0.0
            val advanceAmount = totalAmount * advancePercent / 100

            paymentAmount = if (selectedPayment == "advance") {
                advanceAmount.toString()
            } else {
                totalAmount.toString()
            }

            `package` = "Standard"
            pujaSamagri = listOf("Incense Sticks", "Flowers", "Sandalwood Paste", "Camphor")
            lastName = ""
            firstName = ""
            about = ""
            image = ""
            pujaDescription = ""
            advance = ""
            gst = ""
        }
        confirmBookingViewModel.hitBookPanditji(bookingRequest)
    }

    private fun setupRecyclerView() {
        // if needed in future
    }

    private fun setObserver() {
        confirmBookingViewModel.getBookPanditji().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        201 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                            confirmBookingViewModel.hitBookingPayment(BookingPaymentRequest(bookingId = it.data.payload.booking.id, currency = "INR"))
//                            findNavController().popBackStack(R.id.panditJiFragment, false)
                        }
                        401 -> Log.e("TAG", "Unauthorized access")
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)
                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
        confirmBookingViewModel.getBookingPaymentLiveData().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                            binding.webView.isVisible=true
                            binding.continueBtn.isVisible=false
                            setupWebView(it.data.payload.razorpay.payment_link.short_url, it.data.payload.success_url_app, "cancel")
                        }
                        401 -> Log.e("TAG", "Unauthorized access")
                    }
                    ProcessDialog.dismissDialog(true)
                }

                Status.LOADING -> ProcessDialog.showDialog(requireActivity(), true)
                Status.ERROR -> {
                    Log.e("TAG", "Error: ${it.message}")
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.message}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

    }
    private fun setupWebView(paymentUrl: String?, successUrl: String, cancelUrl: String) {

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        // ------------------------------------
        // 🔥 New WebChromeClient (critical fix)
        // ------------------------------------
        binding.webView.webChromeClient = object : WebChromeClient() {

            private var lastLoggedUrl: String? = null

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)

                val current = view?.url ?: return

                // Log URL changes
                if (current != lastLoggedUrl) {
                    Log.e("WEBVIEW_CHANGE", "URL changed: $current")
                    lastLoggedUrl = current
                }

                // 🚀 When final page loads completely
                if (newProgress == 100) {
                    Log.e("WEBVIEW_PROGRESS", "Loaded: $current")

                    when {
                        current.startsWith(successUrl) -> {
                            handlePaymentResult(true, current)
                        }
                        current.startsWith(cancelUrl) -> {
                            handlePaymentResult(false, current)
                        }
                    }
                }
            }
        }

        // ------------------------------------
        // WebViewClient (still needed)
        // ------------------------------------
        binding.webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "Loading: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Finished: $url")
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                Log.i("TAG", "shouldOverrideUrlLoading: $url")

                // Still try catching here
                when {
                    url.startsWith(successUrl) -> {
                        handlePaymentResult(true, url)
                        return true
                    }
                    url.startsWith(cancelUrl) -> {
                        handlePaymentResult(false, url)
                        return true
                    }
                }

                return false   // 🔥 allow WebView to load normally
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                super.onReceivedSslError(view, handler, error)
            }
        }

        paymentUrl?.let {
            Log.d("WebView", "Loading URL: $it")
            binding.webView.loadUrl(it)
        }
    }

    private fun handlePaymentResult(isSuccess: Boolean, redirectUrl: String) {

        if (!isAdded) return   // Prevent crash if fragment already removed

        if (isSuccess) {
            Toast.makeText(requireContext(), "Payment Successful", Toast.LENGTH_SHORT).show()

            // Navigate to PanditJiFragment without crashing
            findNavController().popBackStack(R.id.panditJiFragment, false)

        } else {
            Toast.makeText(requireContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show()
            binding.webView.isVisible = false
            binding.continueBtn.isVisible = true
        }

        // ❌ REMOVE THIS (it triggers crash)
        // requireActivity().onBackPressedDispatcher.onBackPressed()
    }
}
