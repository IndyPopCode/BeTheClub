package com.example.betheclub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity for adding a new golf club to the database.
 *
 * This activity allows users to input a name for a new golf club and save it
 * to the local database. It interacts with the {@link GolfClubDao} to perform
 * database operations.
 */
class AddClubActivity : AppCompatActivity() {
    private lateinit var golfClubDao: GolfClubDao
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_club)

        db = AppDatabase.getDatabase(applicationContext)
        golfClubDao = db.golfClubDao()

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            val clubName = findViewById<EditText>(R.id.clubNameEditText).text.toString()

            if (clubName.isNotEmpty()) {
                val golfClub = GolfClub(name = clubName, shots = emptyList())
                CoroutineScope(Dispatchers.IO).launch {
                    golfClubDao.insert(golfClub)
                    withContext(Dispatchers.Main) {
                        finish()
                    }
                }
            } else {
                Toast.makeText(this, "Club name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}