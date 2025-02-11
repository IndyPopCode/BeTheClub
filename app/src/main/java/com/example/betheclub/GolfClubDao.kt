package com.example.betheclub

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for interacting with the [GolfClub] entity in the Room database.
 *
 */
@Dao
interface GolfClubDao {
    @Insert
    suspend fun insert(golfClub: GolfClub)

    @Update
    suspend fun update(golfClub: GolfClub)

    @Delete
    suspend fun delete(golfClub: GolfClub)

    @Query("SELECT * FROM golf_clubs")
    fun getAllGolfClubs(): Flow<List<GolfClub>>

    @Query("SELECT * FROM golf_clubs WHERE id = :clubId")
    fun getGolfClubById(clubId: Int): GolfClub?
}
