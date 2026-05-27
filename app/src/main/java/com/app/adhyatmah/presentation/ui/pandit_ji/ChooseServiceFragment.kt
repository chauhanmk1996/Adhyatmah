package com.app.adhyatmah.presentation.ui.pandit_ji

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentChooseServiceBinding
import com.app.adhyatmah.domain.model.get_services.GetServicesResponse
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.ServiceAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.BookingDetailViewModel
import com.app.adhyatmah.utils.MyApplication
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue


class ChooseServiceFragment : Fragment() {
    private val viewmodel by activityViewModels<BookingDetailViewModel>()
    private var _binding: FragmentChooseServiceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ServiceAdapter
    private var selectedService: GetServicesResponse.Payload.Service? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setObserver()
        val token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN)
        Log.i("TAG", "onViewCreated: "+token)
        viewmodel.hitGetServices(UserPreference.panditjiBookingRequest.vendorId ?: "")
        binding.btnNext.setOnClickListener {
            Log.i("TAG", "selectedService: "+selectedService)
            selectedService?.let {
                UserPreference.panditjiBookingRequest.serviceId = it.id
                UserPreference.panditjiBookingRequest.paymentAmount = it.price.toString()
                UserPreference.panditjiBookingRequest.advance = it.advance
                UserPreference.panditjiBookingRequest.gst = it.gst
                UserPreference.panditjiBookingRequest.duration = it.duration
                UserPreference.panditjiBookingRequest.poojaType = it.poojaType
                UserPreference.panditjiBookingRequest.pujaDescription = it.description
                findNavController().navigate(R.id.dateTimeSelectionFragment)
            } ?: Toast.makeText(requireContext(), "Please select a puja", Toast.LENGTH_SHORT).show()
        }
        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = ServiceAdapter(mutableListOf()) {
            selectedService = it
        }
        binding.rvServices.layoutManager = LinearLayoutManager(requireContext())
        binding.rvServices.adapter = adapter
    }
    private fun setObserver() {
        viewmodel.getServices().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {

                        200 -> {
                            binding.apply {
                                adapter.updateServices(it.data.payload.services.toMutableList())
                            }
                            ProcessDialog.dismissDialog(true)

                        }
                        401 -> {
                            ProcessDialog.dismissDialog(true)

                            Toast.makeText(requireActivity(),it.data.message,Toast.LENGTH_SHORT).show()
                            Log.e("TAG", "Unauthorized access $it")
                        }
                    }
                }
                Status.LOADING -> {
                    ProcessDialog.showDialog(requireActivity(), true)
                }

                Status.ERROR -> {
                    ProcessDialog.dismissDialog(true)
                    Snackbar.make(requireView(), "${it.data?.message}", Snackbar.LENGTH_SHORT).show()
                }
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
