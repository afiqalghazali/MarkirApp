package com.scifi.markirapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.scifi.markirapp.databinding.FragmentParkingBinding
import com.scifi.markirapp.view.adapter.ParkingAdapter


class ParkingFragment : Fragment() {

    private var _binding: FragmentParkingBinding? = null
    private val binding get() = _binding!!
    private lateinit var parkingViewModel: ParkingViewModel
    private lateinit var parkingAdapter: ParkingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentParkingBinding.inflate(inflater, container, false)

        parkingViewModel = ViewModelProvider(requireActivity())[ParkingViewModel::class.java]

        setupRecyclerView()
        observeData()

        return binding.root
    }

    private fun setupRecyclerView() {
        parkingAdapter = ParkingAdapter(listOf())
        binding.rvPark.layoutManager = LinearLayoutManager(context)
        binding.rvPark.adapter = parkingAdapter
    }

    private fun observeData() {
        parkingViewModel.parkingLocations.observe(viewLifecycleOwner) { locations ->
            parkingAdapter.updateData(locations)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
