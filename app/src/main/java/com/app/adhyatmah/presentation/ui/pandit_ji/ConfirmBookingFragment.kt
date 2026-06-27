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
import com.app.adhyatmah.domain.model.create_booking.BookPanditJiRequest
import com.app.adhyatmah.domain.model.create_booking.PanditjiBookingRequest
import com.app.adhyatmah.domain.model.create_booking.PujaSamagri
import com.app.adhyatmah.domain.model.get_services.PujaKit
import com.app.adhyatmah.presentation.ui.adapter.PujaKitAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.ConfirmBookingViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getDigit
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.util.Locale
import kotlin.String

class ConfirmBookingFragment : BaseFragment<FragmentConfirmBookingBinding>() {

    private val confirmBookingViewModel by activityViewModels<ConfirmBookingViewModel>()
    private var token = ""
    private var emailId = ""
    private val addOnPujaKitList: ArrayList<PujaKit> = ArrayList()
    private lateinit var addOnKitAdapter: PujaKitAdapter
    private var selectedPayment: String = "full"
    private lateinit var bookingRequest: PanditjiBookingRequest
    private var fullPayment: Double = 0.0
    private var advancePayment: Double = 0.0

    override fun setLayout(): Int = R.layout.fragment_confirm_booking

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        emailId = Preferences.getStringPreference(requireContext(), EMAIL_ID1).toString()
        setUpAddOnKitRecycler()
        setUi()
        updateSelectionUI()
        setUpClick()
        setObserver()

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

                    if (addOnPujaKitList.isEmpty()) {
                        binding.tvAddonPujaKitDetails.hide()
                        binding.rvPujaKit.hide()
                        binding.tvAddOnsPriceHeading.hide()
                        binding.tvAddOnsPrice.hide()
                        binding.view3.hide()
                        binding.tvDeliveryChargeHeading.hide()
                        binding.tvDeliveryCharge.hide()
                        binding.view4.hide()
                        binding.tvHandlingChargeHeading.hide()
                        binding.tvHandlingCharge.hide()
                        binding.view5.hide()
                    }
                } else {
                    addOnPujaKitList[pos].quantity = quantity - 1
                    addOnKitAdapter.updateQuantity(pos, quantity - 1)
                }
                addOnPriceUpdate()
            },
            addClick = { pos ->
                val quantity = addOnPujaKitList[pos].quantity ?: 0
                addOnPujaKitList[pos].quantity = quantity + 1
                addOnKitAdapter.updateQuantity(pos, quantity + 1)
                addOnPriceUpdate()
            }
        )
        binding.rvPujaKit.adapter = addOnKitAdapter
    }

    private fun setUi() {
        binding.apply {
            bookingRequest = UserPreference.panditjiBookingRequest

            val panditJiDetails = UserPreference.panditJiDetails

            //Pandit Ji Details
            panditJiDetails.image?.let { url ->
                Glide.with(requireContext())
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivPanditPic)

            }
            val panditName = "${panditJiDetails.firstName ?: ""} ${panditJiDetails.lastName ?: ""}"
            tvPanditName.text = panditName
            tvPanditAddress.text = panditJiDetails.address ?: ""

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

            val formattedLanguage = bookingRequest.selectedLanguage?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString()
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

                tvDeliveryChargeHeading.show()
                tvDeliveryCharge.show()
                view4.show()
                tvHandlingChargeHeading.show()
                tvHandlingCharge.show()
                view5.show()

            } else {
                tvAddonPujaKitDetails.hide()
                rvPujaKit.hide()
                tvAddOnsPriceHeading.hide()
                tvAddOnsPrice.hide()
                view3.hide()
                tvDeliveryChargeHeading.hide()
                tvDeliveryCharge.hide()
                view4.hide()
                tvHandlingChargeHeading.hide()
                tvHandlingCharge.hide()
                view5.hide()
            }

            //Payment Details
            val serviceAmount: Double = (bookingRequest.paymentAmount ?: "0.0").toDouble()
            val servicePrice = "₹ ${serviceAmount.getDigit()}"
            tvServicePrice.text = servicePrice

            val deliveryCharge = getString(R.string.free)
            tvDeliveryCharge.text = deliveryCharge

            val handlingCharge = getString(R.string.free)
            tvHandlingCharge.text = handlingCharge

            val platformFee = getString(R.string.free)
            tvPlatformFee.text = platformFee

            addOnPriceUpdate()
        }
    }

    override fun onResume() {
        super.onResume()
        if (UserPreference.address1.isEmpty() && UserPreference.address2.isEmpty() && UserPreference.city.isEmpty() && UserPreference.province.isEmpty()) {
            binding.btnSelectAddress.show()
            binding.tvSelectedAddress.hide()
            binding.tvChange.hide()
        } else {
            binding.btnSelectAddress.hide()
            binding.tvSelectedAddress.show()
            binding.tvChange.show()
            val address = listOf(
                UserPreference.address1,
                UserPreference.address2,
                UserPreference.city,
                UserPreference.province,
                UserPreference.country,
                UserPreference.zip
            ).map { it.trim() }
                .filter { it.isNotBlank() }
                .joinToString(", ")
            binding.tvSelectedAddress.text = address
        }
    }

    private fun addOnPriceUpdate() {
        var addOnPrice = 0.0
        addOnPujaKitList.forEach { addOn ->
            addOnPrice += (addOn.price ?: 0.0) * (addOn.quantity ?: 1)
        }
        binding.apply {
            val addOnPriceText = "₹ ${addOnPrice.getDigit()}"
            tvAddOnsPrice.text = addOnPriceText

            val serviceAmount: Double = (bookingRequest.paymentAmount ?: "0.0").toDouble()
            fullPayment = serviceAmount + addOnPrice
            val payFull = "₹ ${fullPayment.getDigit()}"
            tvPayFull.text = payFull

            val paymentAmount = bookingRequest.paymentAmount?.toDoubleOrNull() ?: 0.0
            val advance = bookingRequest.advance?.toDoubleOrNull() ?: 0.0
            advancePayment = (paymentAmount * advance / 100) + addOnPrice
            val payAdvanceText = "₹ ${advancePayment.getDigit()}"
            tvPayAdvance.text = payAdvanceText
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
            .setTitle(getString(R.string.wait_don_t_leave_yet))
            .setMessage(getString(R.string.payment_in_progress_leaving_now_may_cause_transaction_failure_or_duplicate_charges_please_stay_on_this_page))
            .setPositiveButton(getString(R.string.cancel)) { _, _ ->
                binding.nvCart.show()
                binding.btnBook.show()
                binding.webView.hide()
                binding.webView.stopLoading()
                binding.webView.loadUrl("about:blank")
                Toast.makeText(requireContext(),
                    getString(R.string.payment_cancelled), Toast.LENGTH_SHORT).show()
            }

            .setNegativeButton(getString(R.string.continue_payment)) { dialog, _ ->
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

        binding.btnSelectAddress.setOnClickListener {
            val bundle = Bundle().apply {
                putString("from", "bookPanditji")
            }
            findNavController().navigate(R.id.mangeAddressFragment, bundle)
        }

        binding.tvChange.setOnClickListener {
            val bundle = Bundle().apply {
                putString("from", "bookPanditji")
            }
            findNavController().navigate(R.id.mangeAddressFragment, bundle)
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
        val panditJiDetails = UserPreference.panditJiDetails
        val language: ArrayList<String> = ArrayList()
        language.add(bookingRequest.selectedLanguage ?: "")

        val pujaKit: ArrayList<String> = ArrayList()
        bookingRequest.selectedPujaKit?.forEach { kit ->
            pujaKit.add(kit.id)
        }

        val instantKit: ArrayList<String> = ArrayList()
        bookingRequest.selectedInstantKit?.forEach { kit ->
            instantKit.add(kit.id)
        }

        val paymentAmount: Double = if (selectedPayment == "advance") {
            advancePayment
        } else {
            fullPayment
        }
        val address = listOf(
            UserPreference.address1,
            UserPreference.address2,
            UserPreference.city,
            UserPreference.province,
            UserPreference.country,
            UserPreference.zip
        ).map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(", ")

        val bookPanditJiRequest = BookPanditJiRequest(
            vendorId = panditJiDetails.id ?: "",
            address = address,
            serviceId = bookingRequest.serviceId ?: "",
            poojaType = bookingRequest.poojaType ?: "",
            `package` = "Standard",
            dateTime = bookingRequest.dateTime ?: "",
            duration = bookingRequest.duration ?: "",
            language = language,
            pujaSamagri = PujaSamagri(
                pujaKit = pujaKit,
                instantKit = instantKit,
            ),
            paymentAmount = paymentAmount
        )
        confirmBookingViewModel.hitBookPanditJi(bookPanditJiRequest)
    }

    private fun setObserver() {
        confirmBookingViewModel.getBookPanditJi().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        201 -> {
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
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
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
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
                            handlePaymentResult(true)
                        }

                        current.startsWith("cancel") -> {
                            handlePaymentResult(false)
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
                        handlePaymentResult(true)
                        return true
                    }

                    url.startsWith("cancel") -> {
                        handlePaymentResult(false)
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

    private fun handlePaymentResult(isSuccess: Boolean) {
        if (!isAdded) return
        if (isSuccess) {
            Toast.makeText(requireContext(),
                getString(R.string.payment_successful), Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_profileFragment_to_bookingFragment)
        } else {
            Toast.makeText(requireContext(), getString(R.string.payment_cancelled), Toast.LENGTH_SHORT).show()
            binding.nvCart.show()
            binding.btnBook.show()
            binding.webView.hide()
            binding.webView.stopLoading()
            binding.webView.loadUrl("about:blank")
        }
    }
}