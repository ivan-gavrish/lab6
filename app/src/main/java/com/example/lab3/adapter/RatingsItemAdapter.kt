package com.example.lab3.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lab3.databinding.RatingItemBinding
import com.example.lab3.shared.model.ScoreRecord

class RatingsItemAdapter(
    private val context: Context,
    private val scoreRecords: List<ScoreRecord>
) : RecyclerView.Adapter<RatingsItemAdapter.RatingsItemViewHolder>() {
    inner class RatingsItemViewHolder(private val binding: RatingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(scoreRecord: ScoreRecord) {
                    binding.tvEmail.text = scoreRecord.email
                    binding.tvScore.text = scoreRecord.score.toString()
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingsItemViewHolder {
        val binding = RatingItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return RatingsItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingsItemViewHolder, position: Int) {
        holder.bind(scoreRecords[position])
    }

    override fun getItemCount() = scoreRecords.size
}