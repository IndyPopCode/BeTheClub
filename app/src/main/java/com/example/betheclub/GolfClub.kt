package com.example.betheclub

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a golf club used in a round of golf..
 *
 * This data class stores information about a specific golf club, including its unique identifier,
 * name, and a list of shots hit with that club.
 *
 */
@Entity(tableName = "golf_clubs")
data class GolfClub(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    var shots: List<Float>
)