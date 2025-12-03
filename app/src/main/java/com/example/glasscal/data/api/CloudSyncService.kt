package com.example.glasscal.data.api

import com.example.glasscal.data.model.SyncData
import com.example.glasscal.data.model.SyncResponse
import com.example.glasscal.data.model.SyncStatusResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 클라우드 동기화 API 서비스
 */
interface CloudSyncService {

    /**
     * 클라우드 데이터 가져오기
     * GET {id}
     */
    @GET("{id}")
    suspend fun getCloudData(@Path("id") id: String): Response<SyncData>

    /**
     * 동기화 상태 조회
     * GET status/{id}
     */
    @GET("status/{id}")
    suspend fun getSyncStatus(@Path("id") id: String): Response<SyncStatusResponse>

    /**
     * 클라우드 데이터 업데이트 or 동기화
     * POST {id}
     */
    @POST("{id}")
    suspend fun syncCloudData(
        @Path("id") id: String,
        @Body data: SyncData
    ): Response<SyncResponse>
}
