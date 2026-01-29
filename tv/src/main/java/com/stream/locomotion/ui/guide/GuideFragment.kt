package com.stream.locomotion.ui.guide

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stream.locomotion.R
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GuideFragment : Fragment() {

    private val coordinator = HorizontalScrollCoordinator()
    private lateinit var viewModel: GuideViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_guide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[GuideViewModel::class.java]

        val offlineBanner = view.findViewById<View>(R.id.offline_banner)
        val guideTime = view.findViewById<TextView>(R.id.guide_time)
        val nowButton = view.findViewById<TextView>(R.id.guide_now)
        val prevButton = view.findViewById<TextView>(R.id.guide_prev)
        val nextButton = view.findViewById<TextView>(R.id.guide_next)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isOnline.collect { isOnline ->
                    offlineBanner.visibility = if (isOnline) View.GONE else View.VISIBLE
                }
            }
        }

        val timeAxis = view.findViewById<RecyclerView>(R.id.time_axis)
        timeAxis.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        val timeAxisAdapter = TimeAxisAdapter()
        timeAxis.adapter = timeAxisAdapter
        coordinator.register(timeAxis)

        val guideRows = view.findViewById<RecyclerView>(R.id.guide_rows)
        guideRows.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        val guideRowAdapter = GuideRowAdapter(coordinator) { row, program ->
            val streamUrl = resources.getStringArray(R.array.stream_urls).firstOrNull()
            if (streamUrl != null) {
                val title = program?.title ?: row.channel.name
                startActivity(android.content.Intent(requireContext(), com.stream.locomotion.PlaybackActivity::class.java).apply {
                    putExtra(com.stream.locomotion.PlaybackActivity.EXTRA_STREAM_URL, streamUrl)
                    putExtra(com.stream.locomotion.PlaybackActivity.EXTRA_TITLE, title)
                })
            }
        }
        guideRows.adapter = guideRowAdapter

        var currentIndex = 0
        var lastIndex = 0
        var hasAutoScrolled = false
        var latestState: GuideUiState? = null
        nowButton.setOnClickListener {
            coordinator.scrollToPosition(currentIndex)
        }
        prevButton.setOnClickListener {
            if (lastIndex == 0) return@setOnClickListener
            val target = (currentIndex - 1).coerceAtLeast(0)
            currentIndex = target
            coordinator.scrollToPosition(target)
        }
        nextButton.setOnClickListener {
            if (lastIndex == 0) return@setOnClickListener
            val target = (currentIndex + 1).coerceAtMost(lastIndex)
            currentIndex = target
            coordinator.scrollToPosition(target)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    latestState = state
                    guideTime.text = state.currentTimeLabel
                    timeAxisAdapter.submit(state.timeSlots)
                    guideRowAdapter.submit(state.rows)
                    currentIndex = state.currentIndex
                    lastIndex = (state.timeSlots.size - 1).coerceAtLeast(0)
                    if (!hasAutoScrolled && state.timeSlots.isNotEmpty()) {
                        hasAutoScrolled = true
                        val target = (state.currentIndex - 1).coerceAtLeast(0)
                        coordinator.scrollToPosition(target)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (true) {
                    val now = System.currentTimeMillis()
                    guideTime.text = viewModel.uiState.value.currentTimeLabel.takeIf { it.isNotBlank() }
                        ?: java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date(now))

                    val state = latestState
                    if (state != null && state.rows.isNotEmpty()) {
                        val programs = state.rows.first().programs
                        val newIndex = programs.indexOfFirst { now in it.startTime until it.endTime }
                            .let { if (it >= 0) it else 0 }
                        if (newIndex != currentIndex) {
                            currentIndex = newIndex
                            val target = (currentIndex - 1).coerceAtLeast(0)
                            coordinator.scrollToPosition(target)
                        }

                        for (i in 0 until guideRows.childCount) {
                            val rowHolder = guideRows.getChildViewHolder(guideRows.getChildAt(i))
                            val programRow = rowHolder.itemView.findViewById<RecyclerView>(R.id.program_row)
                            val adapter = programRow.adapter as? ProgramAdapter
                            adapter?.updateVisible(programRow, now)
                        }
                    }

                    val delayMs = 60_000L - (now % 60_000L)
                    delay(delayMs)
                }
            }
        }
    }
}
