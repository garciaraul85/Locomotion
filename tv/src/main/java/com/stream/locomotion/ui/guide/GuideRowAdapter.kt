package com.stream.locomotion.ui.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stream.locomotion.R

class GuideRowAdapter(
    private val coordinator: HorizontalScrollCoordinator,
    private val onProgramClick: (GuideRowUi, ProgramUi?) -> Unit
) : RecyclerView.Adapter<GuideRowAdapter.RowViewHolder>() {

    private val rows = mutableListOf<GuideRowUi>()

    class RowViewHolder(view: android.view.View) : RecyclerView.ViewHolder(view) {
        val channelCell: LinearLayout = view.findViewById(R.id.channel_cell)
        val channelBadge: TextView = view.findViewById(R.id.channel_badge)
        val channelName: TextView = view.findViewById(R.id.channel_name)
        val channelSubtitle: TextView = view.findViewById(R.id.channel_subtitle)
        val programRow: RecyclerView = view.findViewById(R.id.program_row)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_guide_row, parent, false)
        return RowViewHolder(view)
    }

    override fun onBindViewHolder(holder: RowViewHolder, position: Int) {
        val row = rows[position]
        holder.channelBadge.text = row.channel.number
        holder.channelName.text = row.channel.name
        holder.channelSubtitle.text = row.channel.subtitle

        holder.channelCell.setOnClickListener {
            onProgramClick(row, null)
        }

        holder.programRow.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            RecyclerView.HORIZONTAL,
            false
        )
        holder.programRow.adapter = ProgramAdapter(row.programs) { program ->
            onProgramClick(row, program)
        }

        coordinator.register(holder.programRow)
    }

    override fun getItemCount(): Int = rows.size

    fun submit(newRows: List<GuideRowUi>) {
        rows.clear()
        rows.addAll(newRows)
        notifyDataSetChanged()
    }
}
