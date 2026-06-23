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
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.UserPreference
import com.app.adhyatmah.databinding.FragmentChooseServiceBinding
import com.app.adhyatmah.domain.model.get_services.Puja
import com.app.adhyatmah.presentation.ui.adapter.PujaAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.BookingDetailViewModel
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class ChooseServiceFragment : Fragment() {
    private val viewmodel by activityViewModels<BookingDetailViewModel>()
    private var _binding: FragmentChooseServiceBinding? = null
    private val binding get() = _binding!!

    private val pujaList: ArrayList<Puja> = ArrayList()
    private lateinit var pujaAdapter: PujaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChooseServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setObserver()
        onClick()

        viewmodel.hitGetServices(UserPreference.panditJiDetails.id ?: "")
    }

    private fun setupRecyclerView() {
        pujaAdapter = PujaAdapter { selectedPuja ->
            selectedPuja.let {
                UserPreference.panditjiBookingRequest.serviceId = it.id
                UserPreference.panditjiBookingRequest.paymentAmount = it.price.toString()
                UserPreference.panditjiBookingRequest.advance = it.advance
                UserPreference.panditjiBookingRequest.gst = it.gst
                UserPreference.panditjiBookingRequest.duration = it.duration
                UserPreference.panditjiBookingRequest.poojaType = it.poojaType
                UserPreference.panditjiBookingRequest.pujaDescription = it.description
                findNavController().navigate(R.id.chooseAddOnFragment)
            }
        }
        binding.rvPujas.adapter = pujaAdapter
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
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
                                pujaList.clear()
                                pujaList.addAll(list)
                                pujaAdapter.addList(pujaList)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}