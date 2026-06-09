package com.app.adhyatmah.presentation.ui.pandit_ji

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentBookingDetailsBinding
import com.app.adhyatmah.domain.model.create_booking.PanditJiDetails
import com.app.adhyatmah.presentation.ui.adapter.FaqAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.BookingDetailViewModel
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class BookingDetailsFragment : Fragment() {

    private val viewmodel by activityViewModels<BookingDetailViewModel>()
    private var _binding: FragmentBookingDetailsBinding? = null
    private val binding get() = _binding!!
    private var panditJiDetails = PanditJiDetails()
    private lateinit var faqAdapter: FaqAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBookingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        panditJiDetails = UserPreference.panditJiDetails
        setUpUi()
        setObserver()
        clickListeners()
    }

    private fun setUpUi() {
        if (!panditJiDetails.poojaSelectFromHomeName.isNullOrEmpty()) {
            viewmodel.hitGetServices(UserPreference.panditJiDetails.id ?: "")
        }
        binding.apply {
            panditJiDetails.image?.let { url ->
                Glide.with(requireContext())
                    .load(url)
                    .placeholder(R.drawable.pamdit_ji)
                    .error(R.drawable.pamdit_ji)
                    .into(ivProfilePic)
            }

            val name = "${panditJiDetails.firstName ?: ""} ${panditJiDetails.lastName ?: ""}"
            tvName.text = name

            val experience =
                "${panditJiDetails.city ?: ""} | ${panditJiDetails.seoContent?.details?.experience ?: ""}"
            tvExperience.text = experience

            val about = "${panditJiDetails.about ?: ""}  ★ 4.8"
            tvAbout.text = about

            tvDescription.text = panditJiDetails.seoContent?.intro?.content?.toString() ?: ""

            tvGotra.text = panditJiDetails.gotra ?: ""

            if (panditJiDetails.verified == true) {
                clVerifiedPandit.show()
            } else {
                clVerifiedPandit.hide()
            }

            if (panditJiDetails.trusted == true) {
                clTrustedService.show()
            } else {
                clTrustedService.hide()
            }

            tvSpecialization.text = panditJiDetails.seoContent?.details?.specialization ?: ""
            tvServices.text = panditJiDetails.seoContent?.details?.services ?: ""
            tvAvailability.text = panditJiDetails.seoContent?.details?.availability ?: ""

            panditJiDetails.seoContent?.faqs?.let { list ->
                faqAdapter = FaqAdapter(list) { pos, isOpen ->
                    faqAdapter.openCloseFaq(pos, isOpen)
                }
                rvFaq.adapter = faqAdapter
            }
        }
    }

    private fun setObserver() {
        viewmodel.getServices().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {

                        200 -> {
                            it.data.payload?.services?.let { list ->
                                list.forEach { service ->
                                    if (service.poojaType == panditJiDetails.poojaSelectFromHomeName) {
                                        UserPreference.panditjiBookingRequest.serviceId = service.id
                                        UserPreference.panditjiBookingRequest.paymentAmount = service.price.toString()
                                        UserPreference.panditjiBookingRequest.advance = service.advance
                                        UserPreference.panditjiBookingRequest.gst = service.gst
                                        UserPreference.panditjiBookingRequest.duration = service.duration
                                        UserPreference.panditjiBookingRequest.poojaType = service.poojaType
                                        UserPreference.panditjiBookingRequest.pujaDescription = service.description
                                    }
                                }
                            }
                            ProcessDialog.dismissDialog(true)
                        }

                        401 -> {
                            ProcessDialog.dismissDialog(true)
                            Toast.makeText(requireActivity(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
                            Log.e("TAG", "Unauthorized access $it")
                        }
                    }
                }

                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.data?.message}", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun clickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvServicesOffered.setOnClickListener {
            if (panditJiDetails.poojaSelectFromHomeName.isNullOrEmpty()) {
                findNavController().navigate(R.id.chooseServiceFragment)
            }else{
                findNavController().navigate(R.id.chooseAddOnFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
