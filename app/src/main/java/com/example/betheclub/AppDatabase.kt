package com.example.betheclub

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * [AppDatabase] is the Room database for the application.
 *
 * This class provides access to the database and its associated Data Access Objects (DAOs).
 * It also manages the creation and maintenance of the database instance.
 */
@Database(entities = [GolfClub::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Apply the TypeConverter
abstract class AppDatabase : RoomDatabase() {
    abstract fun golfClubDao(): GolfClubDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "golf_club_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}