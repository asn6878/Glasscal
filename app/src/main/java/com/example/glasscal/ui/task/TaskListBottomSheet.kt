package com.example.glasscal.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.glasscal.R
import com.example.glasscal.data.local.entity.Task
import com.example.glasscal.databinding.BottomSheetTaskListBinding
import com.example.glasscal.ui.adapter.TaskListAdapter
import com.example.glasscal.util.CalendarUtils
import com.example.glasscal.viewmodel.CalendarViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Bottom Sheet for displaying task list of a specific date
 * 특정 날짜의 할일 목록을 보여주는 BottomSheet
 */
class TaskListBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomSheetTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var taskListAdapter: TaskListAdapter

    private var selectedDate: Long = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        setupRecyclerView()
        setupClickListeners()
        setupBottomSheetBehavior()
        observeTasks()
    }

    /**
     * BottomSheet 동작 설정
     */
    private fun setupBottomSheetBehavior() {
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
     * View 초기 설정
     */
    private fun setupViews() {
        // 선택된 날짜 표시
        binding.tvSelectedDate.text = CalendarUtils.formatDate(selectedDate)
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView() {
        taskListAdapter = TaskListAdapter(
            onTaskClick = { task ->
                // 할일 클릭 시 수정 BottomSheet 열기
                openEditTaskBottomSheet(task)
            },
            onTaskLongClick = { task ->
                // 할일 롱 클릭 시 삭제 확인 다이얼로그
                showDeleteConfirmDialog(task)
            }
        )

        binding.rvTaskList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = taskListAdapter
            setHasFixedSize(false)
        }
    }

    /**
     * 클릭 리스너 설정
     */
    private fun setupClickListeners() {
        // 새 할일 추가 버튼
        binding.btnAddTask.setOnClickListener {
            openAddTaskBottomSheet()
        }
    }

    /**
     * 할일 목록 관찰
     */
    private fun observeTasks() {
        viewModel.getTasksByDate(selectedDate).observe(viewLifecycleOwner) { tasks ->
            updateTaskList(tasks)
        }
    }

    /**
     * 할일 목록 업데이트
     */
    private fun updateTaskList(tasks: List<Task>) {
        if (tasks.isEmpty()) {
            binding.emptyStateContainer.visibility = View.VISIBLE
            binding.rvTaskList.visibility = View.GONE
        } else {
            binding.emptyStateContainer.visibility = View.GONE
            binding.rvTaskList.visibility = View.VISIBLE
            taskListAdapter.submitList(tasks)
        }
    }

    /**
     * 새 할일 추가 BottomSheet 열기
     */
    private fun openAddTaskBottomSheet() {
        val addTaskBottomSheet = AddTaskBottomSheet.newInstance(
            selectedDate = selectedDate,
            onTaskSaved = { task ->
                viewModel.insertTask(task)
            }
        )
        addTaskBottomSheet.show(parentFragmentManager, "AddTaskBottomSheet")
    }

    /**
     * 할일 수정 BottomSheet 열기
     */
    private fun openEditTaskBottomSheet(task: Task) {
        val editTaskBottomSheet = AddTaskBottomSheet.editInstance(
            task = task,
            onTaskSaved = { updatedTask ->
                viewModel.updateTask(updatedTask)
            },
            onTaskDeleted = { deletedTask ->
                viewModel.deleteTask(deletedTask)
            }
        )
        editTaskBottomSheet.show(parentFragmentManager, "EditTaskBottomSheet")
    }

    /**
     * 할일 삭제 확인 다이얼로그
     */
    private fun showDeleteConfirmDialog(task: Task) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("할일 삭제")
            .setMessage("'${task.title}'을(를) 삭제하시겠습니까?")
            .setPositiveButton("삭제") { _, _ ->
                viewModel.deleteTask(task)
            }
            .setNegativeButton("취소", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * TaskListBottomSheet 생성
         */
        fun newInstance(selectedDate: Long): TaskListBottomSheet {
            return TaskListBottomSheet().apply {
                this.selectedDate = selectedDate
            }
        }
    }
}
