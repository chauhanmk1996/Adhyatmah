package com.app.adhyatmah.presentation.ui.pandit_ji

import android.app.AlertDialog
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.app.adhyatmah.domain.model.get_services.PujaKit
import com.app.adhyatmah.presentation.ui.adapter.PujaKitAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.ConfirmBookingViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.util.Locale

class ConfirmBookingFragment : BaseFragment<FragmentConfirmBookingBinding>() {

    private val confirmBookingViewModel by activityViewModels<ConfirmBookingViewModel>()
    private var token = ""
    private var emailId = ""
    private val addOnPujaKitList: ArrayList<PujaKit> = ArrayList()
    private lateinit var addOnKitAdapter: PujaKitAdapter


    private var selectedPayment: String = "full"


    override fun setLayout(): Int = R.layout.fragment_confirm_booking

    override fun initView(savedInstanceState: Bundle?) {
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        emailId = Preferences.getStringPreference(requireContext(), EMAIL_ID1).toString()
        setUpAddOnKitRecycler()
        setUi()
        updateSelectionUI()
        setUpClick()
        setObserver()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.webView.isVisible) {
                        showPaymentCancelConfirmationDialog()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            })
    }

    private fun setUpAddOnKitRecycler() {
        addOnKitAdapter = PujaKitAdapter(
            minusClick = { pos ->
                val quantity = addOnPujaKitList[pos].quantity ?: 0
                if (quantity < 2) {
                    addOnPujaKitList.removeAt(pos)
                    addOnKitAdapter.removePos(pos)
                } else {
                    addOnPujaKitList[pos].quantity = quantity - 1
                    addOnKitAdapter.updateQuantity(pos, quantity - 1)
                }
            },
            addClick = { pos ->
                val quantity = addOnPujaKitList[pos].quantity ?: 0
                addOnPujaKitList[pos].quantity = quantity + 1
                addOnKitAdapter.updateQuantity(pos, quantity + 1)
            }
        )
        binding.rvPujaKit.adapter = addOnKitAdapter
    }

    private fun setUi() {
        binding.apply {
            val bookingRequest = UserPreference.panditjiBookingRequest

            //Pandit Ji Details
            bookingRequest.image?.let { url ->
                Glide.with(requireContext())
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivPanditPic)

            }
            val panditName = "${bookingRequest.firstName ?: ""} ${bookingRequest.lastName ?: ""}"
            tvPanditName.text = panditName
            tvPanditAddress.text = bookingRequest.address ?: ""

            //Puja Details
            val pujaType =
                "${getString(R.string.puja_type_heading)} ${bookingRequest.poojaType ?: ""}"
            tvPujaType.text = pujaType
            tvDescription.text = bookingRequest.pujaDescription ?: ""
            val price = "₹ ${bookingRequest.paymentAmount ?: ""}"
            tvPrice.text = price
            val duration =
                "${getString(R.string.duration_heading)} ${bookingRequest.duration ?: ""}"
            tvDuration.text = duration
            val dateTime =
                CommonUtils.formatDate(bookingRequest.dateTime ?: "", "yyyy-MM-dd'T'HH:mm:ss'Z'")
            val timing = "${getString(R.string.timing_heading)} $dateTime"
            tvTiming.text = timing

            val formattedLanguage = bookingRequest.language?.joinToString(", ") {
                it.replaceFirstChar { c ->
                    if (c.isLowerCase()) c.titlecase(Locale.ROOT) else c.toString()
                }
            }
            val language = "${getString(R.string.language_heading)} $formattedLanguage"
            tvLanguages.text = language

            //Puja Kit Details
            bookingRequest.selectedPujaKit?.let { list ->
                addOnPujaKitList.addAll(list)
            }

            bookingRequest.selectedInstantKit?.let { list ->
                addOnPujaKitList.addAll(list)
            }

            if (addOnPujaKitList.isNotEmpty()) {
                tvAddonPujaKitDetails.show()
                rvPujaKit.show()
                addOnKitAdapter.addList(addOnPujaKitList)
            } else {
                tvAddonPujaKitDetails.hide()
                rvPujaKit.hide()
            }

            //Payment Details
            val servicePrice = "₹ ${bookingRequest.paymentAmount}"
            tvServicePrice.text = servicePrice

            val addOnPrice = "₹ 0"
            tvAddOnsPrice.text = addOnPrice

            val deliveryCharge = "₹ 0"
            tvDeliveryCharge.text = deliveryCharge

            val handlingCharge = "₹ 0"
            tvHandlingCharge.text = handlingCharge

            val platformFee = "₹ 0"
            tvPlatformFee.text = platformFee

            val payFull = "₹ ${bookingRequest.paymentAmount ?: 0}"
            tvPayFull.text = payFull

            val paymentAmount = bookingRequest.paymentAmount?.toDoubleOrNull() ?: 0.0
            val advance = bookingRequest.advance?.toDoubleOrNull() ?: 0.0
            val payAdvance = "₹ ${paymentAmount * advance / 100}"
            tvPayAdvance.text = payAdvance
        }
    }

    private fun updateSelectionUI() {
        binding.apply {
            ivPayFull.setImageResource(
                if (selectedPayment == "full") R.drawable.selected_round_btn else R.drawable.unselected_rounded_btn
            )
            ivPayAdvance.setImageResource(
                if (selectedPayment == "advance") R.drawable.selected_round_btn else R.drawable.unselected_rounded_btn
            )
        }
    }

    private fun showPaymentCancelConfirmationDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Wait! Don't leave yet.")
            .setMessage("Payment in progress. Leaving now may cause transaction failure or duplicate charges. Please stay on this page.")
            .setPositiveButton("Cancel") { _, _ ->
                binding.nvCart.show()
                binding.btnBook.show()
                binding.webView.hide()
                binding.webView.stopLoading()
                binding.webView.loadUrl("about:blank")
                Toast.makeText(requireContext(), "Payment cancelled", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Continue Payment") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(false)
            .create()
        dialog.show()
    }

    private fun setUpClick() {
        binding.ivBack.setOnClickListener {
            if (binding.webView.isVisible) {
                showPaymentCancelConfirmationDialog()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.ivPayFull.setOnClickListener {
            selectedPayment = "full"
            updateSelectionUI()
        }

        binding.ivPayAdvance.setOnClickListener {
            selectedPayment = "advance"
            updateSelectionUI()
        }

        binding.btnBook.setOnClickListener {
            hitPanditJiBooking()
        }
    }

    private fun hitPanditJiBooking() {
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

    private fun setObserver() {
        confirmBookingViewModel.getBookPanditji().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        201 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT).show()
                            confirmBookingViewModel.hitBookingPayment(
                                BookingPaymentRequest(
                                    bookingId = it.data.payload.booking.id,
                                    currency = "INR"
                                )
                            )
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
                            binding.nvCart.hide()
                            binding.btnBook.hide()
                            binding.webView.show()
                            setupWebView(
                                it.data.payload.razorpay.payment_link.short_url,
                                it.data.payload.success_url_app
                            )
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

    private fun setupWebView(paymentUrl: String?, successUrl: String) {
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.loadsImagesAutomatically = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.webView.webChromeClient = object : WebChromeClient() {

            private var lastLoggedUrl: String? = null

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                val current = view?.url ?: return
                if (current != lastLoggedUrl) {
                    Log.e("WEBVIEW_CHANGE", "URL changed: $current")
                    lastLoggedUrl = current
                }

                if (newProgress == 100) {
                    Log.e("WEBVIEW_PROGRESS", "Loaded: $current")

                    when {
                        current.startsWith(successUrl) -> {
                            handlePaymentResult(true, current)
                        }

                        current.startsWith("cancel") -> {
                            handlePaymentResult(false, current)
                        }
                    }
                }
            }
        }


        binding.webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                Log.d("WebView", "Loading: $url")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("WebView", "Finished: $url")
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val url = request?.url.toString()
                Log.i("TAG", "shouldOverrideUrlLoading: $url")
                when {
                    url.startsWith(successUrl) -> {
                        handlePaymentResult(true, url)
                        return true
                    }

                    url.startsWith("cancel") -> {
                        handlePaymentResult(false, url)
                        return true
                    }
                }
                return false
            }

        }

        paymentUrl?.let {
            Log.d("WebView", "Loading URL: $it")
            binding.webView.loadUrl(it)
        }
    }

    private fun handlePaymentResult(isSuccess: Boolean, redirectUrl: String) {
        if (!isAdded) return
        if (isSuccess) {
            Toast.makeText(requireContext(), "Payment Successful", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack(R.id.panditJiFragment, false)
        } else {
            Toast.makeText(requireContext(), "Payment Cancelled", Toast.LENGTH_SHORT).show()
            binding.nvCart.show()
            binding.btnBook.show()
            binding.webView.hide()
            binding.webView.stopLoading()
            binding.webView.loadUrl("about:blank")
        }
    }
}
