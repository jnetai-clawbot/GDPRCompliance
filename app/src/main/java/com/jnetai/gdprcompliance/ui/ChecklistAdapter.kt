package com.jnetai.gdprcompliance.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.gdprcompliance.data.ComplianceCheck
import com.jnetai.gdprcompliance.databinding.ItemCheckBinding

class ChecklistAdapter(private val onClick: (ComplianceCheck) -> Unit) :
    ListAdapter<ComplianceCheck, ChecklistAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<ComplianceCheck>() {
        override fun areItemsTheSame(a: ComplianceCheck, b: ComplianceCheck) = a.id == b.id
        override fun areContentsTheSame(a: ComplianceCheck, b: ComplianceCheck) = a == b
    }

    inner class VH(val binding: ItemCheckBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemCheckBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = currentList[position]
        holder.binding.articleText.text = item.article
        holder.binding.statusText.text = item.status.uppercase()
        val color = if (item.status == "completed") 0xFF4CAF50 else 0xFFFF9800
        holder.binding.statusText.setTextColor(color.toInt())
        holder.binding.root.setOnClickListener { onClick(item) }
    }
}