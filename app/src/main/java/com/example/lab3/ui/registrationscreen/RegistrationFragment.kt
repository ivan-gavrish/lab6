package com.example.lab3.ui.registrationscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.lab3.R
import com.example.lab3.databinding.FragmentRegistrationBinding
import com.example.lab3.shared.model.GameViewModel

class RegistrationFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // since user still logged in after finishing the app,
        // we need to sign him out to log in or register new user
        viewModel.auth.signOut()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        requireActivity().title = getString(R.string.colormatcher)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_signupFragment)
        }

        binding.btnLogIn.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_logInFragment)
        }

        addMenu()
    }

    /**
     * Add options menu to the tool bar.
     * Menu options:
     * settings - open app settings;
     * exit - exit from the app
     */
    private fun addMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {

            @SuppressLint("RestrictedApi")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_registration_screen, menu)

                if (menu is MenuBuilder) {
                    menu.setOptionalIconsVisible(true)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        findNavController().navigate(R.id.action_registrationFragment_to_settingsFragment)
                        true
                    }

                    R.id.menu_exit -> {
                        requireActivity().finish()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }
}