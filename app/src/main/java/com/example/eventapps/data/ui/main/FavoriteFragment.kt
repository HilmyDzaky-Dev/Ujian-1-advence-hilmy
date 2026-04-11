package com.example.eventapps.data.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapps.data.remote.response.ListEventsItem
import com.example.eventapps.data.ui.EventAdapter
import com.example.eventapps.data.ui.FavoriteViewModel
import com.example.eventapps.data.ui.ViewModelFactory
import com.example.testingroomdatabase.R
import com.example.testingroomdatabase.databinding.FragmentFavoriteBinding

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.Companion.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireContext(), layoutManager.orientation)
        binding.rvFavorite.addItemDecoration(itemDecoration)

        viewModel.getFavoriteEvents().observe(viewLifecycleOwner) { favoriteEvents ->
            val items = favoriteEvents.map { entity ->
                ListEventsItem(
                    id = entity.id.toInt(),
                    name = entity.name ?: "",
                    summary = entity.summary ?: "",
                    mediaCover = entity.mediaCover ?: "",
                    imageLogo = entity.imageLogo ?: "",
                    registrants = 0,
                    link = "",
                    description = "",
                    ownerName = "",
                    cityName = "",
                    quota = 0,
                    beginTime = "",
                    endTime = "",
                    category = ""
                )
            }
            setFavoriteData(items)
        }
    }

    private fun setFavoriteData(items: List<ListEventsItem>) {
        val adapter = EventAdapter { event ->
            val bundle = Bundle()
            bundle.putInt(DetailFragment.EXTRA_ID, event.id)
            findNavController().navigate(R.id.action_favoriteFragment_to_detailFragment, bundle)
        }
        adapter.submitList(items)
        binding.rvFavorite.adapter = adapter
        binding.tvEmptyMessage.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}