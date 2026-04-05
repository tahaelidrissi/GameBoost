package com.gameboost.frontend.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gameboost.frontend.databinding.FragmentRegisterBinding
import com.gameboost.frontend.ui.viewmodel.AuthViewModel
import com.gameboost.frontend.utils.ValidationUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()
            val username = binding.usernameInput.text.toString().trim()
            val role = if (binding.coachRadio.isChecked) "COACH" else "JOUEUR"

            // Validations
            if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ValidationUtils.isEmailValid(email)) {
                Toast.makeText(requireContext(), "Email invalide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ValidationUtils.isPasswordValid(password)) {
                Toast.makeText(requireContext(), "Mot de passe: min 8 chars, 1 majuscule, 1 chiffre", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!ValidationUtils.isUsernameValid(username)) {
                Toast.makeText(requireContext(), "Pseudo: 3-20 caractères, alphanumériques + underscore", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.register(email, password, username, role)
        }

        binding.loginLink.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.registerButton.isEnabled = false
                    binding.registerButton.text = "Inscription..."
                }
                is AuthViewModel.AuthState.Success -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    // Rediriger vers dashboard selon le rôle du coach
                    if (binding.coachRadio.isChecked) {
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterToCoachProfile())
                    } else {
                        findNavController().navigate(RegisterFragmentDirections.actionRegisterToDashboard())
                    }
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.registerButton.isEnabled = true
                    binding.registerButton.text = "S'inscrire"
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.registerButton.isEnabled = true
                    binding.registerButton.text = "S'inscrire"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
