package com.example.lab3.ui.gameratingsscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.lab3.R
import com.example.lab3.adapter.RatingsItemAdapter
import com.example.lab3.database.ColorMatcherDatabaseHelper
import com.example.lab3.databinding.FragmentGameRatingsBinding
import com.example.lab3.shared.model.GameViewModel
import com.example.lab3.shared.model.ScoreRecord

class GameRatingsFragment : Fragment() {
    private lateinit var binding: FragmentGameRatingsBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_gameRatingsFragment_to_gameResultsFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameRatingsBinding.inflate(inflater, container, false)
        requireActivity().title = getString(R.string.ratings)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scoreRecords = mutableListOf<ScoreRecord>()

        addMenu()
        getDataFromDatabase(scoreRecords)

        binding.rvScoreRecords.adapter = RatingsItemAdapter(requireContext(), scoreRecords)
    }

    /**
     * Fetch data from the local database into a list
     *
     * @param scoreRecords a list used to store data from the database
     */
    private fun getDataFromDatabase(scoreRecords: MutableList<ScoreRecord>) {
        val cursor = ColorMatcherDatabaseHelper(requireContext()).getUserEmailAndScore(viewModel.difficulty)

        if (cursor?.count == 0) {
            Toast.makeText(requireContext(), "Database is empty", Toast.LENGTH_SHORT).show()
        } else {
            while (cursor!!.moveToNext()) {
                scoreRecords.add(ScoreRecord(cursor.getString((0)), cursor.getInt(1)))
            }
        }
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
                        findNavController().navigate(R.id.action_gameRatingsFragment_to_settingsFragment)
                        true
                    }
                    R.id.menu_sign_out -> {
                        if (isUserLoggedIn()) {
                            viewModel.auth.signOut()
                            findNavController().navigate(R.id.action_gameRatingsFragment_to_registrationFragment    )
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