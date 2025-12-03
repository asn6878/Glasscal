package com.example.glasscal.data.repository

import android.content.Context
import com.example.glasscal.data.api.RetrofitClient
import com.example.glasscal.data.local.SyncPreferences
import com.example.glasscal.data.local.entity.Task
import com.example.glasscal.data.model.SyncData
import com.example.glasscal.data.model.TaskData
import java.util.UUID

/**
 * 클라우드 동기화 Repository
 */
class CloudSyncRepository(context: Context, private val taskRepository: TaskRepository) {

    private val syncPrefs = SyncPreferences(context)
    private val apiService = RetrofitClient.cloudSyncService

    /**
     * 동기화 ID 가져오기 (없으면 생성)
     */
    fun getOrCreateSyncId(): String {
        var syncId = syncPrefs.getSyncId()
        if (syncId == null) {
            syncId = UUID.randomUUID().toString()
            syncPrefs.saveSyncId(syncId)
        }
        return syncId
    }

    /**
     * 동기화 ID 가져오기
     */
    fun getSyncId(): String? {
        return syncPrefs.getSyncId()
    }

    /**
     * 동기화 상태 확인
     */
    fun isSynced(): Boolean {
        return syncPrefs.isSynced()
    }

    /**
     * 마지막 동기화 날짜 가져오기
     */
    fun getLastSyncDate(): String? {
        return syncPrefs.getLastSyncDate()
    }

    /**
     * 클라우드에서 동기화 상태 확인
     */
    suspend fun checkSyncStatus(): Result<Pair<Boolean, String?>> {
        return try {
            val syncId = getSyncId() ?: return Result.failure(Exception("동기화 ID가 없습니다"))
            val response = apiService.getSyncStatus(syncId)

            if (response.isSuccessful) {
                val statusResponse = response.body()
                if (statusResponse != null) {
                    syncPrefs.setIsSynced(statusResponse.synced)
                    statusResponse.lastSyncDate?.let { syncPrefs.saveLastSyncDate(it) }
                    Result.success(Pair(statusResponse.synced, statusResponse.lastSyncDate))
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 로컬 데이터를 클라우드에 동기화
     */
    suspend fun syncToCloud(tasks: List<Task>): Result<String> {
        return try {
            val syncId = getOrCreateSyncId()

            // Task를 TaskData로 변환
            val taskDataList = tasks.map { task ->
                TaskData(
                    id = task.id,
                    title = task.title,
                    content = task.content,
                    date = task.date,
                    imageUri = task.imageUri,
                    createdAt = task.createdAt,
                    updatedAt = task.updatedAt
                )
            }

            val syncData = SyncData(tasks = taskDataList)
            val response = apiService.syncCloudData(syncId, syncData)

            if (response.isSuccessful) {
                val syncResponse = response.body()
                if (syncResponse != null && syncResponse.success) {
                    syncPrefs.setIsSynced(true)
                    syncResponse.syncDate?.let { syncPrefs.saveLastSyncDate(it) }
                    Result.success(syncId)
                } else {
                    Result.failure(Exception(syncResponse?.message ?: "동기화 실패"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 클라우드에서 데이터 가져오기
     */
    suspend fun fetchFromCloud(syncId: String): Result<List<Task>> {
        return try {
            val response = apiService.getCloudData(syncId)

            if (response.isSuccessful) {
                val syncData = response.body()
                if (syncData != null) {
                    // TaskData를 Task로 변환
                    val tasks = syncData.tasks.map { taskData ->
                        Task(
                            id = 0, // Room이 자동 생성하도록 0으로 설정
                            title = taskData.title,
                            content = taskData.content,
                            date = taskData.date,
                            imageUri = taskData.imageUri,
                            createdAt = taskData.createdAt,
                            updatedAt = taskData.updatedAt
                        )
                    }

                    // 로컬 데이터 삭제 후 클라우드 데이터 저장
                    taskRepository.deleteAllTasks()
                    taskRepository.insertTasks(tasks)

                    // 동기화 정보 저장
                    syncPrefs.saveSyncId(syncId)
                    syncPrefs.setIsSynced(true)

                    Result.success(tasks)
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다"))
                }
            } else {
                Result.failure(Exception("서버 오류: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 모든 데이터 삭제 (로컬 + 클라우드)
     */
    suspend fun deleteAllData(): Result<Unit> {
        return try {
            // 로컬 데이터 삭제
            taskRepository.deleteAllTasks()

            // 동기화 정보 삭제
            syncPrefs.clearAll()

            // TODO: 클라우드 데이터 삭제 API가 추가되면 구현
            // 현재는 로컬 데이터와 동기화 정보만 삭제

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
