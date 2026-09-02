package com.danilkinkin.buckwheat.capture

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.danilkinkin.buckwheat.capture.data.NotificationFixture
import com.danilkinkin.buckwheat.notification.NotificationSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Date
import javax.inject.Inject

/**
 * Backs the debug capture screens. All logic lives here so the composables stay dumb.
 */
@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val captureRepository: CaptureRepository,
    private val captureCoordinator: CaptureCoordinator,
) : ViewModel() {

    val observedNotifications = captureCoordinator.observedNotifications.asLiveData()

    val fixtures = captureRepository.fixtures().asLiveData()

    val lastCandidate = captureCoordinator.candidates.asLiveData()

    fun saveFixture(snapshot: NotificationSnapshot, label: String) {
        viewModelScope.launch {
            captureRepository.saveFixture(snapshot, label)
        }
    }

    fun deleteFixture(fixture: NotificationFixture) {
        viewModelScope.launch {
            captureRepository.deleteFixture(fixture.uid)
        }
    }

    fun replayFixture(fixture: NotificationFixture): TransactionCandidate? =
        captureRepository.replay(fixture)

    fun clearObservedNotifications() = captureCoordinator.clearObservedNotifications()

    /**
     * Feeds a manually entered payment into the capture pipeline, bypassing the parsers.
     */
    fun simulatePayment(
        merchant: String,
        amount: BigDecimal,
        currency: String,
        occurredAt: Date = Date(),
    ): TransactionCandidate {
        val candidate = TransactionCandidate(
            amount = amount,
            currency = currency,
            merchant = merchant,
            occurredAt = occurredAt,
            sourcePackage = SIMULATOR_SOURCE,
            sourceNotificationKey = null,
            transactionType = CaptureTransactionType.PAYMENT,
            confidence = 1f,
        )

        captureCoordinator.onCandidate(candidate)

        return candidate
    }

    companion object {
        const val SIMULATOR_SOURCE = "debug.simulator"
    }
}
