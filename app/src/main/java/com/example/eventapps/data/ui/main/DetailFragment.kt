package com.example.eventapps.data.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.eventapps.data.remote.response.ListEventsItem
import com.example.eventapps.data.ui.DetailViewModel
import com.example.eventapps.data.ui.ViewModelFactory
import com.example.testingroomdatabase.R
import com.example.testingroomdatabase.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels {
        ViewModelFactory.Companion.getInstance(requireActivity())
    }

    private var isFavorite = false
    private var currentEvent: ListEventsItem? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventId = arguments?.getInt(EXTRA_ID) ?: 0

        viewModel.getDetailEvent(eventId)

        viewModel.eventDetail.observe(viewLifecycleOwner) { event ->
            if (event != null) {
                currentEvent = event
                displayEventDetail(event)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.isFavorite(eventId.toString()).observe(viewLifecycleOwner) { favorite ->
            isFavorite = favorite
            updateFavoriteIcon()
        }
    }

    private fun displayEventDetail(event: ListEventsItem) {
        binding.apply {
            tvEventName.text = event.name
            tvEventOwner.text = "Penyelenggara: ${event.ownerName}"
            tvEventTime.text = "Waktu: ${event.beginTime}"

            val remainingQuota = event.quota - event.registrants
            tvEventQuota.text = "Sisa Kuota: $remainingQuota"

            tvEventDescription.text = HtmlCompat.fromHtml(
                event.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

            Glide.with(this@DetailFragment)
                .load(event.mediaCover)
                .into(ivEventImage)

            fabFavorite.setOnClickListener {
                currentEvent?.let {
                    if (isFavorite) {
                        viewModel.setFavoriteEvent(it, false)
                        Toast.makeText(context, "Dihapus dari favorit", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.setFavoriteEvent(it, true)
                        Toast.makeText(context, "Ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_active))
        } else {
            binding.fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val EXTRA_ID = "extra_id"
    }
}
