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
    var address= ""
    var currency_ghs=""
    var amounts = ""
    var token =""
    var emailId = ""


    override fun setLayout(): Int {
        return R.layout.fragment_strip
    }

    override fun initView(savedInstanceState: Bundle?) {

        addressId = arguments?.getString("addressId").toString()
        carId = arguments?.getString("cartId").toString()
        address = arguments?.getString("add").toString()
        amounts = arguments?.getString("amount").toString()
        currency_ghs = arguments?.getString("currency").toString()
        token = Preferences.getStringPreference(requireContext(), ACCESS_TOKEN).toString()
        emailId = Preferences.getStringPreference(requireContext(), EMAIL_ID).toString()

        addTextWatcherToEditText()
//        val totalPrice = "₦20,000"
        val totalPrice = amounts
        binding.btnPay.text = "Pay Amount" +totalPrice     //getString(R.string.pay_amount, totalPrice)
        handleClicks()

        setObserver()

           /* val request = PaymentIniRequest(
                token,
                addressId,
                carId,
                currency_ghs,
                emailId
            )
            paymentViewModel.createPayStackOrder(request)

            Log.d("Paystack", "Charge APIs Request: ${token+"\n"+" \n"+addressId+" \n"+carId+"\n"+currency_ghs+"\n"+emailId}")

*/


    }
    private fun addTextWatcherToEditText() {
        binding.btnPay.isEnabled = false
        binding.btnPay.background = ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_9baoa3_12dp_radius)

        // Card number formatting
        binding.etCardNumber.addTextChangedListener(CreditCardTextFormatter())

        // Expiry formatting
        binding.etExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormattingExpiry) return

                val text = s.toString()
                if (text.length == 2 && !text.contains("/")) {
                    isFormattingExpiry = true
                    binding.etExpiry.setText("$text/")
                    binding.etExpiry.setSelection(binding.etExpiry.getLength())
                    isFormattingExpiry = false
                }
                updatePayButtonState()
            }
        })

        // CVV watcher
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
  /*  private fun addTextWatcherToEditText(){

        //Make button UnClickable for the first time
        binding.btnPay.isEnabled = false
        binding.btnPay.background = ContextCompat.getDrawable(requireContext(), R.drawable.rectangle_9baoa3_12dp_radius)

        //make the button clickable after detecting changes in input field
        val watcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                val s1 = binding.etCardNumber.getString()
                val s2 = binding.etExpiry.getString()
                val s3 = binding.etCvv.getString()

                //check if they are empty, make button unclickable
                if (s1.isEmpty() || s2.isEmpty() || s3.isEmpty()) {
                    binding.btnPay.isEnabled = false
                    binding.btnPay.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.rectangle_while_stoke_black
                    )
                }


                //check the length of all edit text, if meet the required length, make button clickable
                if (s1.length >= 16 && s2.length == 5 && s3.length == 3) {
                    binding.btnPay.isEnabled = true
                    binding.btnPay.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.rectangel_f7f7f7_radius_16
                    )
                }

                //if edit text doesn't meet the required length, make button unclickable. You could use else statement from the above if
                if (s1.length < 16 || s2.length < 5 || s3.length < 3) {
                    binding.btnPay.isEnabled = false
                    binding.btnPay.background = ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.rectangle_background_4c4a4a_strock_white
                    )
                }

                //add a slash to expiry date after first two character(month)
                if (s2.length == 2) {
                    if (start == 2 && before == 1 && !s2.contains("/")) {
                        binding.etExpiry.setText("expiry space"+s2)
                        binding.etExpiry.setSelection(binding.etExpiry.text.length)
                    } else {
                        binding.etExpiry.setText(requireContext().getString(R.string.expiry_space.toInt(), s2));
                        binding.etExpiry.setSelection(3)
                    }
                }


            }

            override fun afterTextChanged(s: Editable) {

            }
        }

        //add text watcher
        binding.etCardNumber.addTextChangedListener(CreditCardTextFormatter()) //helper class for card number formatting
        binding.etExpiry.addTextChangedListener(watcher)
        binding.etCvv.addTextChangedListener(watcher)


    }
  */
  private fun handleClicks(){

        //on click of pay button
        binding.btnPay.setOnClickListener {

            //here you can check for network availability first, if the network is available, continue
            if (CommonUtils.isNetworkAvailable(requireContext())) {

                //show loading
                binding.loadingPayOrder.visibility = View.VISIBLE
                binding.btnPay.visibility = View.GONE

                //perform payment
                doPayment()

            } else {

                Toast.makeText(requireContext(), "Please check your internet", Toast.LENGTH_LONG).show()

            }

        }
    }
    private fun doPayment(){

        val publicTestKey = "pk_test_e92c871899c7bcd79a3e67670e325bdc17133355"

        //set public key
        PaystackSdk.setPublicKey(publicTestKey)

        // initialize the charge
        /*charge = Charge()
        charge!!.card = loadCardFromForm()

        charge!!.amount = 20000
        charge!!.email = emailId
        charge!!.reference = "payment" + Calendar.getInstance().timeInMillis
        // charge!!.currency = "NGN"
        charge!!.currency = "GHS"
   */
        // initialize the charge

        val amountInPese = try {
            (amounts.toFloat()).toInt()// Convert GHS to pesewas
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            0 // Or show error message
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
        // charge!!.currency = "NGN"
        charge!!.currency = currency_ghs

        try {
            charge!!.putCustomField("Charged From", "Android SDK")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        doChargeCard()

    }
    private fun loadCardFromForm(): Card {

        //validate fields

        val cardNumber = binding.etCardNumber.getString()
        val expiryDate = binding.etExpiry.getString()
        val cvc = binding.etCvv.getString()

        //formatted values
        val cardNumberWithoutSpace = cardNumber.replace(" ", "")
        val monthValue = expiryDate.substring(0, expiryDate.length.coerceAtMost(2))
        val yearValue = expiryDate.takeLast(2)

        //build card object with ONLY the number, update the other fields later
        val card: Card = Card.Builder(cardNumberWithoutSpace, 0, 0, "").build()

        //update the cvc field of the card
        card.cvc = cvc

        //validate expiry month;
        val sMonth: String = monthValue
        var month = 0
        try {
            month = sMonth.toInt()
        } catch (ignored: Exception) {
        }

        card.expiryMonth = month

        //validate expiry year
        val sYear: String = yearValue
        var year = 0
        try {
            year = sYear.toInt()
        } catch (ignored: Exception) {
        }
        card.expiryYear = year

        return card

    }
    /*private fun loadCardFromForm(): Card {
        val cardNumber = binding.etCardNumber.getString().replace(" ", "")
        val expiryDate = binding.etExpiry.getString()
        val cvc = binding.etCvv.getString()

        val month = expiryDate.substringBefore("/").toIntOrNull() ?: 0
        val year = expiryDate.substringAfter("/").toIntOrNull() ?: 0

        return Card.Builder(cardNumber, month, year, cvc).build()
    }
*/
    private fun doChargeCard(){

        transaction = null

        PaystackSdk.chargeCard(requireActivity(), charge, object : Paystack.TransactionCallback {

            // This is called only after transaction is successful
            override fun onSuccess(transaction: Transaction) {

                Log.d("Paystack", "Transaction Successful")
                Log.d("Paystack", "Reference: ${transaction.reference}")
                Log.d("Paystack", "Reference: ${transaction.toString()}")

                Log.d("Paystack", "Charge Email: ${charge?.email}")
                Log.d("Paystack", "Charge Amount: ${charge?.amount}")
                Log.d("Paystack", "Charge Currency: ${charge?.currency}")
                Log.d("Paystack", "Charge Reference: ${charge?.reference}")

                //hide loading
                binding.loadingPayOrder.visibility = View.GONE
                binding.btnPay.visibility = View.VISIBLE

                val request = PaymentIniRequest(
                    token,
                    addressId,
                    carId,
                    currency_ghs,
                    emailId
                )
//                paymentViewModel.createPayStackOrder(request)


                Log.d("Paystack", "Charge APIs Request: ${token+"\n"+" \n"+addressId+" \n"+carId+"\n"+currency_ghs+"\n"+emailId}")

                Log.d("TAG", "onSuccess: $transaction \n charge $charge")
                //successful, show a toast or navigate to another activity or fragment
//                Toast.makeText(requireContext(), "Yeeeee!!, Payment was successful", Toast.LENGTH_LONG).show()

                this@StripFragment.transaction = transaction

                //now you can store the transaction reference, and perform a verification on your backend server

            }


            override fun beforeValidate(transaction: Transaction) {

                this@StripFragment.transaction = transaction

            }

            override fun onError(error: Throwable, transaction: Transaction) {

                //stop loading
                binding.loadingPayOrder.visibility = View.GONE
                binding.btnPay.visibility =  View.VISIBLE

                // If an access code has expired, simply ask your server for a new one
                // and restart the charge instead of displaying error

                this@StripFragment.transaction = transaction
                if (error is ExpiredAccessCodeException) {
                    this@StripFragment.doChargeCard()
                    return
                }


                if (transaction.reference != null) {

                    Toast.makeText(requireContext(), error.message?:"", Toast.LENGTH_LONG).show()

                    //also you can do a verification on your backend server here

                } else {

                    Toast.makeText(requireContext(), error.message?:"", Toast.LENGTH_LONG).show()

                }
            }

        })

    }

    private fun setObserver(){
        paymentViewModel.getPayStackApisResponse().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload

                            val bundle = Bundle()
                            bundle.putString("oderId",data.stripe.order_id)
                            Log.d("Tag","InitViewdfd ${data.stripe.order_id}")
                            findNavController().navigate(R.id.action_stripFragment_to_congratulationFragment, bundle)
//
                            Toast.makeText(requireContext(),it.data.message,Toast.LENGTH_SHORT).show()
//                            findNavController().navigate(R.id.action_paymentMethodFragment_to_congratulationFragment)
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