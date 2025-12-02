package com.example.glasscal.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.glasscal.R
import com.example.glasscal.databinding.FragmentLoginBinding
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnGoogleLogin.setOnClickListener {
            Snackbar.make(binding.root, "Google OAuth는 추후 구현 예정입니다", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnGithubLogin.setOnClickListener {
            Snackbar.make(binding.root, "Github OAuth는 추후 구현 예정입니다", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnSkipLogin.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
