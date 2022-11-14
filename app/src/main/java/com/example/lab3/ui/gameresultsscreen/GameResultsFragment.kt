package com.example.lab3.ui.gameresultsscreen

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
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
import com.example.lab3.database.ColorMatcherDatabaseHelper
import com.example.lab3.databinding.FragmentGameResultBinding
import com.example.lab3.shared.model.GameViewModel

class GameResultsFragment : Fragment() {
    private lateinit var binding: FragmentGameResultBinding
    private val viewModel: GameViewModel by activityViewModels()
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = getString(R.string.results)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_gameResultsFragment_to_gameMenuFragment)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameResultBinding.inflate(inflater, container, false)
        sharedPref = requireActivity().getSharedPreferences("colormatcher_settings", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMenu()

        binding.tvScore.text = getString(R.string.final_score, viewModel.email, viewModel.score.value)
        binding.btnPlayAgain.setOnClickListener {
            findNavController().navigate(R.id.action_gameResultsFragment_to_gameMenuFragment)
        }

        if (sharedPref.getBoolean("hideButton", false)) {
            binding.btnSendResults.visibility = View.INVISIBLE
        }

        binding.btnSendResults.setOnClickListener {
            val resultSummary = getString(
                R.string.results_summary,
                viewModel.score.value
            )

            val mailTo = "mailto:${viewModel.email}" +
                    "?subject=" + Uri.encode(getString(R.string.color_matcher)) +
                    "&body=" + Uri.encode(resultSummary)

            val intent = Intent(Intent.ACTION_SENDTO).setData(Uri.parse(mailTo))

            startActivity(intent)
        }

        compareUsersOldAndNewScore(ColorMatcherDatabaseHelper(requireContext()))

    }

    /**
     * Compare user's score which stored in the database with the new one
     *
     * @param db a database from which the data is fetched
     */
    private fun compareUsersOldAndNewScore(db: ColorMatcherDatabaseHelper) {
        val cursor = db.getUserScore(viewModel.email, viewModel.difficulty)
        var dbScore = 0 // score from the database

        if (cursor!!.count > 0) {
            cursor.moveToNext()
            dbScore = cursor.getInt(0)
        }

        if (viewModel.score.value!! > dbScore) {
            db.updateScore(viewModel.email, viewModel.difficulty, viewModel.score.value!!)
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
                menuInflater.inflate(R.menu.options_menu_game_result_screen, menu)

                if (menu is MenuBuilder) {
                    menu.setOptionalIconsVisible(true)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_ratings -> {
                        findNavController().navigate(R.id.action_gameResultsFragment_to_gameRatingsFragment)
                        true
                    }
                    R.id.menu_settings -> {
                        findNavController().navigate(R.id.action_gameResultsFragment_to_settingsFragment)
                        true
                    }
                    R.id.menu_sign_out -> {
                        if (isUserLoggedIn()) {
                            viewModel.auth.signOut()
                            findNavController().navigate(R.id.action_gameResultsFragment_to_registrationFragment)
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