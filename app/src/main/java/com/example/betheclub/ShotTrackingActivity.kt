package com.example.betheclub

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ShotTrackingActivity
 *
 * Allows users to track individual shot distances for a specific golf club.
 *
 * Users input shot distances, which are then stored in the database, associated with
 * the selected golf club.
 * Layout: activity_shot_tracking.xml
 * - R.id.shotDistanceEditText: Input field for shot distance.
 * - R.id.addShotButton: Button to add the entered shot.
 */
class ShotTrackingActivity : AppCompatActivity() {
    private lateinit var shotAdapter: ShotAdapter
    private lateinit var golfClubDao: GolfClubDao
    private lateinit var db: AppDatabase
    private var clubId: Int = -1
    private lateinit var shotsRecyclerView: RecyclerView
    private lateinit var addShotButton: Button
    private lateinit var shotDistanceEditText: EditText

    /**
     * Activity for tracking shots for a specific golf club.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shot_tracking)

        clubId = intent.getIntExtra("clubId", -1)
        if (clubId == -1) {
            Toast.makeText(this, "Invalid club ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db = AppDatabase.getDatabase(applicationContext)
        golfClubDao = db.golfClubDao()

        shotsRecyclerView = findViewById(R.id.shotsRecyclerView)
        addShotButton = findViewById(R.id.addShotButton)
        shotDistanceEditText = findViewById(R.id.shotDistanceEditText)

        shotAdapter = ShotAdapter(emptyList())
        shotsRecyclerView.layoutManager = LinearLayoutManager(this)
        shotsRecyclerView.adapter = shotAdapter

        loadGolfClubData()

        addShotButton.setOnClickListener {
            addShot()
        }
    }

    private fun loadGolfClubData() {
        lifecycleScope.launch {
            val golfClub = withContext(Dispatchers.IO) {
                golfClubDao.getGolfClubById(clubId)
            }

            if (golfClub == null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ShotTrackingActivity, "Club not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                withContext(Dispatchers.Main) {
                    shotAdapter.updateShots(golfClub.shots)
                }
            }
        }
    }

    private fun addShot() {
        val shotDistance = shotDistanceEditText.text.toString().toFloatOrNull()

        if (shotDistance != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val golfClub = golfClubDao.getGolfClubById(clubId) ?: return@launch
                val updatedShots = golfClub.shots.toMutableList()
                updatedShots.add(shotDistance)
                golfClub.shots = updatedShots
                golfClubDao.update(golfClub)

                withContext(Dispatchers.Main) {
                    shotAdapter.updateShots(golfClub.shots)
                    shotDistanceEditText.text.clear()
                }
            }
        } else {
            Toast.makeText(this@ShotTrackingActivity, "Please enter a valid shot distance.", Toast.LENGTH_SHORT).show()
        }
    }
}