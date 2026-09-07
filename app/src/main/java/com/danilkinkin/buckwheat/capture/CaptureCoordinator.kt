package com.danilkinkin.buckwheat.capture

import com.danilkinkin.buckwheat.capture.parser.ParserRegistry
import com.danilkinkin.buckwheat.notification.NotificationSnapshot
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single entry point of the capture pipeline.
 *
 * Live notifications, replayed fixtures and the debug payment simulator all go through
 * the very same methods, so debugging never exercises a different code path than a real
 * payment does.
 */
@Singleton
class CaptureCoordinator @Inject constructor(
    private val parserRegistry: ParserRegistry,
) {
    private val _observedNotifications = MutableStateFlow<List<NotificationSnapshot>>(emptyList())

    /** Most recent notifications, newest first. Used by the debug notification inspector. */
    val observedNotifications: StateFlow<List<NotificationSnapshot>> =
        _observedNotifications.asStateFlow()

    private val _candidates = MutableSharedFlow<TransactionCandidate>(
        replay = 1,
        extraBufferCapacity = CANDIDATE_BUFFER,
    )

    val candidates: SharedFlow<TransactionCandidate> = _candidates.asSharedFlow()

    /**
     * Handles a notification snapshot and returns the recognised candidate, if any.
     */
    fun onNotification(snapshot: NotificationSnapshot): TransactionCandidate? {
        _observedNotifications.update { current ->
            (listOf(snapshot) + current.filterNot { it.notificationKey == snapshot.notificationKey })
                .take(MAX_OBSERVED_NOTIFICATIONS)
        }

        val candidate = parserRegistry.parse(snapshot) ?: return null

        onCandidate(candidate)

        return candidate
    }

    /**
     * Handles an already recognised candidate. Used by parsers and by the debug simulator.
     */
    fun onCandidate(candidate: TransactionCandidate) {
        _candidates.tryEmit(candidate)
    }

    fun clearObservedNotifications() {
        _observedNotifications.value = emptyList()
    }

    companion object {
        const val MAX_OBSERVED_NOTIFICATIONS = 50
        private const val CANDIDATE_BUFFER = 16
    }
}
