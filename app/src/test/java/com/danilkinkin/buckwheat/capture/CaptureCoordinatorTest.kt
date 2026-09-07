package com.danilkinkin.buckwheat.capture

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CaptureCoordinatorTest {

    @Test
    fun `records observed notifications newest first`() {
        val coordinator = CaptureCoordinator(registryOf())

        coordinator.onNotification(snapshot(notificationKey = "a"))
        coordinator.onNotification(snapshot(notificationKey = "b"))

        assertEquals(
            listOf("b", "a"),
            coordinator.observedNotifications.value.map { it.notificationKey },
        )
    }

    @Test
    fun `an updated notification replaces the previous one instead of duplicating it`() {
        val coordinator = CaptureCoordinator(registryOf())

        coordinator.onNotification(snapshot(notificationKey = "a", text = "first"))
        coordinator.onNotification(snapshot(notificationKey = "a", text = "updated"))

        assertEquals(1, coordinator.observedNotifications.value.size)
        assertEquals("updated", coordinator.observedNotifications.value.single().text)
    }

    @Test
    fun `keeps the buffer bounded`() {
        val coordinator = CaptureCoordinator(registryOf())

        repeat(CaptureCoordinator.MAX_OBSERVED_NOTIFICATIONS + 10) {
            coordinator.onNotification(snapshot(notificationKey = "key-$it"))
        }

        assertEquals(
            CaptureCoordinator.MAX_OBSERVED_NOTIFICATIONS,
            coordinator.observedNotifications.value.size,
        )
    }

    @Test
    fun `returns null when no parser recognises the notification`() {
        val coordinator = CaptureCoordinator(registryOf())

        assertNull(coordinator.onNotification(snapshot()))
    }

    @Test
    fun `emits the candidate produced by a parser`() = runTest {
        val coordinator = CaptureCoordinator(
            registryOf(FakeParser(supportedPackage = "de.traderepublic.app"))
        )

        val result = coordinator.onNotification(snapshot())

        assertEquals("ALDI SÜD", result?.merchant)
        assertEquals(result, coordinator.candidates.first())
    }

    @Test
    fun `a simulated candidate takes the same path as a parsed one`() = runTest {
        val coordinator = CaptureCoordinator(registryOf())
        val simulated = candidate(merchant = "REWE")

        coordinator.onCandidate(simulated)

        assertEquals(simulated, coordinator.candidates.first())
    }

    @Test
    fun `clears observed notifications`() {
        val coordinator = CaptureCoordinator(registryOf())

        coordinator.onNotification(snapshot())
        coordinator.clearObservedNotifications()

        assertEquals(emptyList<Any>(), coordinator.observedNotifications.value)
    }
}
