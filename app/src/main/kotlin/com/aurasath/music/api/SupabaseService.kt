package com.aurasath.music.api

import com.aurasath.music.AppUpdateRow
import com.aurasath.music.NewsUpdateRow
import com.aurasath.music.SuggestionRow
import com.aurasath.music.BugReportRow
import com.aurasath.music.SupabaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class AdminAuthException(message: String = "Incorrect password") : Exception(message)

object SupabaseService {
    private val client = OkHttpClient()
    private val adminClient = client.newBuilder()
        .callTimeout(12, TimeUnit.SECONDS)
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .build()

    private val json = Json { ignoreUnknownKeys = true }
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    @Volatile
    private var adminSessionPassword: String? = null

    var onAuthFailureListener: (() -> Unit)? = null

    fun setAdminPassword(password: String) {
        adminSessionPassword = password
    }

    fun clearAdminPassword() {
        adminSessionPassword = null
    }

    fun hasAdminSession(): Boolean = !adminSessionPassword.isNullOrBlank()

    private fun handleAuthFailure() {
        clearAdminPassword()
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            onAuthFailureListener?.invoke()
        }
    }

    private const val ADMIN_PASSWORD = "Aurasath_0805"

    private suspend fun callAdminApi(
        action: String,
        passwordOverride: String? = null,
        payload: JsonObject = buildJsonObject {}
    ): Result<String> = withContext(Dispatchers.IO) {
        val password = passwordOverride ?: adminSessionPassword
        if (password.isNullOrBlank() || password != ADMIN_PASSWORD) {
            handleAuthFailure()
            return@withContext Result.failure(AdminAuthException("Incorrect password"))
        }

        try {
            when (action) {
                "verify" -> {
                    Result.success("""{"success":true}""")
                }
                "fetch_bug_reports" -> {
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/bug_reports?order=created_at.desc")
                        .get()
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            Result.success(response.body?.string() ?: "[]")
                        } else {
                            Result.failure(Exception("HTTP error: ${response.code} ${response.message}"))
                        }
                    }
                }
                "fetch_suggestions" -> {
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/suggestions?order=created_at.desc")
                        .get()
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            Result.success(response.body?.string() ?: "[]")
                        } else {
                            Result.failure(Exception("HTTP error: ${response.code} ${response.message}"))
                        }
                    }
                }
                "update_bug_report_status" -> {
                    val id = payload["id"]?.toString() ?: ""
                    val status = payload["status"]?.toString()?.trim('"') ?: "pending"
                    val body = """{"status":"$status"}"""
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/bug_reports?id=eq.$id")
                        .patch(body.toRequestBody(mediaType))
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .header("Content-Type", "application/json")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) Result.success("""{"success":true}""")
                        else Result.failure(Exception("HTTP error: ${response.code}"))
                    }
                }
                "update_suggestion_status" -> {
                    val id = payload["id"]?.toString() ?: ""
                    val status = payload["status"]?.toString()?.trim('"') ?: "pending"
                    val body = """{"status":"$status"}"""
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/suggestions?id=eq.$id")
                        .patch(body.toRequestBody(mediaType))
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .header("Content-Type", "application/json")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) Result.success("""{"success":true}""")
                        else Result.failure(Exception("HTTP error: ${response.code}"))
                    }
                }
                "update_bug_report_comment" -> {
                    val id = payload["id"]?.toString() ?: ""
                    val comment = payload["admin_comment"]?.toString()?.trim('"')
                    val body = if (comment != null) buildJsonObject { put("admin_comment", comment) }.toString() else """{"admin_comment":null}"""
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/bug_reports?id=eq.$id")
                        .patch(body.toRequestBody(mediaType))
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .header("Content-Type", "application/json")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) Result.success("""{"success":true}""")
                        else Result.failure(Exception("HTTP error: ${response.code}"))
                    }
                }
                "update_suggestion_comment" -> {
                    val id = payload["id"]?.toString() ?: ""
                    val comment = payload["admin_comment"]?.toString()?.trim('"')
                    val body = if (comment != null) buildJsonObject { put("admin_comment", comment) }.toString() else """{"admin_comment":null}"""
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/suggestions?id=eq.$id")
                        .patch(body.toRequestBody(mediaType))
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .header("Content-Type", "application/json")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) Result.success("""{"success":true}""")
                        else Result.failure(Exception("HTTP error: ${response.code}"))
                    }
                }
                "delete_bug_report" -> {
                    val id = payload["id"]?.toString() ?: ""
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/bug_reports?id=eq.$id")
                        .delete()
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) Result.success("""{"success":true}""")
                        else Result.failure(Exception("HTTP error: ${response.code}"))
                    }
                }
                "delete_suggestion" -> {
                    val id = payload["id"]?.toString() ?: ""
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/suggestions?id=eq.$id")
                        .delete()
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) Result.success("""{"success":true}""")
                        else Result.failure(Exception("HTTP error: ${response.code}"))
                    }
                }
                "publish_update" -> {
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/app_updates")
                        .post(payload.toString().toRequestBody(mediaType))
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .header("Content-Type", "application/json")
                        .header("Prefer", "return=representation")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) Result.success(response.body?.string() ?: """{"success":true}""")
                        else Result.failure(Exception("HTTP error: ${response.code}"))
                    }
                }
                "publish_news" -> {
                    val request = Request.Builder()
                        .url("${SupabaseConfig.URL}/rest/v1/news_updates")
                        .post(payload.toString().toRequestBody(mediaType))
                        .header("apikey", SupabaseConfig.SECRET_KEY)
                        .header("Authorization", "Bearer ${SupabaseConfig.SECRET_KEY}")
                        .header("Content-Type", "application/json")
                        .header("Prefer", "return=representation")
                        .build()
                    adminClient.newCall(request).execute().use { response ->
                        if (response.isSuccessful) Result.success(response.body?.string() ?: """{"success":true}""")
                        else Result.failure(Exception("HTTP error: ${response.code}"))
                    }
                }
                else -> Result.failure(Exception("Unknown action: $action"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyAdminPassword(password: String): Result<Unit> = withContext(Dispatchers.IO) {
        if (password.isBlank()) {
            return@withContext Result.failure(AdminAuthException("Incorrect password"))
        }
        val result = callAdminApi(action = "verify", passwordOverride = password)
        if (result.isSuccess) {
            setAdminPassword(password)
            Result.success(Unit)
        } else {
            clearAdminPassword()
            Result.failure(result.exceptionOrNull() ?: AdminAuthException("Incorrect password"))
        }
    }

    suspend fun publishUpdate(update: AppUpdateRow): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("version", update.version)
            put("title", update.title)
            put("description", update.description)
            put("update_type", update.update_type)
            update.apk_url?.let { put("apk_url", it) }
            update.sha256?.let { put("sha256", it) }
        }
        val res = callAdminApi("publish_update", payload = payload)
        if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception("Failed to publish update"))
    }

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

    suspend fun fetchBugReports(): Result<List<BugReportRow>> = withContext(Dispatchers.IO) {
        val res = callAdminApi("fetch_bug_reports")
        res.mapCatching { body ->
            json.decodeFromString<List<BugReportRow>>(body)
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

    suspend fun updateBugReportStatus(id: Long, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("id", id)
            put("status", status)
        }
        val res = callAdminApi("update_bug_report_status", payload = payload)
        if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception("Failed to update bug report status"))
    }

    suspend fun fetchSuggestions(): Result<List<SuggestionRow>> = withContext(Dispatchers.IO) {
        val res = callAdminApi("fetch_suggestions")
        res.mapCatching { body ->
            json.decodeFromString<List<SuggestionRow>>(body)
        }
    }

    suspend fun updateSuggestionStatus(id: Long, status: String): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("id", id)
            put("status", status)
        }
        val res = callAdminApi("update_suggestion_status", payload = payload)
        if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception("Failed to update status"))
    }

    suspend fun updateSuggestionComment(id: Long, comment: String): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("id", id)
            if (comment.isNotBlank()) {
                put("admin_comment", comment)
            }
        }
        val res = callAdminApi("update_suggestion_comment", payload = payload)
        if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception("Failed to update comment"))
    }

    suspend fun updateBugReportComment(id: Long, comment: String): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("id", id)
            if (comment.isNotBlank()) {
                put("admin_comment", comment)
            }
        }
        val res = callAdminApi("update_bug_report_comment", payload = payload)
        if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception("Failed to update bug report comment"))
    }

    suspend fun deleteSuggestion(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("id", id)
        }
        val res = callAdminApi("delete_suggestion", payload = payload)
        if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception("Failed to delete suggestion"))
    }

    suspend fun deleteBugReport(id: Long): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("id", id)
        }
        val res = callAdminApi("delete_bug_report", payload = payload)
        if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception("Failed to delete bug report"))
    }

    suspend fun publishNews(news: NewsUpdateRow): Result<Unit> = withContext(Dispatchers.IO) {
        val payload = buildJsonObject {
            put("title", news.title)
            put("content", news.content)
            news.image_url?.let { put("image_url", it) }
        }
        val res = callAdminApi("publish_news", payload = payload)
        if (res.isSuccess) Result.success(Unit) else Result.failure(res.exceptionOrNull() ?: Exception("Failed to publish news"))
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
