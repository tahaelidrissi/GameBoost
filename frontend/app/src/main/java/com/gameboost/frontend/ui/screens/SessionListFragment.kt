package com.gameboost.frontend.ui.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gameboost.frontend.databinding.FragmentSessionListBinding
import com.gameboost.frontend.ui.viewmodel.SessionViewModel
import com.gameboost.frontend.utils.DateUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SessionListFragment : Fragment() {

    private var _binding: FragmentSessionListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SessionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSessions()

        binding.filterButton.setOnClickListener {
            val status = binding.statusFilter.text.toString().trim()
            viewModel.getSessions(if (status.isEmpty()) null else status)
        }

        viewModel.sessions.observe(viewLifecycleOwner) { sessions ->
            binding.sessionCount.text = "Sessions: ${sessions.size}"
            binding.sessionList.text = sessions.joinToString("\n\n") { session ->
                "Session #${session.id}\n" +
                "Statut: ${session.status}\n" +
                "Durée: ${session.durationHours}h | Montant: ${session.amount}€\n" +
                "Date: ${DateUtils.formatDateTime(session.scheduledAt)}"
            }
        }

        viewModel.sessionState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is SessionViewModel.SessionState.Loading -> {
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is SessionViewModel.SessionState.Success -> {
                    binding.loadingIndicator.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                is SessionViewModel.SessionState.Error -> {
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
