package com.jnetai.gdprcompliance.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.gdprcompliance.data.BreachLog
import com.jnetai.gdprcompliance.databinding.ItemBreachBinding

class BreachAdapter : ListAdapter<BreachLog, BreachAdapter.VH>(Diff) {
    object Diff : DiffUtil.ItemCallback<BreachLog>() {
        override fun areItemsTheSame(a: BreachLog, b: BreachLog) = a.id == b.id
        override fun areContentsTheSame(a: BreachLog, b: BreachLog) = a == b
    }
    inner class VH(val binding: ItemBreachBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemBreachBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = currentList[position]
        holder.binding.titleText.text = item.title
        holder.binding.severityText.text = item.severity.uppercase()
        holder.binding.affectedText.text = "${item.affectedUsers} affected"
        holder.binding.dateText.text = item.dateOccurred.toString()
        val color = when (item.severity) { "critical" -> 0xFFF44336; "high" -> 0xFFFF9800; "medium" -> 0xFFFFC107; else -> 0xFF4CAF50 }
        holder.binding.severityText.setTextColor(color.toInt())
    }
}