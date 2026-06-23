package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
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
import androidx.core.net.toUri

class ContactUsFragment : BaseFragment<FragmentContactUsBinding>() {

    private val profileViewModel by activityViewModels<ProfileViewModel>()
    var phoneNumber = ""

    override fun setLayout(): Int {
        return R.layout.fragment_contact_us
    }

    private fun openWhatsApp(phoneNumber: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://wa.me/$phoneNumber".toUri()
                setPackage("com.whatsapp")
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.whatsapp_is_not_installed), Snackbar.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("ContactUsFragment", "Error opening WhatsApp: ${e.message}")
            Snackbar.make(
                requireView(),
                getString(R.string.error_opening_whatsapp), Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun openGmail(email: String) {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = "mailto:$email".toUri() // Email URI scheme
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email)) // Attach the email address
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.no_email_client_installed), Snackbar.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("ContactUsFragment", "Error opening email client: ${e.message}")
            Snackbar.make(
                requireView(),
                getString(R.string.error_opening_email_client), Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun openInstagram(instagramHandle: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = "https://www.instagram.com/$instagramHandle".toUri()
            }

            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.instagram_is_not_installed), Snackbar.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Log.e("ContactUsFragment", "Error opening Instagram: ${e.message}")
            Snackbar.make(
                requireView(),
                getString(R.string.error_opening_instagram), Snackbar.LENGTH_SHORT
            ).show()
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
            val instaHandle = binding.instaId.getString().removePrefix("• Instagram: ")
            openInstagram(instaHandle)
        }
    }

    private fun setObserve() {
        profileViewModel.getContactUsRes().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {

                    val statusCode = it.data?.code
                    when (statusCode) {
                        200 -> {
                            val data = it.data.payload
                            val phoneNumber = data.phone
                            val email = data.email
                            this.phoneNumber = phoneNumber

                            val emailText = "• ${getString(R.string.email_heading)} " + data.email
                            binding.email.text = emailText

                            val instaIdText =
                                "• ${getString(R.string.instagram_heading)} " + data.domain
                            binding.instaId.text = instaIdText

                            val phoneIdText =
                                "• ${getString(R.string.phone_number_heading)} " + data.phone
                            binding.phoneId.text = phoneIdText

                            binding.whatsAppLayoyt.setOnClickListener {
                                openWhatsApp(phoneNumber)
                            }

                            binding.gmaillayout.setOnClickListener {
                                openGmail(email)
                            }

                            binding.instaLayout.setOnClickListener {
                                val instaHandle = data.domain
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