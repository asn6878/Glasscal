package com.example.glasscal.ui.calendar

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.glasscal.data.model.CalendarDay
import com.example.glasscal.databinding.FragmentCalendarBinding
import com.example.glasscal.ui.adapter.CalendarAdapter
import com.example.glasscal.util.CalendarUtils
import com.example.glasscal.viewmodel.CalendarViewModel
import eightbitlab.com.blurview.RenderScriptBlur
import kotlinx.coroutines.launch

/**
 * Fragment for displaying calendar with glassmorphism design
 * 글래스모피즘 디자인의 캘린더를 표시하는 프래그먼트
 */
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var calendarAdapter: CalendarAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBlurView()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    /**
     * BlurView 설정 (성능 최적화: auto-update 비활성화)
     */
    private fun setupBlurView() {
        val decorView = requireActivity().window.decorView as? ViewGroup
        decorView?.let {
            val windowBackground: Drawable? = decorView.background

            // 헤더 BlurView: auto-update 비활성화로 성능 개선
            binding.headerBlurView.setupWith(decorView, RenderScriptBlur(requireContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(20f)
                .setBlurAutoUpdate(false)

            // 요일 BlurView: auto-update 비활성화로 성능 개선
            binding.dayOfWeekBlurView.setupWith(decorView, RenderScriptBlur(requireContext()))
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(20f)
                .setBlurAutoUpdate(false)
        }
    }

    /**
     * RecyclerView 설정
     */
    private fun setupRecyclerView() {
        calendarAdapter = CalendarAdapter { calendarDay ->
            onDateClick(calendarDay)
        }

        binding.rvCalendar.apply {
            layoutManager = GridLayoutManager(context, 7) // 7 columns (일주일)
            adapter = calendarAdapter
            setHasFixedSize(true)
        }
    }

    /**
     * 클릭 리스너 설정
     */
    private fun setupClickListeners() {
        binding.btnPreviousMonth.setOnClickListener {
            viewModel.navigateToPreviousMonth()
        }

        binding.btnNextMonth.setOnClickListener {
            viewModel.navigateToNextMonth()
        }
    }

    /**
     * ViewModel 관찰
     */
    private fun observeViewModel() {
        // 현재 연월과 할일 목록을 함께 관찰하여 캘린더 업데이트
        lifecycleScope.launch {
            kotlinx.coroutines.flow.combine(
                viewModel.currentYear,
                viewModel.currentMonth,
                viewModel.monthTasks
            ) { year, month, tasks ->
                Triple(year, month, tasks)
            }.collect { (year, month, tasks) ->
                updateMonthYearTitle(year, month)
                updateCalendar(year, month, tasks)
            }
        }
    }

    /**
     * 월/연도 타이틀 업데이트
     */
    private fun updateMonthYearTitle(year: Int, month: Int) {
        binding.tvMonthYear.text = CalendarUtils.formatMonthYear(year, month)
    }

    /**
     * 캘린더 업데이트
     */
    private fun updateCalendar(
        year: Int,
        month: Int,
        tasks: List<com.example.glasscal.data.local.entity.Task>
    ) {
        val calendarDays = CalendarUtils.generateCalendarDays(year, month, tasks)
        calendarAdapter.submitList(calendarDays) {
            // submitList 완료 후 스크롤 위치 유지
            binding.rvCalendar.scrollToPosition(0)
        }
    }

    /**
     * 날짜 클릭 처리
     */
    private fun onDateClick(calendarDay: CalendarDay) {
        // TaskListBottomSheet를 열어서 할일 목록 확인
        val bottomSheet = com.example.glasscal.ui.task.TaskListBottomSheet.newInstance(
            selectedDate = calendarDay.timestamp
        )
        bottomSheet.show(parentFragmentManager, "TaskListBottomSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
