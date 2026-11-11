package com.aurasath.music.api

import com.aurasath.music.AppUpdateRow
import com.aurasath.music.NewsUpdateRow
import com.aurasath.music.SuggestionRow
import com.aurasath.music.BugReportRow
import com.aurasath.music.SupabaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object SupabaseService {
    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun checkLatestUpdate(): Result<AppUpdateRow?> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${SupabaseConfig.URL}/rest/v1/app_updates?order=created_at.desc&limit=1")
                .get()
                .header("apikey", SupabaseConfig.ANON_KEY)
                .header("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@use Result.success(null)
                    val updates = json.decodeFromString<List<AppUpdateRow>>(body)
                    Result.success(updates.firstOrNull())
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Result.failure(Exception("HTTP error: ${response.code} ${response.message} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitSuggestion(userName: String, instaId: String?, content: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val payload = buildJsonObject {
                put("user_name", userName)
                if (!instaId.isNullOrBlank()) put("insta_id", instaId)
                put("content", content)
                put("status", "pending")
            }
            val bodyStr = payload.toString()
            val request = Request.Builder()
                .url("${SupabaseConfig.URL}/rest/v1/suggestions")
                .post(bodyStr.toRequestBody(mediaType))
                .header("apikey", SupabaseConfig.ANON_KEY)
                .header("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val list = json.decodeFromString<List<SuggestionRow>>(body)
                    val inserted = list.firstOrNull()
                    if (inserted?.id != null) {
                        Result.success(inserted.id)
                    } else {
                        Result.failure(Exception("Failed to retrieve suggestion ID"))
                    }
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Result.failure(Exception("HTTP error: ${response.code} ${response.message} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchSuggestionsByIds(ids: List<Long>): Result<List<SuggestionRow>> = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) return@withContext Result.success(emptyList())
        try {
            val idsParam = ids.joinToString(",")
            val request = Request.Builder()
                .url("${SupabaseConfig.URL}/rest/v1/suggestions?id=in.($idsParam)&order=created_at.desc")
                .get()
                .header("apikey", SupabaseConfig.ANON_KEY)
                .header("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@use Result.success(emptyList())
                    val list = json.decodeFromString<List<SuggestionRow>>(body)
                    Result.success(list)
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Result.failure(Exception("HTTP error: ${response.code} ${response.message} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun submitBugReport(userName: String, instaId: String?, description: String, deviceInfo: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val payload = buildJsonObject {
                put("user_name", userName)
                if (!instaId.isNullOrBlank()) put("insta_id", instaId)
                put("description", description)
                put("device_info", deviceInfo)
                put("status", "pending")
            }
            val bodyStr = payload.toString()
            val request = Request.Builder()
                .url("${SupabaseConfig.URL}/rest/v1/bug_reports")
                .post(bodyStr.toRequestBody(mediaType))
                .header("apikey", SupabaseConfig.ANON_KEY)
                .header("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
                .header("Content-Type", "application/json")
                .header("Prefer", "return=representation")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    val list = json.decodeFromString<List<BugReportRow>>(body)
                    val inserted = list.firstOrNull()
                    if (inserted?.id != null) {
                        Result.success(inserted.id)
                    } else {
                        Result.failure(Exception("Failed to retrieve bug report ID"))
                    }
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Result.failure(Exception("HTTP error: ${response.code} ${response.message} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchBugReportsByIds(ids: List<Long>): Result<List<BugReportRow>> = withContext(Dispatchers.IO) {
        if (ids.isEmpty()) return@withContext Result.success(emptyList())
        try {
            val idsParam = ids.joinToString(",")
            val request = Request.Builder()
                .url("${SupabaseConfig.URL}/rest/v1/bug_reports?id=in.($idsParam)&order=created_at.desc")
                .get()
                .header("apikey", SupabaseConfig.ANON_KEY)
                .header("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@use Result.success(emptyList())
                    val list = json.decodeFromString<List<BugReportRow>>(body)
                    Result.success(list)
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Result.failure(Exception("HTTP error: ${response.code} ${response.message} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun checkLatestNews(): Result<NewsUpdateRow?> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("${SupabaseConfig.URL}/rest/v1/news_updates?order=created_at.desc&limit=1")
                .get()
                .header("apikey", SupabaseConfig.ANON_KEY)
                .header("Authorization", "Bearer ${SupabaseConfig.ANON_KEY}")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: return@use Result.success(null)
                    val list = json.decodeFromString<List<NewsUpdateRow>>(body)
                    Result.success(list.firstOrNull())
                } else {
                    val errorBody = response.body?.string() ?: ""
                    Result.failure(Exception("HTTP error: ${response.code} ${response.message} - $errorBody"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
