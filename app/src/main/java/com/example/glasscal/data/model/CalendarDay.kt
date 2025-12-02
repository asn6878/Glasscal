package com.example.glasscal.data.model

import com.example.glasscal.data.local.entity.Task

/**
 * Represents a single day in the calendar grid
 * 캘린더 그리드의 하나의 날짜를 표현하는 모델
 */
data class CalendarDay(
    /** 날짜 (1-31), 현재 월이 아닌 경우 0 */
    val dayOfMonth: Int,

    /** 해당 날짜의 타임스탬프 */
    val timestamp: Long,

    /** 현재 월에 속하는지 여부 */
    val isCurrentMonth: Boolean,

    /** 오늘 날짜인지 여부 */
    val isToday: Boolean = false,

    /** 해당 날짜의 할일 목록 */
    val tasks: List<Task> = emptyList()
) {
    /**
     * 해당 날짜에 할일이 있는지 여부
     */
    fun hasTasks(): Boolean = tasks.isNotEmpty()

    /**
     * 첫 번째 할일의 제목 (미리보기용)
     */
    fun getFirstTaskTitle(): String? = tasks.firstOrNull()?.title

    /**
     * 추가 할일 개수 (첫 번째 제외)
     */
    fun getAdditionalTaskCount(): Int = maxOf(0, tasks.size - 1)
}
