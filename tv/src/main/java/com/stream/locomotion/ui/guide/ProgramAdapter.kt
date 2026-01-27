package com.stream.locomotion.ui.guide

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stream.locomotion.R

class ProgramAdapter(
    private val programs: List<ProgramUi>,
    private val onProgramClick: (ProgramUi) -> Unit
) : RecyclerView.Adapter<ProgramAdapter.ProgramViewHolder>() {

    class ProgramViewHolder(val root: LinearLayout) : RecyclerView.ViewHolder(root) {
        val title: TextView = root.findViewById(R.id.program_title)
        val time: TextView = root.findViewById(R.id.program_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_program, parent, false) as LinearLayout
        return ProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.title.text = program.title
        holder.time.text = program.timeLabel
        holder.root.setOnClickListener { onProgramClick(program) }
    }

    override fun getItemCount(): Int = programs.size
}
