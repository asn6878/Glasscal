package com.example.glasscal.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.glasscal.R
import com.example.glasscal.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }

        binding.btnSyncData.setOnClickListener {
            Snackbar.make(binding.root, "데이터 동기화는 로그인 후 이용 가능합니다", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnExportData.setOnClickListener {
            Snackbar.make(binding.root, "데이터 내보내기 기능은 추후 구현 예정입니다", Snackbar.LENGTH_SHORT).show()
        }

        binding.btnClearData.setOnClickListener {
            showClearDataDialog()
        }
    }

    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("모든 데이터 삭제")
            .setMessage("정말로 모든 데이터를 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.")
            .setPositiveButton("삭제") { dialog, _ ->
                Snackbar.make(binding.root, "데이터 삭제 기능은 추후 구현 예정입니다", Snackbar.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
