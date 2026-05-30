package com.app.adhyatmah.presentation.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.app.adhyatmah.R
import com.app.adhyatmah.data.preferences.Preferences
import com.app.adhyatmah.data.preferences.SELECTED_LANGUAGE_CODE
import com.app.adhyatmah.data.preferences.SELECTED_LANGUAGE_NAME
import com.app.adhyatmah.databinding.FragmentSelectLanguageBinding
import com.app.adhyatmah.presentation.ui.activity.MainActivity
import com.app.adhyatmah.presentation.ui.pandit_ji.adapter.ChooseLanguageAdapter
import com.app.adhyatmah.utils.base.BaseFragment
import com.app.adhyatmah.utils.common_utils.CommonUtils
import com.app.adhyatmah.utils.hide
import com.app.panditji.data.model.app_language.AppLanguage

class ChooseLanguageFragment : BaseFragment<FragmentSelectLanguageBinding>() {

    private var selectedLanguage: AppLanguage? = null
    private var selectedLanguageCode: String = ""
    private var adapter: ChooseLanguageAdapter? = null
    private var languageList: ArrayList<AppLanguage> = ArrayList()

    override fun setLayout(): Int = R.layout.fragment_select_language

    override fun initView(savedInstanceState: Bundle?) {
        binding.ivBack.hide()
        binding.btnNext.text = getString(R.string.change_language)

        val language = Preferences.getStringPreference(
            requireContext(),
            SELECTED_LANGUAGE_NAME
        ).takeUnless { it.isNullOrEmpty() } ?: "English"

        selectedLanguageCode = Preferences.getStringPreference(
            requireContext(),
            SELECTED_LANGUAGE_CODE
        ).takeUnless { it.isNullOrEmpty() } ?: "en"
        selectedLanguage = AppLanguage(language, selectedLanguageCode)
        languageList = CommonUtils.languageList

        adapter = ChooseLanguageAdapter(languageList, selectedLanguageCode) { selectedLang ->
            selectedLanguage = selectedLang
        }
        binding.rvLanguage.adapter = adapter

        binding.btnNext.setOnClickListener {
            if (selectedLanguage == null) {
                Toast.makeText(requireContext(), "Please select a language", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            saveLanguageAndApply(selectedLanguage!!)
        }
    }

    private fun saveLanguageAndApply(language: AppLanguage) {
        Preferences.setStringPreference(requireContext(), SELECTED_LANGUAGE_NAME, language.name)
        Preferences.setStringPreference(requireContext(), SELECTED_LANGUAGE_CODE, language.code)
        val localeList = LocaleListCompat.forLanguageTags(language.code)
        AppCompatDelegate.setApplicationLocales(localeList)
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finishAffinity()
    }
}