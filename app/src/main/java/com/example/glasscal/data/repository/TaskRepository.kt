package com.example.glasscal.data.repository

import com.example.glasscal.data.local.dao.TaskDao
import com.example.glasscal.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

/**
 * Repository for Task data
 * 할일 데이터를 관리하는 Repository
 *
 * 추후 Firebase 연동 시 이 Repository를 확장하여
 * 로컬 데이터와 원격 데이터를 동기화할 수 있습니다.
 */
class TaskRepository(private val taskDao: TaskDao) {

    /**
     * 모든 할일 조회 (Flow)
     */
    fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks()
    }

    /**
     * 특정 날짜의 할일 조회
     * @param startDate 시작 날짜 타임스탬프
     * @param endDate 종료 날짜 타임스탬프
     */
    fun getTasksByDate(startDate: Long, endDate: Long): Flow<List<Task>> {
        return taskDao.getTasksByDate(startDate, endDate)
    }

    /**
     * 특정 월의 할일 조회
     * @param startOfMonth 월의 시작 타임스탬프
     * @param endOfMonth 월의 끝 타임스탬프
     */
    fun getTasksByMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<Task>> {
        return taskDao.getTasksByMonth(startOfMonth, endOfMonth)
    }

    /**
     * ID로 할일 조회
     */
    suspend fun getTaskById(taskId: Long): Task? {
        return taskDao.getTaskById(taskId)
    }

    /**
     * 할일 추가
     * @return 추가된 할일의 ID
     */
    suspend fun insertTask(task: Task): Long {
        return taskDao.insertTask(task)
    }

    /**
     * 여러 할일 추가
     */
    suspend fun insertTasks(tasks: List<Task>) {
        taskDao.insertTasks(tasks)
    }

    /**
     * 할일 수정
     */
    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.copy(updatedAt = System.currentTimeMillis()))
    }

    /**
     * 할일 삭제
     */
    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    /**
     * ID로 할일 삭제
     */
    suspend fun deleteTaskById(taskId: Long) {
        taskDao.deleteTaskById(taskId)
    }

    /**
     * 모든 할일 삭제
     */
    suspend fun deleteAllTasks() {
        taskDao.deleteAllTasks()
    }

    // TODO: Firebase 연동 시 추가할 메서드들
    // - syncWithFirebase(): 로컬 데이터와 Firebase 동기화
    // - uploadImageToStorage(): 이미지를 Firebase Storage에 업로드
    // - downloadImageFromStorage(): Firebase Storage에서 이미지 다운로드
}
