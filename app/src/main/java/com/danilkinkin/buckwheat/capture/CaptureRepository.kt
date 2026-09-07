package com.danilkinkin.buckwheat.capture

import com.danilkinkin.buckwheat.capture.data.NotificationFixture
import com.danilkinkin.buckwheat.capture.data.NotificationFixtureDao
import com.danilkinkin.buckwheat.notification.NotificationSnapshot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Storage of recorded notification fixtures and replay into the capture pipeline.
 *
 * Intentionally independent of [com.danilkinkin.buckwheat.di.SpendsRepository]: captured
 * data must not touch Buckwheat's budget model before the user confirms it.
 */
@Singleton
class CaptureRepository @Inject constructor(
    private val fixtureDao: NotificationFixtureDao,
    private val captureCoordinator: CaptureCoordinator,
) {
    fun fixtures(): Flow<List<NotificationFixture>> = fixtureDao.getAll()

    suspend fun saveFixture(snapshot: NotificationSnapshot, label: String) {
        fixtureDao.insert(NotificationFixture.fromSnapshot(snapshot, label))
    }

    suspend fun deleteFixture(uid: Int) = fixtureDao.deleteById(uid)

    suspend fun deleteAllFixtures() = fixtureDao.deleteAll()

    /**
     * Replays a fixture through the regular pipeline, exactly as a live notification would.
     */
    fun replay(fixture: NotificationFixture): TransactionCandidate? =
        captureCoordinator.onNotification(fixture.toSnapshot())
}
