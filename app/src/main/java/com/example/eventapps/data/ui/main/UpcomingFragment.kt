package com.example.eventapps.data.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapps.data.ui.EventAdapter
import com.example.eventapps.data.ui.MainViewModel
import com.example.eventapps.data.ui.ViewModelFactory
import com.example.testingroomdatabase.R
import com.example.testingroomdatabase.databinding.FragmentUpcomingBinding

class UpcomingFragment : Fragment() {

    private var _binding: FragmentUpcomingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.Companion.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpcomingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = EventAdapter { event ->
            val bundle = Bundle().apply {
                putInt(DetailFragment.EXTRA_ID, event.id)
            }
            findNavController().navigate(R.id.action_upcomingFragment_to_detailFragment, bundle)
        }

        binding.rvEvents.layoutManager = LinearLayoutManager(requireContext())
        binding.rvEvents.adapter = adapter

        viewModel.listUpcoming.observe(viewLifecycleOwner) { events ->
            if (binding.searchView.query.isEmpty()) {
                adapter.submitList(events)
            }
        }

        viewModel.listSearch.observe(viewLifecycleOwner) { searchResults ->
            if (binding.searchView.query.isNotEmpty()) {
                adapter.submitList(searchResults)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        setupSearch()

        // Panggil fungsi untuk mengambil data
        viewModel.getUpcomingEvents()
    }

    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrBlank()) {
                    viewModel.searchEvents(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    viewModel.listUpcoming.value?.let {
                        (binding.rvEvents.adapter as? EventAdapter)?.submitList(it)
                    }
                }
                return false
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
