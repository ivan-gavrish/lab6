package com.example.lab3.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.lab3.BuildConfig
import com.example.lab3.R
import com.example.lab3.databinding.FragmentSettingsBinding
import com.example.lab3.shared.model.GameViewModel

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private lateinit var sharedPref: SharedPreferences
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        requireActivity().title = getString(R.string.settings)
        sharedPref = requireActivity().getSharedPreferences("colormatcher_settings", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.switchHideButton.isChecked = sharedPref.getBoolean("hideButton", false)

        binding.switchHideButton.setOnClickListener {
            if (binding.switchHideButton.isChecked) {
                sharedPref.edit().putBoolean("hideButton", true).apply()
            } else {
                sharedPref.edit().putBoolean("hideButton", false).apply()
            }
        }

        binding.tvVersionNumber.text = getString(R.string.version_number, BuildConfig.VERSION_CODE)
    }
}