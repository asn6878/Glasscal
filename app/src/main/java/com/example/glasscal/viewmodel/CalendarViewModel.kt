package com.example.glasscal.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.glasscal.data.local.database.AppDatabase
import com.example.glasscal.data.local.entity.Task
import com.example.glasscal.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * ViewModel for Calendar screen
 * 캘린더 화면의 비즈니스 로직을 처리하는 ViewModel
 */
class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    // 현재 선택된 연월
    private val _currentYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val currentYear: StateFlow<Int> = _currentYear

    private val _currentMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))
    val currentMonth: StateFlow<Int> = _currentMonth

    // 현재 월의 모든 할일
    private val _monthTasks = MutableStateFlow<List<Task>>(emptyList())
    val monthTasks: StateFlow<List<Task>> = _monthTasks

    init {
        val taskDao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)

        // 월 변경 시 자동으로 할일 새로고침
        viewModelScope.launch {
            kotlinx.coroutines.flow.combine(_currentYear, _currentMonth) { year, month ->
                Pair(year, month)
            }.collect { (year, month) ->
                val (startOfMonth, endOfMonth) = getMonthRange(year, month)
                repository.getTasksByMonth(startOfMonth, endOfMonth).collect { tasks ->
                    _monthTasks.value = tasks
                }
            }
        }
    }

    /**
     * 특정 월로 이동
     */
    fun navigateToMonth(year: Int, month: Int) {
        _currentYear.value = year
        _currentMonth.value = month
        refreshMonthTasks()
    }

    /**
     * 이전 월로 이동
     */
    fun navigateToPreviousMonth() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, _currentYear.value)
            set(Calendar.MONTH, _currentMonth.value)
            add(Calendar.MONTH, -1)
        }
        _currentYear.value = calendar.get(Calendar.YEAR)
        _currentMonth.value = calendar.get(Calendar.MONTH)
        refreshMonthTasks()
    }

    /**
     * 다음 월로 이동
     */
    fun navigateToNextMonth() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, _currentYear.value)
            set(Calendar.MONTH, _currentMonth.value)
            add(Calendar.MONTH, 1)
        }
        _currentYear.value = calendar.get(Calendar.YEAR)
        _currentMonth.value = calendar.get(Calendar.MONTH)
        refreshMonthTasks()
    }

    /**
     * 할일 추가
     */
    fun insertTask(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task)
        }
    }

    /**
     * 할일 수정
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }

    /**
     * 할일 삭제
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    /**
     * ID로 할일 삭제
     */
    fun deleteTaskById(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(taskId)
        }
    }

    /**
     * 특정 날짜의 할일 조회
     */
    fun getTasksByDate(date: Long): LiveData<List<Task>> {
        val (startOfDay, endOfDay) = getDayRange(date)
        return repository.getTasksByDate(startOfDay, endOfDay).asLiveData()
    }

    /**
     * 현재 월의 할일을 새로고침 (Flow가 자동으로 처리)
     */
    private fun refreshMonthTasks() {
        // Flow를 사용하므로 자동으로 업데이트됩니다
    }

    /**
     * 특정 연월의 시작과 끝 타임스탬프 반환
     */
    private fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // 월의 시작 (1일 00:00:00)
        calendar.set(year, month, 1, 0, 0, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        // 월의 끝 (마지막 일 23:59:59)
        calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis

        return Pair(startOfMonth, endOfMonth)
    }

    /**
     * 특정 날짜의 시작과 끝 타임스탬프 반환
     */
    private fun getDayRange(date: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = date
        }

        // 하루의 시작 (00:00:00)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // 하루의 끝 (23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        return Pair(startOfDay, endOfDay)
    }
}
