package com.app.adhyatmah.presentation.ui.pandit_ji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentBookingDetailsBinding
import com.app.adhyatmah.domain.model.create_booking.PanditJiDetails
import com.app.adhyatmah.presentation.ui.adapter.FaqAdapter
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.bumptech.glide.Glide

class BookingDetailsFragment : Fragment() {

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
        clickListeners()
    }

    private fun setUpUi() {
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

    private fun clickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvServicesOffered.setOnClickListener {
            findNavController().navigate(R.id.chooseServiceFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
