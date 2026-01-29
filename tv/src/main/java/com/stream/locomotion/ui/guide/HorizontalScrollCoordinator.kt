package com.stream.locomotion.ui.guide

import androidx.recyclerview.widget.RecyclerView

class HorizontalScrollCoordinator {
    private val rows = mutableListOf<RecyclerView>()
    private var isSyncing = false

    fun register(recyclerView: RecyclerView) {
        if (rows.contains(recyclerView)) return
        rows.add(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                if (isSyncing || dx == 0) return
                isSyncing = true
                rows.filter { it !== rv }.forEach { it.scrollBy(dx, 0) }
                isSyncing = false
            }
        })
    }

    fun scrollToPosition(position: Int) {
        isSyncing = true
        rows.forEach { it.scrollToPosition(position) }
        isSyncing = false
    }
}
