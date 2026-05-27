package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.databinding.FragmentContactUsBinding
import com.app.adhyatmah.presentation.ui.viewmodel.ProfileViewModel
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.ProcessDialog
import com.app.adhyatmah.utils.common_utils.Status
import com.app.adhyatmah.utils.getString
import com.google.android.material.snackbar.Snackbar
import kotlin.getValue

class ContactUsFragment : BaseFragment<FragmentContactUsBinding>() {

    private val profileViewModel by activityViewModels<ProfileViewModel>()
   var  phoneNumber=""

    override fun setLayout(): Int {
       return R.layout.fragment_contact_us

    }

    private fun openWhatsApp(phoneNumber: String) {
        try {
            // Ensure phone number is in the proper format with country code, e.g., "+1234567890"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phoneNumber") // WhatsApp API URL
                setPackage("com.whatsapp") // Optional: Ensure it opens WhatsApp specifically
            }

            // Check if WhatsApp is installed
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Snackbar.make(requireView(), "WhatsApp is not installed", Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ContactUsFragment", "Error opening WhatsApp: ${e.message}")
            Snackbar.make(requireView(), "Error opening WhatsApp", Snackbar.LENGTH_SHORT).show()
        }
    }
    private fun openGmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:$email") // Email URI scheme
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email)) // Attach the email address
            }

            // Check if there's an email client installed
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Snackbar.make(requireView(), "No email client installed", Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ContactUsFragment", "Error opening email client: ${e.message}")
            Snackbar.make(requireView(), "Error opening email client", Snackbar.LENGTH_SHORT).show()
        }
    }
    private fun openInstagram(instagramHandle: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.instagram.com/$instagramHandle") // Instagram URL scheme
            }

            // Check if Instagram is installed
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Snackbar.make(requireView(), "Instagram is not installed", Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ContactUsFragment", "Error opening Instagram: ${e.message}")
            Snackbar.make(requireView(), "Error opening Instagram", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.backArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        profileViewModel.getContactUsData()
        setObserve()
        binding.whatsAppLayoyt.setOnClickListener {
            openWhatsApp(phoneNumber)
        }
        binding.gmaillayout.setOnClickListener {
            openGmail(binding.email.text.toString())
        }
        binding.instaLayout.setOnClickListener {
            val instaHandle = binding.instaId.getString().removePrefix("• Instagram: ") // Extract the Instagram handle
            openInstagram(instaHandle)
        }

    }


    private fun setObserve(){

        profileViewModel.getContactUsRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code // assuming your wrapper contains code
                    when (statusCode) {
                        200 -> {
                            var data = it.data.payload
                            var phoneNumber = data.phone
                            var email = data.email
                            this.phoneNumber = phoneNumber
                            binding.email.text = "• Email: "+ data.email
                            binding.instaId.text = "• Instagram: "+ data.domain
                            binding.phoneId.text = "• Phone Number: "+ data.phone
                            val instaHandle = data.domain

                            binding.whatsAppLayoyt.setOnClickListener {
                                openWhatsApp(phoneNumber)
                            }
                            binding.gmaillayout.setOnClickListener {
                                openGmail(email)
                            }
                            binding.instaLayout.setOnClickListener {
                                openInstagram(instaHandle)
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

    }


}