package com.stream.locomotion.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stream.locomotion.R

class GuideFragment : Fragment() {

    private val coordinator = HorizontalScrollCoordinator()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_guide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timeAxis = view.findViewById<RecyclerView>(R.id.time_axis)
        timeAxis.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        timeAxis.adapter = TimeAxisAdapter(buildTimeSlots())
        coordinator.register(timeAxis)

        val guideRows = view.findViewById<RecyclerView>(R.id.guide_rows)
        guideRows.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        guideRows.adapter = GuideRowAdapter(buildRows(), coordinator)
    }

    private fun buildTimeSlots(): List<String> {
        return listOf("12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM")
    }

    private fun buildRows(): List<GuideRowUi> {
        val channel = ChannelUi(
            id = "locomotion",
            number = "1",
            name = "Locomotion",
            subtitle = "Network"
        )
        val programs = listOf(
            ProgramUi("p1", "Chica Marioneta J", "12:00 PM"),
            ProgramUi("p2", "Escaflowne", "12:30 PM"),
            ProgramUi("p3", "El Baron Rojo", "01:00 PM"),
            ProgramUi("p4", "Lupin III", "01:30 PM"),
            ProgramUi("p5", "Lost Universe", "02:00 PM"),
            ProgramUi("p6", "Evangelion", "02:30 PM")
        )
        return listOf(
            GuideRowUi(channel, programs),
            GuideRowUi(channel.copy(number = "2", name = "AZTV"), programs),
            GuideRowUi(channel.copy(number = "3", name = "Anime XTV"), programs)
        )
    }
}
