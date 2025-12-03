package com.example.glasscal.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * 동기화 관련 데이터를 저장하는 SharedPreferences 관리 클래스
 */
class SyncPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val PREF_NAME = "glasscal_sync_prefs"
        private const val KEY_SYNC_ID = "sync_id"
        private const val KEY_LAST_SYNC_DATE = "last_sync_date"
        private const val KEY_IS_SYNCED = "is_synced"
    }

    /**
     * 동기화 ID 저장
     */
    fun saveSyncId(id: String) {
        prefs.edit().putString(KEY_SYNC_ID, id).apply()
    }

    /**
     * 동기화 ID 가져오기
     */
    fun getSyncId(): String? {
        return prefs.getString(KEY_SYNC_ID, null)
    }

    /**
     * 동기화 ID 삭제
     */
    fun clearSyncId() {
        prefs.edit().remove(KEY_SYNC_ID).apply()
    }

    /**
     * 마지막 동기화 날짜 저장
     */
    fun saveLastSyncDate(date: String) {
        prefs.edit().putString(KEY_LAST_SYNC_DATE, date).apply()
    }

    /**
     * 마지막 동기화 날짜 가져오기
     */
    fun getLastSyncDate(): String? {
        return prefs.getString(KEY_LAST_SYNC_DATE, null)
    }

    /**
     * 동기화 상태 저장
     */
    fun setIsSynced(synced: Boolean) {
        prefs.edit().putBoolean(KEY_IS_SYNCED, synced).apply()
    }

    /**
     * 동기화 상태 가져오기
     */
    fun isSynced(): Boolean {
        return prefs.getBoolean(KEY_IS_SYNCED, false)
    }

    /**
     * 모든 동기화 데이터 삭제
     */
    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
