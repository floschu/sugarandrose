package org.sugarandrose.app.util.pagination

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

abstract class PaginationScrollListener(private val visibleThreshold: Int) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val lastVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        val totalItemCount = recyclerView.layoutManager.itemCount

        if (!isLoading() && (lastVisibleItemPosition + visibleThreshold > totalItemCount)) loadMoreItems()
    }

    protected abstract fun loadMoreItems()
    abstract fun isLoading(): Boolean
}