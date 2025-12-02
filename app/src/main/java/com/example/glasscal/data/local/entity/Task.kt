package com.example.glasscal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity for Task data
 * 할일 데이터를 저장하는 Room Entity
 */
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    /** 할일 제목 */
    val title: String,

    /** 할일 내용 */
    val content: String,

    /** 할일 날짜 (Unix timestamp in milliseconds) */
    val date: Long,

    /** 이미지 URI (로컬 파일 경로 또는 Firebase Storage URL) */
    val imageUri: String? = null,

    /** 생성 시간 */
    val createdAt: Long = System.currentTimeMillis(),

    /** 수정 시간 */
    val updatedAt: Long = System.currentTimeMillis()
)
