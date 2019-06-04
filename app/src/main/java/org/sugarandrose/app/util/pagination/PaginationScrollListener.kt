package org.sugarandrose.app.util.pagination

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

abstract class PaginationScrollListener(
    private val visibleThreshold: Int
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager ?: return
        val lastVisibleItemPosition = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        val totalItemCount = layoutManager.itemCount

        if (!isLoading() && (lastVisibleItemPosition + visibleThreshold > totalItemCount)) loadMoreItems()
    }

    protected abstract fun loadMoreItems()
    abstract fun isLoading(): Boolean
}