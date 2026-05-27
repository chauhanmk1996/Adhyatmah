package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.SELECTED_LANGUAGE_CODE
import com.app.adhyatmah.data.preferences.SELECTED_LANGUAGE_NAME
import com.app.adhyatmah.databinding.FragmentSelectLanguageBinding
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.ChooseLanguageAdapter
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.panditji.data.model.app_language.AppLanguage
import java.util.Locale

class ChooseLanguageFragment : BaseFragment<FragmentSelectLanguageBinding>() {

    private var selectedLanguage: AppLanguage? = null
    private var adapter: ChooseLanguageAdapter? = null


    override fun setLayout(): Int = R.layout.fragment_select_language

    override fun initView(savedInstanceState: Bundle?) {

        selectedLanguage = AppLanguage(
            Preferences.getStringPreference(requireContext(), SELECTED_LANGUAGE_NAME) ?: "English",
            Preferences.getStringPreference(requireContext(), SELECTED_LANGUAGE_CODE) ?: "en"
        )
        Log.i("TAG", "initView: "+selectedLanguage.toString())
        Log.i("TAG", "initView: "+selectedLanguage)

        adapter = ChooseLanguageAdapter(
            CommonUtils.languageList,
            Preferences.getStringPreference(requireContext(), SELECTED_LANGUAGE_CODE) ?: "en"
        ) { selectedLang ->
            selectedLanguage = selectedLang

        }

        binding.rvServices.adapter = adapter

        binding.btnNext.setOnClickListener {
            Log.i("TAG", "btn next: "+selectedLanguage)

            if (selectedLanguage == null) {
                Toast.makeText(requireContext(), "Please select a language", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveLanguageAndApply(selectedLanguage!!)
        }

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
    private fun saveLanguageAndApply(language: AppLanguage) {
        Preferences.setStringPreference(requireContext(), SELECTED_LANGUAGE_NAME, language.name)
        Preferences.setStringPreference(requireContext(), SELECTED_LANGUAGE_CODE, language.code)

        // This is the correct API for Android 13+ and backwards compatible via AppCompat
        val localeList = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(localeList)
        // DO NOT call recreate() — setApplicationLocales handles it automatically
        findNavController().popBackStack()
    }




}

