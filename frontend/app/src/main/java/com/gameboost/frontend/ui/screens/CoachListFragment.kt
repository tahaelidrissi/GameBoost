package com.gameboost.frontend.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.gameboost.frontend.databinding.FragmentCoachListBinding
import com.gameboost.frontend.ui.viewmodel.CoachViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CoachListFragment : Fragment() {

    private var _binding: FragmentCoachListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CoachViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoachListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {
            val game = binding.gameInput.text.toString().trim()
            val rank = binding.rankInput.text.toString().trim()
            viewModel.fetchCoaches(
                game = if (game.isEmpty()) null else game,
                rank = if (rank.isEmpty()) null else rank
            )
        }

        // Charger les coachs au démarrage
        viewModel.fetchCoaches()

        viewModel.coaches.observe(viewLifecycleOwner) { coaches ->
            binding.coachCount.text = "Coachs trouvés: ${coaches.size}"
            // Afficher les coachs dans une liste
            binding.coachList.text = coaches.joinToString("\n\n") { coach ->
                "${coach.username} - ${coach.gameTitle} (${coach.rank})\n" +
                "Tarif: ${coach.hourlyRate}€/h - Note: ${coach.averageRating}⭐"
            }
        }

        viewModel.coachState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CoachViewModel.CoachState.Loading -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is CoachViewModel.CoachState.Success -> {
                    binding.loadingIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is CoachViewModel.CoachState.Error -> {
                    binding.loadingIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.loadingIndicator.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
