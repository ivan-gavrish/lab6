package com.example.lab3.ui.gamemenuscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.lab3.shared.model.GameViewModel
import com.example.lab3.R
import com.example.lab3.databinding.FragmentGameMenuBinding

class GameMenuFragment : Fragment() {
    private lateinit var binding: FragmentGameMenuBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = getString(R.string.colormatcher)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_gameMenuFragment_to_registrationFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnStartGame.setOnClickListener {
            val radioButton: RadioButton = binding.root.findViewById(binding.rgGameDifficulties.checkedRadioButtonId)
            viewModel.setDifficulty(radioButton.text.toString())
            findNavController().navigate(R.id.action_gameMenuFragment_to_gameFragment)
        }

        addMenu()
    }

    /**
     * Add options menu to the tool bar.
     * Menu options:
     * settings - open app settings;
     * sign out - sign out current user;
     * exit - exit from the app
     */
    private fun addMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            @SuppressLint("RestrictedApi")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_menu_other_screens, menu)

                if (menu is MenuBuilder) {
                    menu.setOptionalIconsVisible(true)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_settings -> {
                        findNavController().navigate(R.id.action_gameMenuFragment_to_settingsFragment)
                        true
                    }
                    R.id.menu_sign_out -> {
                        if (isUserLoggedIn()) {
                            viewModel.auth.signOut()
                            findNavController().navigate(R.id.action_gameMenuFragment_to_registrationFragment)
                        } else {
                            Toast.makeText(requireContext(), "Log in first!", Toast.LENGTH_SHORT)
                        }
                        true
                    }
                    R.id.menu_exit -> {
                        requireActivity().finish();
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    /**
     * Check if user logged in in the account
     *
     * @return true if user logged in, false otherwise
     */
    private fun isUserLoggedIn(): Boolean {
        return viewModel.auth.currentUser != null
    }
}