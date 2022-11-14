package com.example.lab3.ui.gamescreen

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.lab3.R
import com.example.lab3.databinding.FragmentGameBinding
import com.example.lab3.shared.model.GameViewModel
import java.util.*

class GameFragment : Fragment() {
    private lateinit var binding: FragmentGameBinding
    private val viewModel: GameViewModel by activityViewModels()
    private var timeLeftInMillis: Long = 0
    private lateinit var gameDifficulty: String
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().title = getString(R.string.colormatcher)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            countDownTimer.cancel()
            findNavController().navigate(R.id.action_gameFragment_to_gameMenuFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameDifficulty = viewModel.difficulty

        addMenu()
        viewModel.reinitializeGameData()

        viewModel.leftWord.observe(viewLifecycleOwner) { leftWord ->
            binding.tvLeftWord.text = getString(R.string.left_word, leftWord)
        }

        viewModel.rightWord.observe(viewLifecycleOwner) { rightWord ->
            binding.tvRightWord.text = getString(R.string.left_word, rightWord)
        }

        viewModel.leftWordColor.observe(viewLifecycleOwner) { leftWordColor ->
            binding.tvLeftWord.setTextColor(Color.parseColor(leftWordColor))
        }

        viewModel.rightWordColor.observe(viewLifecycleOwner) { rightWordColor ->
            binding.tvRightWord.setTextColor(Color.parseColor(rightWordColor))
        }

        viewModel.score.observe(viewLifecycleOwner) { score ->
            binding.tvScore.text = getString(R.string.score, score)
        }

        binding.btnYes.setOnClickListener {
            onYesNoButtonClickedListener(binding.btnYes.text.toString())
        }

        binding.btnNo.setOnClickListener {
            onYesNoButtonClickedListener(binding.btnNo.text.toString())
        }

        startTimer()
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
                        countDownTimer.cancel()
                        findNavController().navigate(R.id.action_gameFragment_to_settingsFragment)
                        true
                    }
                    R.id.menu_sign_out -> {
                        if (isUserLoggedIn()) {
                            countDownTimer.cancel()
                            viewModel.auth.signOut()
                            findNavController().navigate(R.id.action_gameFragment_to_registrationFragment)
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

    /**
     * Response on the click on Yes or No button
     *
     * @param userAnswer a text from the button which corresponds to user answer
     */
    private fun onYesNoButtonClickedListener(userAnswer: String) {
        binding.tvAnswerState.visibility = View.VISIBLE

        if (!viewModel.isUserAnswerCorrect(binding.tvLeftWord.text.toString(),
                binding.tvRightWord.currentTextColor, userAnswer)) {
            binding.tvAnswerState.text = getString(R.string.answer_incorrect)
        } else {
            binding.tvAnswerState.text = getString(R.string.answer_correct)
        }

        if (!viewModel.nextWords()) {
            countDownTimer.cancel()
            findNavController().navigate(R.id.action_gameFragment_to_gameResultsFragment)
        }
    }

    /**
     * Start timer in the game
     */
    private fun startTimer() {
        val startTimeInMillis = when (gameDifficulty) {
            getString(R.string.easy) -> {
                60000 // a minute
            }
            getString((R.string.medium)) -> {
                30000 // half a minute
            }
            else -> {
                10000 // 10 sec
            }
        }

        timeLeftInMillis = startTimeInMillis.toLong()

        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(l: Long) {
                timeLeftInMillis = l
                updateTimerOnText()
            }

            override fun onFinish() {
                findNavController().navigate(R.id.action_gameFragment_to_gameResultsFragment)
            }
        }.start()
    }

    private fun updateTimerOnText() {
        val minutes = (timeLeftInMillis / 1000).toInt() / 60
        val seconds = (timeLeftInMillis / 1000).toInt() % 60
        val timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeLeftFormatted
    }
}