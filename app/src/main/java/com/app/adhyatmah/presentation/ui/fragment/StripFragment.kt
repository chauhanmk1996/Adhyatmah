package com.app.adhyatmah.presentation.ui.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import co.paystack.android.Paystack
import co.paystack.android.PaystackSdk
import co.paystack.android.Transaction
import co.paystack.android.exceptions.ExpiredAccessCodeException
import co.paystack.android.model.Card
import co.paystack.android.model.Charge
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.ACCESS_TOKEN
import com.app.adhyatmah.data.preferences.EMAIL_ID
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.databinding.FragmentStripBinding
import com.app.adhyatmah.payment.paymentIniRequest.PaymentIniRequest
import com.app.adhyatmah.presentation.ui.viewmodel.PaymentViewModel
import com.app.adhyatmah.presentation.utils.CreditCardTextFormatter
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getLength
import com.app.adhyatmah.utils.getString
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import java.util.Calendar
import kotlin.getValue

class StripFragment : BaseFragment<FragmentStripBinding>() {

    private var transaction: Transaction? = null
    private var charge: Charge? = null
    private var isFormattingExpiry = false
    private val paymentViewModel by activityViewModels<PaymentViewModel>()
    var addressId = ""
    var carId = ""
    var address = ""
    var currency = ""
    var amounts = ""
    var token = ""
    var emailId = ""

    override fun setLayout(): Int {
        return R.layout.fragment_strip
    }

    override fun initView(savedInstanceState: Bundle?) {
        addressId = arguments?.getString("addressId").toString()
        carId = arguments?.getString("cartId").toString()
        address = arguments?.getString("add").toString()
        amounts = arguments?.getString("amount").toString()
        currency = arguments?.getString("currency").toString()
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        emailId = Preferences.getStringPreference(requireContext(), EMAIL_ID).toString()

        addTextWatcherToEditText()
        val totalPrice = amounts
        val payAmount = getString(R.string.pay_amount) + totalPrice
        binding.btnPay.text = payAmount
        handleClicks()
        setObserver()
    }

    private fun addTextWatcherToEditText() {
        binding.btnPay.isEnabled = false
        binding.btnPay.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_9baoa3_12dp_radius)
        binding.etCardNumber.addTextChangedListener(CreditCardTextFormatter())

        binding.etExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormattingExpiry) return

                val text = s.toString()
                if (text.length == 2 && !text.contains("/")) {
                    isFormattingExpiry = true
                    val expiryText = "$text/"
                    binding.etExpiry.setText(expiryText)
                    binding.etExpiry.setSelection(binding.etExpiry.getLength())
                    isFormattingExpiry = false
                }
                updatePayButtonState()
            }
        })

        binding.etCvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePayButtonState()
            }
        })
    }

    private fun updatePayButtonState() {
        val s1 = binding.etCardNumber.getString()
        val s2 = binding.etExpiry.getString()
        val s3 = binding.etCvv.getString()

        val isValid = s1.length >= 16 && s2.length == 5 && s3.length == 3
        binding.btnPay.isEnabled = isValid

        val backgroundRes = when {
            s1.isEmpty() || s2.isEmpty() || s3.isEmpty() -> R.drawable.rectangle_while_stoke_black
            isValid -> R.drawable.rectangel_f7f7f7_radius_16
            else -> R.drawable.rectangle_background_4c4a4a_strock_white
        }

        binding.btnPay.background = ContextCompat.getDrawable(requireContext(), backgroundRes)
    }

    private fun handleClicks() {
        binding.btnPay.setOnClickListener {
            if (CommonUtils.isNetworkAvailable(requireContext())) {
                binding.loadingPayOrder.visibility = View.VISIBLE
                binding.btnPay.visibility = View.GONE
                doPayment()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_check_your_internet), Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private fun doPayment() {
        val publicTestKey = "pk_test_e92c871899c7bcd79a3e67670e325bdc17133355"
        PaystackSdk.setPublicKey(publicTestKey)

        val amountInPese = try {
            (amounts.toFloat()).toInt()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0
        }

        if (amountInPese <= 0) {
            Toast.makeText(requireContext(), "Not a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        charge = Charge()
        charge!!.card = loadCardFromForm()

        charge!!.amount = amountInPese
        charge!!.email = emailId
        charge!!.reference = "payment" + Calendar.getInstance().timeInMillis
        charge!!.currency = currency

        try {
            charge!!.putCustomField("Charged From", "Android SDK")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        doChargeCard()
    }

    private fun loadCardFromForm(): Card {
        val cardNumber = binding.etCardNumber.getString()
        val expiryDate = binding.etExpiry.getString()
        val cvc = binding.etCvv.getString()
        val cardNumberWithoutSpace = cardNumber.replace(" ", "")
        val monthValue = expiryDate.substring(0, expiryDate.length.coerceAtMost(2))
        val yearValue = expiryDate.takeLast(2)
        val card: Card = Card.Builder(cardNumberWithoutSpace, 0, 0, "").build()
        card.cvc = cvc
        val sMonth: String = monthValue
        var month = 0
        try {
            month = sMonth.toInt()
        } catch (_: Exception) {
        }

        card.expiryMonth = month
        val sYear: String = yearValue
        var year = 0
        try {
            year = sYear.toInt()
        } catch (_: Exception) {
        }
        card.expiryYear = year

        return card
    }

    private fun doChargeCard() {
        transaction = null
        PaystackSdk.chargeCard(requireActivity(), charge, object : Paystack.TransactionCallback {
            override fun onSuccess(transaction: Transaction) {
                binding.loadingPayOrder.visibility = View.GONE
                binding.btnPay.visibility = View.VISIBLE

                PaymentIniRequest(
                    token,
                    addressId,
                    carId,
                    currency,
                    emailId
                )
                this@StripFragment.transaction = transaction
            }

            override fun beforeValidate(transaction: Transaction) {
                this@StripFragment.transaction = transaction
            }

            override fun onError(error: Throwable, transaction: Transaction) {
                binding.loadingPayOrder.visibility = View.GONE
                binding.btnPay.visibility = View.VISIBLE

                this@StripFragment.transaction = transaction
                if (error is ExpiredAccessCodeException) {
                    this@StripFragment.doChargeCard()
                    return
                }

                if (transaction.reference != null) {
                    Toast.makeText(requireContext(), error.message ?: "", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), error.message ?: "", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun setObserver() {
        paymentViewModel.getPayStackApisResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            val bundle = Bundle()
                            bundle.putString("oderId", data.stripe.order_id)
                            Log.d("Tag", "InitViewdfd ${data.stripe.order_id}")
                            findNavController().navigate(
                                R.id.action_stripFragment_to_congratulationFragment,
                                bundle
                            )
                            Toast.makeText(requireContext(), it.data.message, Toast.LENGTH_SHORT)
                                .show()
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

    override fun onPause() {
        super.onPause()
        binding.loadingPayOrder.visibility = View.GONE
    }
}