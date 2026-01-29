package com.stream.locomotion.ui.guide

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.stream.locomotion.R

class ProgramAdapter(
    private val programs: List<ProgramUi>,
    private val onProgramClick: (ProgramUi) -> Unit
) : RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {

    class ProgramViewHolder(val root: FrameLayout) : RecyclerView.ViewHolder(root) {
        val title: TextView = root.findViewById(R.id.program_title)
        val time: TextView = root.findViewById(R.id.program_time)
        val progressFill: View = root.findViewById(R.id.program_progress_fill)
        val progressLine: View = root.findViewById(R.id.program_progress_line)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_program, parent, false) as FrameLayout
        return ProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.title.text = program.title
        holder.time.text = program.timeLabel
        holder.root.isActivated = program.isCurrent
        ViewCompat.setLayoutDirection(holder.root, ViewCompat.LAYOUT_DIRECTION_LTR)
        ViewCompat.setLayoutDirection(holder.progressFill, ViewCompat.LAYOUT_DIRECTION_LTR)
        ViewCompat.setLayoutDirection(holder.progressLine, ViewCompat.LAYOUT_DIRECTION_LTR)
        updateProgress(holder, program, System.currentTimeMillis())
        holder.root.setOnClickListener { onProgramClick(program) }
    }

    override fun getItemCount(): Int = programs.size

    fun updateVisible(recyclerView: RecyclerView, now: Long) {
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val holder = recyclerView.getChildViewHolder(child) as? ProgramViewHolder ?: continue
            val position = holder.bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) continue
            val program = programs.getOrNull(position) ?: continue
            updateProgress(holder, program, now)
        }
    }

    private fun updateProgress(holder: ProgramViewHolder, program: ProgramUi, now: Long) {
        if (program.endTime <= program.startTime) {
            holder.progressFill.visibility = View.GONE
            holder.progressLine.visibility = View.GONE
            return
        }

        holder.root.post {
            val width = holder.root.width
            if (width <= 0) return@post
            val isCurrent = now in program.startTime until program.endTime
            if (!isCurrent) {
                holder.progressFill.visibility = View.GONE
                holder.progressLine.visibility = View.GONE
                return@post
            }
            val total = (program.endTime - program.startTime).toFloat()
            val elapsed = (now - program.startTime).coerceIn(0L, (program.endTime - program.startTime)).toFloat()
            val fraction = if (total > 0f) elapsed / total else 0f
            val lineX = (width * fraction).toInt().coerceIn(0, width)

            holder.progressFill.visibility = View.VISIBLE
            holder.progressLine.visibility = View.VISIBLE

            val fillParams = holder.progressFill.layoutParams as FrameLayout.LayoutParams
            fillParams.width = lineX
            fillParams.leftMargin = 0
            holder.progressFill.layoutParams = fillParams

            val lineParams = holder.progressLine.layoutParams as FrameLayout.LayoutParams
            lineParams.leftMargin = lineX
            holder.progressLine.layoutParams = lineParams
        }
    }
}
