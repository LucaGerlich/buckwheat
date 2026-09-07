package com.danilkinkin.buckwheat.capture.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationFixtureDao {
    @Query("SELECT * FROM notification_fixtures ORDER BY recorded_at DESC")
    fun getAll(): Flow<List<NotificationFixture>>

    @Query("SELECT * FROM notification_fixtures WHERE uid = :uid")
    suspend fun getById(uid: Int): NotificationFixture?

    @Insert
    suspend fun insert(fixture: NotificationFixture): Long

    @Query("DELETE FROM notification_fixtures WHERE uid = :uid")
    suspend fun deleteById(uid: Int)

    @Query("DELETE FROM notification_fixtures")
    suspend fun deleteAll()
}
