package com.example.glasscal.util

import com.example.glasscal.data.local.entity.Task
import com.example.glasscal.data.model.CalendarDay
import java.util.*

/**
 * Utility class for Calendar operations
 * 캘린더 관련 유틸리티 함수 모음
 */
object CalendarUtils {

    /**
     * 특정 연월의 캘린더 그리드 생성 (7 x 5 or 6)
     * @param year 연도
     * @param month 월 (0-11, Calendar.MONTH 형식)
     * @param tasks 해당 월의 할일 목록
     * @return CalendarDay 리스트 (35-42개)
     */
    fun generateCalendarDays(
        year: Int,
        month: Int,
        tasks: List<Task>
    ): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        val today = getTodayTimestamp()

        // 해당 월의 1일로 설정
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val firstDayOfMonth = calendar.clone() as Calendar
        val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) // 1=일요일, 7=토요일
        val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)

        val calendarDays = mutableListOf<CalendarDay>()

        // 1. 이전 월의 날짜들 (빈 칸 채우기)
        if (firstDayOfWeek > 1) {
            calendar.add(Calendar.DAY_OF_MONTH, -(firstDayOfWeek - 1))
            for (i in 1 until firstDayOfWeek) {
                val dayTimestamp = calendar.timeInMillis
                calendarDays.add(
                    CalendarDay(
                        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                        timestamp = dayTimestamp,
                        isCurrentMonth = false,
                        isToday = isSameDay(dayTimestamp, today),
                        tasks = getTasksForDay(dayTimestamp, tasks)
                    )
                )
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // 2. 현재 월의 날짜들
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        for (day in 1..daysInMonth) {
            val dayTimestamp = calendar.timeInMillis
            calendarDays.add(
                CalendarDay(
                    dayOfMonth = day,
                    timestamp = dayTimestamp,
                    isCurrentMonth = true,
                    isToday = isSameDay(dayTimestamp, today),
                    tasks = getTasksForDay(dayTimestamp, tasks)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // 3. 다음 월의 날짜들 (그리드를 35 또는 42개로 맞추기)
        val remainingCells = if (calendarDays.size <= 35) {
            35 - calendarDays.size
        } else {
            42 - calendarDays.size
        }

        for (i in 1..remainingCells) {
            val dayTimestamp = calendar.timeInMillis
            calendarDays.add(
                CalendarDay(
                    dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH),
                    timestamp = dayTimestamp,
                    isCurrentMonth = false,
                    isToday = isSameDay(dayTimestamp, today),
                    tasks = getTasksForDay(dayTimestamp, tasks)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return calendarDays
    }

    /**
     * 특정 날짜의 할일 목록 필터링
     */
    private fun getTasksForDay(dayTimestamp: Long, allTasks: List<Task>): List<Task> {
        val (startOfDay, endOfDay) = getDayRange(dayTimestamp)
        return allTasks.filter { task ->
            task.date in startOfDay..endOfDay
        }
    }

    /**
     * 특정 날짜의 시작과 끝 타임스탬프 반환
     */
    fun getDayRange(timestamp: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = calendar.timeInMillis

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }

    /**
     * 오늘 날짜의 타임스탬프 (00:00:00)
     */
    fun getTodayTimestamp(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    /**
     * 두 타임스탬프가 같은 날인지 확인
     */
    fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    /**
     * 연월을 "YYYY년 M월" 형식으로 포맷
     */
    fun formatMonthYear(year: Int, month: Int): String {
        return "${year}년 ${month + 1}월"
    }

    /**
     * 타임스탬프를 "YYYY년 M월 D일" 형식으로 포맷
     */
    fun formatDate(timestamp: Long): String {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "${year}년 ${month}월 ${day}일"
    }

    /**
     * 특정 날짜의 타임스탬프 생성
     */
    fun getTimestampForDate(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
