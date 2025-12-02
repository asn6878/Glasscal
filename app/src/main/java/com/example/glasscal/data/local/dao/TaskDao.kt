package com.example.glasscal.data.local.dao

import androidx.room.*
import com.example.glasscal.data.local.entity.Task
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Task entity
 * Task 데이터에 접근하기 위한 DAO
 */
@Dao
interface TaskDao {

    /**
     * 모든 할일 조회 (Flow로 실시간 업데이트)
     */
    @Query("SELECT * FROM tasks ORDER BY date ASC")
    fun getAllTasks(): Flow<List<Task>>

    /**
     * 특정 날짜의 할일 조회
     * @param startDate 시작 날짜 (해당 날짜 00:00:00)
     * @param endDate 종료 날짜 (해당 날짜 23:59:59)
     */
    @Query("SELECT * FROM tasks WHERE date >= :startDate AND date <= :endDate ORDER BY createdAt ASC")
    fun getTasksByDate(startDate: Long, endDate: Long): Flow<List<Task>>

    /**
     * 특정 월의 모든 할일 조회
     * @param startOfMonth 월의 시작 타임스탬프
     * @param endOfMonth 월의 끝 타임스탬프
     */
    @Query("SELECT * FROM tasks WHERE date >= :startOfMonth AND date <= :endOfMonth ORDER BY date ASC")
    fun getTasksByMonth(startOfMonth: Long, endOfMonth: Long): Flow<List<Task>>

    /**
     * ID로 특정 할일 조회
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Long): Task?

    /**
     * 할일 추가
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    /**
     * 여러 할일 추가
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<Task>)

    /**
     * 할일 수정
     */
    @Update
    suspend fun updateTask(task: Task)

    /**
     * 할일 삭제
     */
    @Delete
    suspend fun deleteTask(task: Task)

    /**
     * ID로 할일 삭제
     */
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: Long)

    /**
     * 모든 할일 삭제
     */
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
