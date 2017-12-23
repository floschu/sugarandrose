package org.sugarandrose.app.util

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

abstract class PaginationScrollListener : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val visibleItemCount = recyclerView.layoutManager.childCount
        val totalItemCount = recyclerView.layoutManager.itemCount
        val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (!isLoading() && ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount - 2)) loadMoreItems()
    }

    protected abstract fun loadMoreItems()
    abstract fun isLoading(): Boolean
}