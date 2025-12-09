package com.example.glasscal.ui.task

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import coil.load
import com.example.glasscal.R
import com.example.glasscal.data.local.entity.Task
import com.example.glasscal.databinding.BottomSheetAddTaskBinding
import com.example.glasscal.util.CalendarUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar

/**
 * Bottom Sheet for adding or editing tasks
 * 할일 추가 또는 수정을 위한 BottomSheet
 */
class AddTaskBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddTaskBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: Long = 0L
    private var selectedImageUri: Uri? = null
    private var editingTask: Task? = null

    private var onTaskSaved: ((Task) -> Unit)? = null
    private var onTaskDeleted: ((Task) -> Unit)? = null

    // 이미지 선택 결과 처리
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                try {
                    // 영구 권한 부여 시도
                    requireContext().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    selectedImageUri = uri
                    showImagePreview(uri)
                } catch (e: SecurityException) {
                    // 권한 부여 실패 시에도 URI 저장 (임시)
                    selectedImageUri = uri
                    showImagePreview(uri)
                    Snackbar.make(
                        binding.root,
                        "이미지 권한 설정에 문제가 있습니다. 앱을 재시작하면 이미지가 사라질 수 있습니다.",
                        Snackbar.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Snackbar.make(
                        binding.root,
                        "이미지 로드 실패: ${e.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // 권한 요청 결과 처리
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 권한이 승인되면 이미지 선택기 열기
            openImagePicker()
        } else {
            // 권한이 거부되면 안내 메시지 표시
            Snackbar.make(
                binding.root,
                "이미지를 선택하려면 저장소 접근 권한이 필요합니다.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupClickListeners()
        setupAnimations()
        setupBottomSheetBehavior()
    }

    /**
     * BottomSheet 동작 설정
     */
    private fun setupBottomSheetBehavior() {
        // BottomSheet가 완전히 확장된 상태로 시작하도록 설정
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as? com.google.android.material.bottomsheet.BottomSheetDialog
            val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            bottomSheet?.let {
                val behavior = com.google.android.material.bottomsheet.BottomSheetBehavior.from(it)
                behavior.state = com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_EXPANDED
                behavior.skipCollapsed = true
            }
        }
    }

    /**
     * 애니메이션 설정
     */
    private fun setupAnimations() {
        // Fade in animation for content
        val fadeIn = android.view.animation.AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.root.startAnimation(fadeIn)
    }

    /**
     * View 초기 설정
     */
    private fun setupViews() {
        // 선택된 날짜 표시
        binding.tvSelectedDate.text = "날짜: ${CalendarUtils.formatDate(selectedDate)}"

        // 수정 모드인 경우 기존 데이터 표시
        editingTask?.let { task ->
            binding.tvSheetTitle.text = "할일 수정"
            binding.etTaskTitle.setText(task.title)
            binding.etTaskContent.setText(task.content)
            binding.btnDelete.visibility = View.VISIBLE

            task.imageUri?.let { uri ->
                selectedImageUri = Uri.parse(uri)
                showImagePreview(Uri.parse(uri))
            }
        }
    }

    /**
     * 클릭 리스너 설정
     */
    private fun setupClickListeners() {
        // 이미지 선택 버튼
        binding.btnSelectImage.setOnClickListener {
            checkAndRequestPermission()
        }

        // 이미지 제거 버튼
        binding.btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            binding.cvImagePreview.visibility = View.GONE
        }

        // 취소 버튼
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        // 저장 버튼
        binding.btnSave.setOnClickListener {
            saveTask()
        }

        // 삭제 버튼
        binding.btnDelete.setOnClickListener {
            deleteTask()
        }
    }

    /**
     * 권한 확인 및 요청
     */
    private fun checkAndRequestPermission() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openImagePicker()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                Snackbar.make(
                    binding.root,
                    "할일에 이미지를 추가하려면 저장소 접근 권한이 필요합니다.",
                    Snackbar.LENGTH_LONG
                ).setAction("확인") {
                    permissionLauncher.launch(permission)
                }.show()
            }
            else -> {
                // 권한 요청
                permissionLauncher.launch(permission)
            }
        }
    }

    /**
     * 이미지 선택기 열기
     */
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        imagePickerLauncher.launch(intent)
    }

    /**
     * 이미지 미리보기 표시
     */
    private fun showImagePreview(uri: Uri) {
        binding.cvImagePreview.visibility = View.VISIBLE
        binding.ivImagePreview.load(uri) {
            crossfade(true)
            // BlurView와 호환되도록 hardware bitmap 비활성화
            allowHardware(false)
        }
    }

    /**
     * 할일 저장
     */
    private fun saveTask() {
        val title = binding.etTaskTitle.text.toString().trim()
        val content = binding.etTaskContent.text.toString().trim()

        // 유효성 검사
        if (title.isEmpty()) {
            Snackbar.make(binding.root, "제목을 입력해주세요", Snackbar.LENGTH_SHORT).show()
            return
        }

        // Task 객체 생성
        val task = Task(
            id = editingTask?.id ?: 0,
            title = title,
            content = content,
            date = selectedDate,
            imageUri = selectedImageUri?.toString(),
            createdAt = editingTask?.createdAt ?: System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        // 콜백 호출
        onTaskSaved?.invoke(task)
        dismiss()
    }

    /**
     * 할일 삭제
     */
    private fun deleteTask() {
        editingTask?.let { task ->
            com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("할일 삭제")
                .setMessage("'${task.title}'을(를) 삭제하시겠습니까?")
                .setPositiveButton("삭제") { _, _ ->
                    onTaskDeleted?.invoke(task)
                    dismiss()
                }
                .setNegativeButton("취소", null)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * 새 할일 추가용 BottomSheet 생성
         */
        fun newInstance(
            selectedDate: Long,
            onTaskSaved: (Task) -> Unit
        ): AddTaskBottomSheet {
            return AddTaskBottomSheet().apply {
                this.selectedDate = selectedDate
                this.onTaskSaved = onTaskSaved
            }
        }

        /**
         * 할일 수정용 BottomSheet 생성
         */
        fun editInstance(
            task: Task,
            onTaskSaved: (Task) -> Unit,
            onTaskDeleted: (Task) -> Unit
        ): AddTaskBottomSheet {
            return AddTaskBottomSheet().apply {
                this.selectedDate = task.date
                this.editingTask = task
                this.onTaskSaved = onTaskSaved
                this.onTaskDeleted = onTaskDeleted
            }
        }
    }
}
