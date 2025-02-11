package com.example.betheclub

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainActivity is the main entry point of the application.
 * It displays a list of golf clubs, allows users to add new clubs,
 * delete existing clubs via swipe, get club suggestions based on distance,
 * and access a help screen.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var golfClubDao: GolfClubDao
    private lateinit var db: AppDatabase
    private lateinit var clubAdapter: ClubAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.clubsRecyclerView)
        db = AppDatabase.getDatabase(applicationContext)
        golfClubDao = db.golfClubDao()
        clubAdapter = ClubAdapter(emptyList(), this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = clubAdapter

        val addButton: Button = findViewById(R.id.addClubButton)
        addButton.setOnClickListener {
            val intent = Intent(this, AddClubActivity::class.java)
            startActivity(intent)
        }

        // Initialize ItemTouchHelper for swipe-to-delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clubToDelete = clubAdapter.clubs[position]

                    // Show confirmation dialog
                    AlertDialog.Builder(this@MainActivity)
                        .setTitle("Confirm Delete")
                        .setMessage("Are you sure you want to delete ${clubToDelete.name}?")
                        .setPositiveButton("Delete") { _, _ ->
                            // User confirmed, delete the club
                            lifecycleScope.launch {
                                withContext(Dispatchers.IO) {
                                    golfClubDao.delete(clubToDelete)
                                }
                                // Update adapter on the main thread
                                withContext(Dispatchers.Main) {
                                    clubAdapter.removeAt(position)
                                }
                            }
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            // User canceled, do nothing
                            dialog.dismiss()
                            clubAdapter.notifyItemChanged(position)
                        }
                        .setOnCancelListener {
                            clubAdapter.notifyItemChanged(position)
                        }
                        .show()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        loadClubs()

        val inputEditText: EditText = findViewById(R.id.inputEditText)
        val suggestButton: Button = findViewById(R.id.suggestButton)

        suggestButton.setOnClickListener {
            val inputYards = inputEditText.text.toString().toIntOrNull()

            if (inputYards != null) {
                val suggestedClub = findClosestClub(inputYards)

                if (suggestedClub != null) {
                    // Show dialog with suggested club
                    val dialog = AlertDialog.Builder(this)
                        .setTitle("Suggested Club")
                        .setMessage("Based on your input, I suggest using the ${suggestedClub.name} club.")
                        .setPositiveButton("OK", null)
                        .create()
                    dialog.window?.setDimAmount(0.8f)
                    dialog.show()
                } else {
                    // Handle case where no suitable club is found
                    Toast.makeText(this, "No suitable club found for that distance.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Handle invalid input
                Toast.makeText(this, "Please enter a valid number.", Toast.LENGTH_SHORT).show()
            }
        }
        val helpButton = findViewById<Button>(R.id.helpButton)

        helpButton.setOnClickListener {
            val intent = Intent(this, HelpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadClubs() {
        lifecycleScope.launch {
            golfClubDao.getAllGolfClubs().collect { golfClubs ->
                withContext(Dispatchers.Main) {
                    clubAdapter.updateClubs(golfClubs)
                }
            }
        }
    }

    /**
     * Finds the closest golf club based on the provided input yards.
     *
     * This function iterates through a list of golf clubs and determines which club's
     * average shot distance is closest to the `inputYards`. It prioritizes clubs
     * that are within a 10-yard difference of the target distance.
     *
     * @param inputYards The target distance in yards for which to find the closest club.
     * @return The GolfClub object that is closest to the `inputYards`, or `null` if no club
     *         is within 10 yards of the target.
     */
    fun findClosestClub(inputYards: Int): GolfClub? {
        var closestClub: GolfClub? = null
        var minDistance = Int.MAX_VALUE

        for (club in clubAdapter.clubs) {
            val averageDistance = club.shots.average().toInt()
            val distanceDiff = Math.abs(inputYards - averageDistance)

            if (distanceDiff < minDistance && distanceDiff <= 10) {
                closestClub = club
                minDistance = distanceDiff
            }
        }

        return closestClub
    }
}