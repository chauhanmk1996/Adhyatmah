package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentTrackOrderBinding
import com.app.adhyatmah.utils.base.BaseFragment
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.domain.model.TrackItem
import com.app.adhyatmah.presentation.ui.viewmodel.ProfileViewModel
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class TrackOrderFragment : BaseFragment<FragmentTrackOrderBinding>() {

    private val profileViewModel by activityViewModels<ProfileViewModel>()

    var url = ""
    val trackingList = mutableListOf<TrackItem>()

    override fun setLayout(): Int {
        return R.layout.fragment_track_order
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }
        profileViewModel.shippingUrlData()
        setObserver()

        trackingList.addAll(
            listOf(
                TrackItem(
                    "Package arrived at a Facility, Pathankot,\nPunjab, IN",
                    "16 July, 2024 | 6:23 pm"
                ),
                TrackItem(
                    "Package has left a Facility, Amritsar,\nPunjab, IN",
                    "15 July, 2024 | 4:23 pm"
                ),
                TrackItem(
                    "Package has left a Facility, Amritsar,\nPunjab, IN",
                    "15 July, 2024 | 4:23 pm"
                ),
                TrackItem(
                    "Package has left a Facility, Amritsar,\nPunjab, IN",
                    "15 July, 2024 | 4:23 pm"
                ),
                TrackItem("Order Placed.", "13 July, 2024 | 2:12 pm")
            )
        )
    }

    private fun setObserver() {
        profileViewModel.shippingUrlRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload.shoppingUrls
                            url = data[0].url
                            openWebUrl(url)
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

    private fun openWebUrl(urls: String) {
        val webView = binding.webView
        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return url != null && url.contains("/payment/callback") // Allow all other URLs to load
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Toast.makeText(requireContext(), "Loading...", Toast.LENGTH_SHORT).show()
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?,
            ) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Toast.makeText(
                    requireContext(),
                    "Error loading page: $description",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        webView.loadUrl(urls)
    }
}