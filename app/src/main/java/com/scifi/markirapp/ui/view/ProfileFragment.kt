package com.scifi.markirapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.scifi.markirapp.databinding.FragmentProfileBinding
import com.scifi.markirapp.ui.adapter.FavoriteAdapter
import com.scifi.markirapp.ui.viewmodel.FavoriteViewModel
import com.scifi.markirapp.utils.FirebaseAuthUtils
import com.scifi.markirapp.utils.InterfaceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuthUtils.instance }
    private lateinit var favoriteAdapter: FavoriteAdapter
    private val favoriteViewModel by viewModels<FavoriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupAction() {
            binding.apply {
                btnLogout.setOnClickListener {
                    signOut()
                }
                tvName.text = auth.currentUser?.displayName
                tvEmail.text = auth.currentUser?.email
                Glide.with(this@ProfileFragment)
                    .load(auth.currentUser?.photoUrl)
                    .into(ivAvatar)
            }
    }

    private fun signOut() {
        InterfaceUtils.showAlert(
            requireActivity(),
            isWarning = true,
            message = "Are you sure you want to sign out?",
            primaryButtonText = "Yes",
            onPrimaryButtonClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    val credentialManager = CredentialManager.create(requireActivity())
                    auth.signOut()
                    credentialManager.clearCredentialState(ClearCredentialStateRequest())
                    startActivity(Intent(requireActivity(), WelcomeActivity::class.java))
                    requireActivity().finish()
                }
            },
            secondaryButtonText = "No"
        )
    }

    private fun setupRecyclerView() {
        favoriteAdapter = FavoriteAdapter(mutableListOf())
        binding.rvFav.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        favoriteViewModel.favoriteLocations.observe(viewLifecycleOwner) { locations ->
            favoriteAdapter.updateData(locations)
            binding.viewEmpty.visibility = if (locations.isEmpty()) View.VISIBLE else View.GONE
            binding.rvFav.visibility = if (locations.isEmpty()) View.GONE else View.VISIBLE
        }
    }
}