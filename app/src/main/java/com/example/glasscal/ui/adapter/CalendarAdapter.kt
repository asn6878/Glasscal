package com.example.glasscal.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.glasscal.R
import com.example.glasscal.data.model.CalendarDay
import com.example.glasscal.databinding.ItemCalendarCellBinding
import eightbitlab.com.blurview.RenderScriptBlur

/**
 * RecyclerView Adapter for Calendar Grid
 * 캘린더 그리드를 위한 어댑터
 */
class CalendarAdapter(
    private val onDateClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarAdapter.CalendarViewHolder>(CalendarDayDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val binding = ItemCalendarCellBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CalendarViewHolder(binding, onDateClick)
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for Calendar Cell
     */
    class CalendarViewHolder(
        private val binding: ItemCalendarCellBinding,
        private val onDateClick: (CalendarDay) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // BlurView 초기화
            setupBlurView()
        }

        private fun setupBlurView() {
            val decorView = (binding.root.context as? android.app.Activity)?.window?.decorView as? ViewGroup
            decorView?.let {
                val windowBackground: Drawable? = decorView.background

                binding.blurView.setupWith(decorView, RenderScriptBlur(binding.root.context))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(20f)
                    .setBlurAutoUpdate(true)
            }
        }

        fun bind(calendarDay: CalendarDay) {
            binding.apply {
                // 날짜 숫자 표시
                tvDateNumber.text = if (calendarDay.isCurrentMonth) {
                    calendarDay.dayOfMonth.toString()
                } else {
                    "" // 현재 월이 아니면 숫자 표시 안함
                }

                // 현재 월이 아닌 날짜는 투명도 조정
                tvDateNumber.alpha = if (calendarDay.isCurrentMonth) 1.0f else 0.3f

                // 오늘 날짜 표시
                todayIndicator.visibility = if (calendarDay.isToday) View.VISIBLE else View.GONE

                // 할일 미리보기
                if (calendarDay.hasTasks()) {
                    tvTaskPreview.visibility = View.VISIBLE
                    tvTaskPreview.text = calendarDay.getFirstTaskTitle()

                    // 추가 할일 개수 표시
                    val additionalCount = calendarDay.getAdditionalTaskCount()
                    if (additionalCount > 0) {
                        tvTaskCount.visibility = View.VISIBLE
                        tvTaskCount.text = "+${additionalCount} more"
                    } else {
                        tvTaskCount.visibility = View.GONE
                    }

                    // 첫 번째 할일의 이미지 배경 (있으면)
                    val firstTask = calendarDay.tasks.firstOrNull()
                    if (firstTask?.imageUri != null && firstTask.imageUri.isNotEmpty()) {
                        try {
                            ivTaskBackground.visibility = View.VISIBLE
                            ivTaskBackground.load(android.net.Uri.parse(firstTask.imageUri)) {
                                crossfade(true)
                                placeholder(R.drawable.bg_calendar_cell)
                                error(R.drawable.bg_calendar_cell)
                                // BlurView와 호환되도록 hardware bitmap 비활성화
                                allowHardware(false)
                                listener(
                                    onError = { _, _ ->
                                        // 이미지 로드 실패 시 백그라운드 숨김
                                        ivTaskBackground.visibility = View.GONE
                                    }
                                )
                            }
                        } catch (e: Exception) {
                            // URI 파싱 실패 시 이미지 숨김
                            ivTaskBackground.visibility = View.GONE
                        }
                    } else {
                        ivTaskBackground.visibility = View.GONE
                    }
                } else {
                    tvTaskPreview.visibility = View.GONE
                    tvTaskCount.visibility = View.GONE
                    ivTaskBackground.visibility = View.GONE
                }

                // 클릭 이벤트 with 애니메이션
                cellCard.setOnClickListener {
                    if (calendarDay.isCurrentMonth) {
                        // 클릭 애니메이션
                        val scaleUp = AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_up)
                        val scaleDown = AnimationUtils.loadAnimation(binding.root.context, R.anim.scale_down)

                        cellCard.startAnimation(scaleUp)
                        cellCard.postDelayed({
                            cellCard.startAnimation(scaleDown)
                            onDateClick(calendarDay)
                        }, 150)
                    }
                }
            }
        }
    }

    /**
     * DiffUtil Callback for efficient list updates
     */
    private class CalendarDayDiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem.timestamp == newItem.timestamp
        }

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem == newItem
        }
    }
}
