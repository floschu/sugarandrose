package org.sugarandrose.app.util.pagination

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

abstract class PaginationScrollListener(
    private val visibleThreshold: Int
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount

        if (!isLoading() && (lastVisibleItemPosition + visibleThreshold > totalItemCount)) loadMoreItems()
    }

    protected abstract fun loadMoreItems()
    abstract fun isLoading(): Boolean
}