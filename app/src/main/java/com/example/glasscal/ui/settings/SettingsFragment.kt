package com.example.glasscal.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.glasscal.R
import com.example.glasscal.data.local.database.AppDatabase
import com.example.glasscal.data.repository.CloudSyncRepository
import com.example.glasscal.data.repository.TaskRepository
import com.example.glasscal.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var cloudSyncRepository: CloudSyncRepository
    private lateinit var taskRepository: TaskRepository

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

        // Repository 초기화
        val taskDao = AppDatabase.getDatabase(requireContext()).taskDao()
        taskRepository = TaskRepository(taskDao)
        cloudSyncRepository = CloudSyncRepository(requireContext(), taskRepository)

        setupClickListeners()
        updateSyncStatus()
    }

    private fun setupClickListeners() {
        binding.btnSyncData.setOnClickListener {
            syncDataToCloud()
        }

        binding.btnFetchData.setOnClickListener {
            showFetchDataDialog()
        }

        binding.btnClearData.setOnClickListener {
            showClearDataDialog()
        }
    }

    /**
     * 동기화 상태 업데이트
     */
    private fun updateSyncStatus() {
        lifecycleScope.launch {
            try {
                val syncId = cloudSyncRepository.getSyncId()

                if (syncId != null) {
                    // 클라우드에서 동기화 상태 확인
                    val result = withContext(Dispatchers.IO) {
                        cloudSyncRepository.checkSyncStatus()
                    }

                    result.onSuccess { (synced, lastSyncDate) ->
                        if (synced && lastSyncDate != null) {
                            binding.tvSyncStatus.text = "클라우드 데이터 동기화 완료! - 최신 동기화 날짜[$lastSyncDate]"
                            binding.tvSyncStatus.setTextColor(resources.getColor(R.color.text_primary, null))
                        } else {
                            binding.tvSyncStatus.text = "클라우드 동기화 필요"
                            binding.tvSyncStatus.setTextColor(resources.getColor(R.color.calendar_text_weekend, null))
                        }
                        binding.tvSyncId.text = "동기화 ID: $syncId"
                        binding.tvSyncId.visibility = View.VISIBLE
                    }.onFailure {
                        binding.tvSyncStatus.text = "클라우드 동기화 필요"
                        binding.tvSyncStatus.setTextColor(resources.getColor(R.color.calendar_text_weekend, null))
                        binding.tvSyncId.text = "동기화 ID: $syncId"
                        binding.tvSyncId.visibility = View.VISIBLE
                    }
                } else {
                    binding.tvSyncStatus.text = "클라우드 동기화 필요"
                    binding.tvSyncStatus.setTextColor(resources.getColor(R.color.calendar_text_weekend, null))
                    binding.tvSyncId.visibility = View.GONE
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "상태 확인 실패: ${e.message}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * 데이터를 클라우드에 동기화
     */
    private fun syncDataToCloud() {
        lifecycleScope.launch {
            try {
                // 로딩 표시
                binding.btnSyncData.isEnabled = false
                binding.btnSyncData.text = "동기화 중..."

                // 모든 할일 가져오기
                val tasks = withContext(Dispatchers.IO) {
                    taskRepository.getAllTasks().first()
                }

                // 클라우드에 동기화
                val result = withContext(Dispatchers.IO) {
                    cloudSyncRepository.syncToCloud(tasks)
                }

                result.onSuccess { syncId ->
                    Snackbar.make(
                        binding.root,
                        "동기화 완료! ID: $syncId\n이 ID를 기억하세요!",
                        Snackbar.LENGTH_LONG
                    ).show()
                    updateSyncStatus()
                }.onFailure { error ->
                    Snackbar.make(
                        binding.root,
                        "동기화 실패: ${error.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Snackbar.make(
                    binding.root,
                    "오류 발생: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            } finally {
                binding.btnSyncData.isEnabled = true
                binding.btnSyncData.text = "동기화"
            }
        }
    }

    /**
     * 데이터 가져오기 다이얼로그
     */
    private fun showFetchDataDialog() {
        val editText = EditText(requireContext()).apply {
            hint = "동기화 ID 입력"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("데이터 가져오기")
            .setMessage("클라우드에서 데이터를 가져오면 기존 로컬 데이터가 모두 삭제됩니다.\n계속하시겠습니까?")
            .setView(editText)
            .setPositiveButton("가져오기") { dialog, _ ->
                val syncId = editText.text.toString().trim()
                if (syncId.isNotEmpty()) {
                    fetchDataFromCloud(syncId)
                } else {
                    Snackbar.make(binding.root, "동기화 ID를 입력하세요", Snackbar.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * 클라우드에서 데이터 가져오기
     */
    private fun fetchDataFromCloud(syncId: String) {
        lifecycleScope.launch {
            try {
                // 로딩 표시
                binding.btnFetchData.isEnabled = false
                binding.btnFetchData.text = "가져오는 중..."

                val result = withContext(Dispatchers.IO) {
                    cloudSyncRepository.fetchFromCloud(syncId)
                }

                result.onSuccess { tasks ->
                    Snackbar.make(
                        binding.root,
                        "데이터 가져오기 완료! (${tasks.size}개)",
                        Snackbar.LENGTH_LONG
                    ).show()
                    updateSyncStatus()
                }.onFailure { error ->
                    Snackbar.make(
                        binding.root,
                        "데이터 가져오기 실패: ${error.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Snackbar.make(
                    binding.root,
                    "오류 발생: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            } finally {
                binding.btnFetchData.isEnabled = true
                binding.btnFetchData.text = "데이터 가져오기"
            }
        }
    }

    /**
     * 모든 데이터 삭제 확인 다이얼로그
     */
    private fun showClearDataDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("모든 데이터 삭제")
            .setMessage("로컬 및 클라우드에 저장된 모든 데이터가 삭제됩니다. 확실하십니까?\n\n삭제되는 항목:\n• 로컬에 저장된 모든 할일\n• 클라우드에 동기화된 모든 데이터\n• 동기화 정보\n\n이 작업은 되돌릴 수 없습니다.")
            .setPositiveButton("삭제") { dialog, _ ->
                clearAllData()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    /**
     * 모든 데이터 삭제
     */
    private fun clearAllData() {
        lifecycleScope.launch {
            try {
                // 로딩 표시
                binding.btnClearData.isEnabled = false
                binding.btnClearData.text = "삭제 중..."

                val result = withContext(Dispatchers.IO) {
                    cloudSyncRepository.deleteAllData()
                }

                result.onSuccess {
                    Snackbar.make(
                        binding.root,
                        "모든 데이터가 삭제되었습니다",
                        Snackbar.LENGTH_LONG
                    ).show()
                    updateSyncStatus()
                }.onFailure { error ->
                    Snackbar.make(
                        binding.root,
                        "삭제 실패: ${error.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Snackbar.make(
                    binding.root,
                    "오류 발생: ${e.message}",
                    Snackbar.LENGTH_LONG
                ).show()
            } finally {
                binding.btnClearData.isEnabled = true
                binding.btnClearData.text = "모든 데이터 삭제"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
