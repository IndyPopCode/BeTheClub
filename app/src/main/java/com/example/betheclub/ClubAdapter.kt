package com.example.betheclub

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView

/**
 * [ClubAdapter] is a RecyclerView adapter responsible for displaying a list of [GolfClub] objects.
 *
 * It provides functionality to:
 * - Display the name and average distance of each club.
 * - Navigate to the [ShotTrackingActivity] when a club item is clicked, passing the club ID.
 * - Remove a club from the list.
 * - Update the list of clubs.
 */
class ClubAdapter(var clubs: List<GolfClub>, private val context: Context) :
    RecyclerView.Adapter<ClubAdapter.ClubViewHolder>() {

    inner class ClubViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val clubNameTextView: TextView = itemView.findViewById(R.id.clubNameText)
        val averageDistanceTextView: TextView = itemView.findViewById(R.id.clubAvgText)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clubId = clubs[position].id
                    val intent = Intent(context, ShotTrackingActivity::class.java)
                    intent.putExtra("clubId", clubId)
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClubViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_club, parent, false)
        return ClubViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ClubViewHolder, position: Int) {
        val currentClub = clubs[position]
        holder.clubNameTextView.text = currentClub.name

        // Calculate and display average distance
        val averageDistance = if (currentClub.shots.isNotEmpty()) {
            String.format("%.1f", currentClub.shots.average()) // Format to one decimal place
        } else {
            "N/A"
        }
        holder.averageDistanceTextView.text = "Avg Distance: $averageDistance"
    }

    override fun getItemCount(): Int {
        return clubs.size
    }

    fun removeAt(position: Int) {
        val mutableClubs = clubs.toMutableList()
        mutableClubs.removeAt(position)
        clubs = mutableClubs
        notifyItemRemoved(position)
    }

    fun updateClubs(newClubs: List<GolfClub>) {
        clubs = newClubs
        notifyDataSetChanged()
    }
}