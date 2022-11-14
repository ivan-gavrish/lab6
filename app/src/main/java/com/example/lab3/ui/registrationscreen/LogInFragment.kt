package com.example.lab3.ui.registrationscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.lab3.R
import com.example.lab3.databinding.FragmentLoginBinding
import com.example.lab3.shared.model.GameViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class LogInFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val viewModel: GameViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigate(R.id.action_logInFragment_to_registrationFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        requireActivity().title = getString(R.string.log_in)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tfEmail.setOnClickListener {
            binding.tfEmail.isErrorEnabled = false
        }

        binding.tfPassword.setOnClickListener {
            binding.tfPassword.isErrorEnabled = false
        }

        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_signupFragment)
        }

        binding.btnLogIn.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty()) {
                binding.tfEmail.error = "Enter an email!"
                binding.tfEmail.isErrorEnabled = true
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.tfPassword.error = "Enter a password!"
                binding.tfPassword.isErrorEnabled = true
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    /**
     * Logs in user with Firebase Authentication
     * @see [com.google.firebase.auth.FirebaseAuth.signInWithEmailAndPassword]
     */
    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                viewModel.auth.signInWithEmailAndPassword(email, password).await()
                withContext(Dispatchers.Main) {
                    checkLoggedInState(email)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Check if user managed to log in
     *
     * @param email a user's email
     */
    private fun checkLoggedInState(email: String) {
        if (viewModel.auth.currentUser == null) {
            Toast.makeText(requireContext(), "You are not logged in", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "You are logged in!", Toast.LENGTH_SHORT).show()
            viewModel.setEmail(email)
            findNavController().navigate(R.id.action_logInFragment_to_gameMenuFragment)
        }
    }
}