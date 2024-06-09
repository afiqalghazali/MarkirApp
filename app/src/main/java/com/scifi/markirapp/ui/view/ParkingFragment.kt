package com.scifi.markirapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.scifi.markirapp.databinding.FragmentParkingBinding
import com.scifi.markirapp.ui.adapter.ParkingAdapter
import com.scifi.markirapp.ui.viewmodel.ParkingViewModel


class ParkingFragment : Fragment() {

    private var _binding: FragmentParkingBinding? = null
    private val binding get() = _binding!!
    private val parkingViewModel by activityViewModels<ParkingViewModel>()
    private lateinit var parkingAdapter: ParkingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentParkingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
    }

    private fun setupRecyclerView() {
        parkingAdapter = ParkingAdapter(listOf())
        binding.rvPark.layoutManager = LinearLayoutManager(context)
        binding.rvPark.adapter = parkingAdapter

    }

    private fun observeData() {
        parkingViewModel.parkingLocations.observe(viewLifecycleOwner) { locations ->
            parkingAdapter.updateData(locations)
            parkingAdapter.sortByDistance()
            binding.viewEmpty.visibility = if (locations.isEmpty()) View.VISIBLE else View.GONE
            binding.rvPark.visibility = if (locations.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}