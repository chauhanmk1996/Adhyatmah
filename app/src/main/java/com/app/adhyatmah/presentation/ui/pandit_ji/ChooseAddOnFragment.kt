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
import com.app.adhyatmah.databinding.FragmentChooseAddOnBinding
import com.app.adhyatmah.domain.model.get_services.PujaKit
import com.app.adhyatmah.presentation.ui.adapter.PujaKitAdapter
import com.app.adhyatmah.presentation.ui.pandit_ji.viewModel.BookingDetailViewModel
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.hide
import com.app.adhyatmah.utils.show
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class ChooseAddOnFragment : Fragment() {
    private val viewmodel by activityViewModels<BookingDetailViewModel>()
    private var _binding: FragmentChooseAddOnBinding? = null
    private val binding get() = _binding!!
    private val pujaKitList: ArrayList<PujaKit> = ArrayList()
    private val instantKitList: ArrayList<PujaKit> = ArrayList()
    private lateinit var pujaKitAdapter: PujaKitAdapter
    private lateinit var instantKitAdapter: PujaKitAdapter
    private var hasQuantity: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentChooseAddOnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()
        setObserver()
        onClick()
        skipNextButtonHandle()
        viewmodel.hitGetAddOnKit(UserPreference.panditjiBookingRequest.serviceId ?: "")
    }

    private fun setupRecyclerView() {
        pujaKitAdapter = PujaKitAdapter(
            minusClick = { pos ->
                val quantity = pujaKitList[pos].quantity ?: 0
                pujaKitList[pos].quantity = quantity - 1
                pujaKitAdapter.updateQuantity(pos, quantity - 1)
                skipNextButtonHandle()
            },
            addClick = { pos ->
                val quantity = pujaKitList[pos].quantity ?: 0
                pujaKitList[pos].quantity = quantity + 1
                pujaKitAdapter.updateQuantity(pos, quantity + 1)
                skipNextButtonHandle()
            }
        )
        binding.rvPujaKit.adapter = pujaKitAdapter

        instantKitAdapter = PujaKitAdapter(
            minusClick = { pos ->
                val quantity = instantKitList[pos].quantity ?: 0
                instantKitList[pos].quantity = quantity - 1
                instantKitAdapter.updateQuantity(pos, quantity - 1)
                skipNextButtonHandle()
            },
            addClick = { pos ->
                val quantity = instantKitList[pos].quantity ?: 0
                instantKitList[pos].quantity = quantity + 1
                instantKitAdapter.updateQuantity(pos, quantity + 1)
                skipNextButtonHandle()
            }
        )
        binding.rvInstantKit.adapter = instantKitAdapter
    }

    private fun skipNextButtonHandle() {
        hasQuantity = pujaKitList.any { (it.quantity ?: 0) > 0 } ||
                instantKitList.any { (it.quantity ?: 0) > 0 }

        binding.btnSkipNext.text = if (hasQuantity) {
            getString(R.string.next)
        } else {
            getString(R.string.skip)
        }
    }

    private fun onClick() {
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnSkipNext.setOnClickListener {

            if (hasQuantity) {
                val selectedPujaKitList = ArrayList(pujaKitList.filter { (it.quantity ?: 0) > 0 })
                val selectedInstantKitList =
                    ArrayList(instantKitList.filter { (it.quantity ?: 0) > 0 })

                UserPreference.panditjiBookingRequest.selectedPujaKit =
                    selectedPujaKitList.ifEmpty {
                        null
                    }

                UserPreference.panditjiBookingRequest.selectedInstantKit =
                    selectedInstantKitList.ifEmpty {
                        null
                    }
                findNavController().navigate(R.id.dateTimeSelectionFragment)
            } else {
                UserPreference.panditjiBookingRequest.selectedPujaKit = null
                UserPreference.panditjiBookingRequest.selectedInstantKit = null
                findNavController().navigate(R.id.dateTimeSelectionFragment)
            }
        }
    }

    private fun setObserver() {
        viewmodel.getAddOnKit().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            binding.apply {
                                it.data.payload?.pujaKit?.let { list ->
                                    pujaKitList.clear()
                                    pujaKitList.addAll(list)
                                }

                                if (pujaKitList.isNotEmpty()) {
                                    tvPujaKit.show()
                                    rvPujaKit.show()
                                    pujaKitAdapter.addList(pujaKitList)
                                } else {
                                    tvPujaKit.hide()
                                    rvPujaKit.hide()
                                }

                                it.data.payload?.instantKit?.let { list ->
                                    instantKitList.clear()
                                    instantKitList.addAll(list)
                                }

                                if (instantKitList.isNotEmpty()) {
                                    tvInstantKit.show()
                                    rvInstantKit.show()
                                    instantKitAdapter.addList(instantKitList)
                                } else {
                                    tvInstantKit.hide()
                                    rvInstantKit.hide()
                                }
                            }
                            ProcessDialog.dismissDialog(true)
                        }

                        401 -> {
                            ProcessDialog.dismissDialog(true)
                            Toast.makeText(
                                requireActivity(),
                                it.data.message,
                                Toast.LENGTH_SHORT
                            )
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