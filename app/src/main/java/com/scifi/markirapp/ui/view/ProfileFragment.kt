package com.scifi.markirapp.ui.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.scifi.markirapp.databinding.FragmentProfileBinding
import com.scifi.markirapp.utils.InterfaceUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var auth: FirebaseAuth

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
        auth = Firebase.auth
        setupAction()

    }

    private fun setupAction() {
        binding.apply {
            btnLogout.setOnClickListener {
                signOut()
            }
            binding.btnFav.setOnClickListener {
                goToFavoriteActivity()
            }
            tvName.text = auth.currentUser?.displayName
            tvEmail.text = auth.currentUser?.email
            Glide.with(this@ProfileFragment)
                .load(auth.currentUser?.photoUrl)
                .into(ivAvatar)
        }
    }
    private fun goToFavoriteActivity() {
        val intent = Intent(requireActivity(), FavoriteActivity::class.java)
        startActivity(intent)
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
    companion object{
        const val  LOCATON_EXTRA = "location_extra"
    }
}