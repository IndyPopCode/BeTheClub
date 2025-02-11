package com.example.betheclub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.semantics.text
import androidx.recyclerview.widget.RecyclerView

class ShotAdapter(private var shots: List<Float>) : RecyclerView.Adapter<ShotAdapter.ShotViewHolder>() {

    class ShotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shotDistanceTextView: TextView = itemView.findViewById(R.id.shotDistanceTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShotViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.shot_item, parent, false)
        return ShotViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ShotViewHolder, position: Int) {
        val currentShot = shots[position]
        holder.shotDistanceTextView.text = currentShot.toString()
    }

    override fun getItemCount(): Int {
        return shots.size
    }

    fun updateShots(newShots: List<Float>) {
        shots = newShots
        notifyDataSetChanged()
    }
}