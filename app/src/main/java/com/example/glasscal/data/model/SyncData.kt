package com.example.glasscal.data.model

import com.google.gson.annotations.SerializedName

/**
 * 클라우드 동기화 데이터 모델
 */
data class SyncData(
    @SerializedName("tasks")
    val tasks: List<TaskData>
)

/**
 * API 전송용 Task 데이터
 */
data class TaskData(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("content")
    val content: String?,

    @SerializedName("date")
    val date: Long?,

    @SerializedName("imageUri")
    val imageUri: String?,

    @SerializedName("createdAt")
    val createdAt: Long?,

    @SerializedName("updatedAt")
    val updatedAt: Long?
)

/**
 * 동기화 상태 응답 모델
 */
data class SyncStatusResponse(
    @SerializedName("synced")
    val synced: Boolean,

    @SerializedName("lastSyncDate")
    val lastSyncDate: String?
)

/**
 * 동기화 응답 모델
 */
data class SyncResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("syncDate")
    val syncDate: String?
)
