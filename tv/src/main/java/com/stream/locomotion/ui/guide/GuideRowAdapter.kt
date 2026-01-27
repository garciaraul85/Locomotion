package com.stream.locomotion.ui.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stream.locomotion.PlaybackActivity
import com.stream.locomotion.R
import android.content.Intent

class GuideRowAdapter(
    private val rows: List<GuideRowUi>,
    private val coordinator: HorizontalScrollCoordinator
) : RecyclerView.Adapter<GuideRowAdapter.RowViewHolder>() {

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
            val context = holder.itemView.context
            context.startActivity(Intent(context, PlaybackActivity::class.java).apply {
                putExtra(PlaybackActivity.EXTRA_STREAM_URL, DEFAULT_STREAM_URL)
                putExtra(PlaybackActivity.EXTRA_TITLE, row.channel.name)
            })
        }

        holder.programRow.layoutManager = LinearLayoutManager(
            holder.itemView.context,
            RecyclerView.HORIZONTAL,
            false
        )
        holder.programRow.adapter = ProgramAdapter(row.programs) {
            val context = holder.itemView.context
            context.startActivity(Intent(context, PlaybackActivity::class.java).apply {
                putExtra(PlaybackActivity.EXTRA_STREAM_URL, DEFAULT_STREAM_URL)
                putExtra(PlaybackActivity.EXTRA_TITLE, row.channel.name)
            })
        }

        coordinator.register(holder.programRow)
    }

    override fun getItemCount(): Int = rows.size

    companion object {
        private const val DEFAULT_STREAM_URL = "http://51.222.85.85:81/hls/loco/index.m3u8"
    }
}
