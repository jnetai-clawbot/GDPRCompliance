package com.jnetai.gdprcompliance.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jnetai.gdprcompliance.data.ProcessingActivity
import com.jnetai.gdprcompliance.databinding.ItemActivityBinding

class ActivityAdapter(private val onClick: (ProcessingActivity) -> Unit) :
    ListAdapter<ProcessingActivity, ActivityAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<ProcessingActivity>() {
        override fun areItemsTheSame(a: ProcessingActivity, b: ProcessingActivity) = a.id == b.id
        override fun areContentsTheSame(a: ProcessingActivity, b: ProcessingActivity) = a == b
    }

    inner class VH(val binding: ItemActivityBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = currentList[position]
        holder.binding.nameText.text = item.name
        holder.binding.basisText.text = "Legal basis: ${item.legalBasis}"
        holder.binding.categoryText.text = item.dataCategories
        holder.binding.root.setOnClickListener { onClick(item) }
    }
}