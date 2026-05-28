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

    /*private val profileViewModel by activityViewModels<ProfileViewModel>()
    var isAddressFetched = false*/
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

        /*setFragmentResultListener("requestKey") { _, bundle ->
            Log.i("TAG", "onViewCreated: fragment result listener called")
            val user = bundle.getParcelable<Addresse>("resultKey")
            user?.let {
                binding.localityEt.setText(it.address1)
                binding.pinCodeEt.setText(it.zip)
                binding.cityEt.setText(it.city)
                binding.state.setText(it.province)
                binding.country.setText(it.country)
            }
        }
        Log.i("TAG", "onViewCreated: ${UserPreference.panditjiBookingRequest.address}")
        Log.i("TAG", "onViewCreated: locality " + binding.localityEt.text)

        if (!isAddressFetched) {
            val token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
            val request = ManageAddressRequest()
            request.accessToken = token
            profileViewModel.getAddressData(request)
        }
        binding.btnNext.setOnClickListener {
            setupNextButton()
        }*/
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

        /*binding.changeAddress.setOnClickListener {
            val bundle = Bundle().apply {
                putString("from", "bookPanditji")
            }
            findNavController().navigate(R.id.mangeAddressFragment, bundle)
        }*/
    }


    /*private fun setupNextButton() {
        val locality = binding.localityEt.getString()
        val pinCode = binding.pinCodeEt.getString()
        val city = binding.cityEt.getString()
        val state = binding.state.getString()
        val country = binding.country.getString()

        // Validation for each field
        when {
            locality.isEmpty() -> {
                Toast.makeText(requireContext(), "Please enter your locality", Toast.LENGTH_SHORT)
                    .show()
                binding.localityEt.requestFocus()
                return
            }

            pinCode.isEmpty() -> {
                Toast.makeText(requireContext(), "Please enter your pincode", Toast.LENGTH_SHORT)
                    .show()
                binding.pinCodeEt.requestFocus()
                return
            }

            city.isEmpty() -> {
                Toast.makeText(requireContext(), "Please enter your city", Toast.LENGTH_SHORT)
                    .show()
                binding.cityEt.requestFocus()
                return
            }

            state.isEmpty() -> {
                Toast.makeText(requireContext(), "Please enter your state", Toast.LENGTH_SHORT)
                    .show()
                binding.state.requestFocus()
                return
            }

            country.isEmpty() -> {
                Toast.makeText(requireContext(), "Please enter your country", Toast.LENGTH_SHORT)
                    .show()
                binding.country.requestFocus()
                return
            }
        }

        // Combine all parts into one comma-separated address
        val fullAddress = "$locality, $pinCode, $city, $state, $country"
        Log.i("TAG", "setupNextButton: Full Address: $fullAddress")
        UserPreference.panditjiBookingRequest.address = fullAddress
        findNavController().navigate(R.id.chooseServiceFragment)
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /*@SuppressLint("SuspiciousIndentation")
    private fun setObserver() {
        profileViewModel.getCustomerAddressRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.addresses

                            val allAddresses = it.data?.payload?.addresses

                            val validAddresses = allAddresses?.filter { address ->
                                !address.name.isNullOrBlank() && !address.address1.isNullOrBlank()
                            }


                            if (data.isNotEmpty()) {
                                val address = data[0]
                                binding.localityEt.setText(address.address1)
                                binding.pinCodeEt.setText(address.zip)
                                binding.cityEt.setText(address.city)
                                binding.state.setText(address.province)
                                binding.country.setText(address.country)
                                isAddressFetched = true
                            }
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
    }*/
}
