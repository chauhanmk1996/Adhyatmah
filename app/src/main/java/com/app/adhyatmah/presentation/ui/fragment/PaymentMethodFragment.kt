package com.app.adhyatmah.presentation.ui.fragment
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.adhyatmah.R
import com.app.adhyatmah.data.local.PaymentMethod
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.ADDRESS_ID
import com.app.adhyatmah.data.preferences.CART_ID
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.FragmentPaymentMethodBinding
import com.app.adhyatmah.payment.payment.Payload
import com.app.adhyatmah.presentation.ui.adapter.PaymentMethodAdapter
import com.app.adhyatmah.presentation.ui.viewmodel.PaymentViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.google.android.material.snackbar.Snackbar

class PaymentMethodFragment : BaseFragment<FragmentPaymentMethodBinding>() {
    lateinit var paymentMethodAdapter: PaymentMethodAdapter
    val paymentList = mutableListOf<PaymentMethod>()
    private val viewmodel by activityViewModels<PaymentViewModel>()

    var addressId =""
    var carId =""
    var address=""

    override fun setLayout(): Int {
        return R.layout.fragment_payment_method
    }

    override fun initView(savedInstanceState: Bundle?) {

        addressId = arguments?.getString(ADDRESS_ID).toString()
        carId = arguments?.getString(CART_ID).toString()
        address = arguments?.getString("address").toString()

        val token = Preferences.getStringPreference(requireContext(),ACCESS_TOKEN)
        Log.d("TAG", "initView: $addressId \n ,$carId \n ,$token")

        binding.backImg.setOnClickListener {
            findNavController().popBackStack()
        }
        clickConfirms()
//        clickConfirm(addressId,carId,token)
        viewmodel.hitPaymentTypeAPI()
        setObserver()

        /*binding.selectPaymentTv.setOnClickListener {
        }*/
    }
    fun setAdapter(dataList: List<Payload>) {
        paymentList.clear()
       /* paymentList.addAll(listOf(
            PaymentMethod("Credit or Debit Card", R.drawable.card_icon, R.drawable.arrow_right_grey),
            PaymentMethod("Other UPI Apps", R.drawable.upi_icon, R.drawable.selected_round_btn),
            PaymentMethod("Net Banking", R.drawable.net_banking_icon, R.drawable.unselected_rounded_btn),
            PaymentMethod("Cash on Delivery", R.drawable.cod_icon, R.drawable.unselected_rounded_btn)
        ))*/



        paymentList.addAll(dataList.map { dataList ->
            PaymentMethod(dataList.id, dataList.name,dataList.type,dataList.icon,false)
        })
        paymentMethodAdapter = PaymentMethodAdapter(paymentList)
        binding.paymentMethodRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.paymentMethodRecycler.adapter = paymentMethodAdapter

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    fun clickConfirms() {

        binding.continueBtn.setOnClickListener {
            val selectedMethod = paymentMethodAdapter.getSelectedPaymentMethod()

            if (selectedMethod == null) {
                // No payment method selected — show a warning
                Toast.makeText(requireContext(), "Please select a payment method", Toast.LENGTH_SHORT).show()
            } else {
                val bundle = Bundle().apply {
                    putString("payment_type", selectedMethod.name) // example
                    putString(ADDRESS_ID, arguments?.getString(ADDRESS_ID))
                    putString(CART_ID, arguments?.getString(CART_ID))
                }
                if (selectedMethod.name == "Cash on Delivery") {
                    // Call your Cash on Delivery API here
                    /* var request = CreaterOrderRequest()

                     request.addressId = addressId
                     request.cartId = carId
                     request.accessToken =token
                     viewmodel.hipAPICreateCODOrder(request)*/

                    val bundle = Bundle()
                    bundle.putString("paymentMethod",selectedMethod.name)
                    bundle.putString("addressId",addressId)
                    bundle.putString("cartId",carId)
                    bundle.putString("add",address)

                    findNavController().navigate(R.id.action_paymentMethodFragment_to_confirmOrderFragment, bundle)

                    Log.d("TAG","SENDDetails: ${selectedMethod.name},\n $addressId,\n $carId}")


                }else if(selectedMethod.name == "Credit or Debit Card"){

                    val bundle = Bundle()
                    bundle.putString("paymentMethod",selectedMethod.name)

                    bundle.putString("addressId",addressId)
                    bundle.putString("cartId",carId)
                    bundle.putString("add",address)

                    findNavController().navigate(R.id.action_paymentMethodFragment_to_confirmOrderFragment, bundle)

//                  findNavController().navigate(R.id.action_paymentMethodFragment_to_stripFragment)
                }
                else {
                    Toast.makeText(requireContext(), "Selected method is not Cash on Delivery", Toast.LENGTH_SHORT).show()
                    // Optionally navigate or handle other payment types here
                }

                //  findNavController().navigate(R.id.action_paymentMethodFragment_to_confirmOrderFragment, bundle)
            }
        }

    }



    private fun setObserver(){
        viewmodel.getCOD().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload.order
                            Toast.makeText(requireContext(),it.data.message,Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_paymentMethodFragment_to_congratulationFragment)
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
        viewmodel.getPaymentMethodApiResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload
                            Toast.makeText(requireContext(),it.data.message,Toast.LENGTH_SHORT).show()
                            setAdapter(data)
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



}