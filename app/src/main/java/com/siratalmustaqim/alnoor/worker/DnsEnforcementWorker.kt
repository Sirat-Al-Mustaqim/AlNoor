package com.siratalmustaqim.alnoor.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.siratalmustaqim.alnoor.data.repository.GuardRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber

/**
 * Worker that periodically checks and enforces private DNS settings
 * when always-on protection is enabled
 */
@HiltWorker
class DnsEnforcementWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val guardRepository: GuardRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "dns_enforcement_work"
    }

    override suspend fun doWork(): Result {
        Timber.d("DnsEnforcementWorker running")

        return try {
            guardRepository.enforceProtectionIfNeeded()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "Error in DNS enforcement worker")
            Result.retry()
        }
    }
}
