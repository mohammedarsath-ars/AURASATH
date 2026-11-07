package com.aurasath.music.vivimusic.updater.downloadmanager

import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.aurasath.music.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.ZipInputStream

class UpdateDownloadWorker(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val apkUrl = inputData.getString("apk_url") ?: return@withContext Result.failure()
        val version = inputData.getString("version") ?: "unknown"
        val fileSize = inputData.getString("file_size") ?: ""

        if (!apkUrl.startsWith("https://", ignoreCase = true)) {
            DownloadNotificationManager.showDownloadFailed(
                version,
                "Insecure URL: HTTPS is required"
            )
            return@withContext Result.failure()
        }

        DownloadNotificationManager.showDownloadStarting(version, fileSize)

        try {
            val url = URL(apkUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                DownloadNotificationManager.showDownloadFailed(
                    version,
                    context.getString(R.string.server_error, connection.responseCode)
                )
                return@withContext Result.failure()
            }

            val fileLength = connection.contentLength
            val inputStream = connection.inputStream

            val downloadDir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "vivi_updates"
            )
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }

            val isZip = apkUrl.contains("nightly.link") || apkUrl.endsWith(".zip")
            val downloadFile = if (isZip) File(downloadDir, "vivi_temp.zip") else File(downloadDir, "vivi.apk")
            val outputStream = FileOutputStream(downloadFile)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var totalBytesRead: Long = 0

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                if (isStopped) {
                    outputStream.close()
                    inputStream.close()
                    connection.disconnect()
                    if (downloadFile.exists()) {
                        downloadFile.delete()
                    }
                    return@withContext Result.retry()
                }

                outputStream.write(buffer, 0, bytesRead)
                totalBytesRead += bytesRead

                if (fileLength > 0) {
                    val progress = (totalBytesRead.toFloat() / fileLength.toFloat() * 100).toInt()
                    // Update notification
                    DownloadNotificationManager.updateDownloadProgress(progress, version)
                    // Update WorkManager progress for UI observation
                    setProgress(workDataOf("progress" to progress.toFloat() / 100f))
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            connection.disconnect()

            val finalFile = if (isZip) {
                val targetApkFile = File(downloadDir, "vivi.apk")
                var extracted = false
                try {
                    ZipInputStream(downloadFile.inputStream()).use { zis ->
                        var entry = zis.nextEntry
                        while (entry != null) {
                            if (!entry.isDirectory && entry.name.endsWith(".apk")) {
                                val destinationFile = File(downloadDir, entry.name)
                                val canonicalDestPath = destinationFile.canonicalPath
                                val canonicalDirPath = downloadDir.canonicalPath
                                if (!canonicalDestPath.startsWith(canonicalDirPath + File.separator)) {
                                    entry = zis.nextEntry
                                    continue
                                }
                                FileOutputStream(targetApkFile).use { fos ->
                                    zis.copyTo(fos)
                                }
                                extracted = true
                                break
                            }
                            entry = zis.nextEntry
                        }
                    }
                } catch (e: Exception) {
                    if (downloadFile.exists()) downloadFile.delete()
                    DownloadNotificationManager.showDownloadFailed(
                        version,
                        e.message ?: "Failed to extract zip file"
                    )
                    return@withContext Result.failure()
                } finally {
                    if (downloadFile.exists()) {
                        downloadFile.delete()
                    }
                }
                if (!extracted) {
                    DownloadNotificationManager.showDownloadFailed(
                        version,
                        "Could not find APK in zip"
                    )
                    return@withContext Result.failure()
                }
                targetApkFile
            } else {
                downloadFile
            }

            val expectedSha256 = inputData.getString("sha256")
            if (!expectedSha256.isNullOrBlank()) {
                val actualSha256 = computeSha256(finalFile)
                if (!actualSha256.equals(expectedSha256.trim(), ignoreCase = true)) {
                    if (finalFile.exists()) {
                        finalFile.delete()
                    }
                    DownloadNotificationManager.showDownloadFailed(
                        version,
                        "SHA-256 verification failed"
                    )
                    return@withContext Result.failure()
                }
            }

            DownloadNotificationManager.showDownloadComplete(version, finalFile.absolutePath)

            Result.success(workDataOf("file_path" to finalFile.absolutePath))
        } catch (e: Exception) {
            DownloadNotificationManager.showDownloadFailed(
                version,
                e.message ?: context.getString(R.string.download_failed)
            )
            Result.failure()
        }
    }

    private fun computeSha256(file: File): String {
        val digest = java.security.MessageDigest.getInstance("SHA-256")
        file.inputStream().use { stream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (stream.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
