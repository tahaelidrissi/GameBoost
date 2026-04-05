package com.gameboost.frontend.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gameboost.frontend.databinding.FragmentDashboardBinding
import com.gameboost.frontend.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.getProfile()

        binding.viewCoachesButton.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardToCoachList())
        }

        binding.viewSessionsButton.setOnClickListener {
            findNavController().navigate(DashboardFragmentDirections.actionDashboardToSessionList())
        }

        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
            findNavController().navigate(DashboardFragmentDirections.actionDashboardToLogin())
        }

        authViewModel.currentUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.welcomeText.text = "Bienvenue, ${user.username}!"
                binding.roleText.text = "Rôle: ${user.role.name}"
            }
        }

        authViewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
