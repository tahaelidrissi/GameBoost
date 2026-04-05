package com.gameboost.frontend.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gameboost.frontend.databinding.FragmentCoachProfileBinding
import com.gameboost.frontend.ui.viewmodel.CoachViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class CoachProfileFragment : Fragment() {

    private var _binding: FragmentCoachProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoachViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoachProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.submitButton.setOnClickListener {
            val gameTitle = binding.gameInput.text.toString().trim()
            val rank = binding.rankInput.text.toString().trim()
            val bio = binding.bioInput.text.toString().trim()
            val hourlyRate = binding.rateInput.text.toString().trim()

            if (gameTitle.isEmpty() || rank.isEmpty() || bio.isEmpty() || hourlyRate.isEmpty()) {
                Toast.makeText(requireContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val rate = BigDecimal(hourlyRate)
                viewModel.createCoachProfile(gameTitle, rank, bio, rate, null)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Tarif invalide", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.coachState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CoachViewModel.CoachState.Loading -> {
                    binding.submitButton.isEnabled = false
                    binding.submitButton.text = "Soumission en cours..."
                }
                is CoachViewModel.CoachState.Success -> {
                    binding.submitButton.isEnabled = true
                    binding.submitButton.text = "Soumettre candidature"
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is CoachViewModel.CoachState.Error -> {
                    binding.submitButton.isEnabled = true
                    binding.submitButton.text = "Soumettre candidature"
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.submitButton.isEnabled = true
                    binding.submitButton.text = "Soumettre candidature"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
