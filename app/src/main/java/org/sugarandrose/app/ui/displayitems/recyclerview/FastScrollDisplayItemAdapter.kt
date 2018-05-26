package org.sugarandrose.app.ui.displayitems.recyclerview

import android.support.v7.widget.RecyclerView
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import org.sugarandrose.app.R
import org.sugarandrose.app.SugarAndRoseApp
import org.sugarandrose.app.data.model.LocalDisplayHeader
import org.sugarandrose.app.data.model.LocalDisplayItem

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */
class FastScrollDisplayItemAdapter : DisplayItemAdapter(), FastScrollRecyclerView.SectionedAdapter, FastScrollRecyclerView.MeasurableAdapter {
    override fun getSectionName(position: Int): String = data[if (position == 0) 0 else position - 1].name[0].toString()

    fun addAllWithHeaders(items: List<LocalDisplayItem>) {
        val result = ArrayList<LocalDisplayItem>()
        items.groupBy { it.name[0].toString() }.toSortedMap().forEach {
            result.add(LocalDisplayHeader(it.key))
            result.addAll(it.value)
        }

        super.clear()
        super.add(result)
    }

    override fun getViewTypeHeight(recyclerView: RecyclerView?, viewType: Int): Int {
        val resources = SugarAndRoseApp.appComponent.resources()
        return when (viewType) {
            TYPE_POST -> (2 * resources.getDimensionPixelSize(R.dimen.card_margin_half)) + ((9 * resources.displayMetrics.widthPixels) / 16) + (2 * resources.getDimensionPixelSize(R.dimen.margin)) + resources.getDimensionPixelSize(R.dimen.title_one_line)
            TYPE_ROSE -> (2 * resources.getDimensionPixelSize(R.dimen.card_margin_half)) + resources.displayMetrics.widthPixels + (2 * resources.getDimensionPixelSize(R.dimen.margin)) + resources.getDimensionPixelSize(R.dimen.title_one_line)
            TYPE_HEADER -> resources.getDimensionPixelSize(R.dimen.margin) + resources.getDimensionPixelSize(R.dimen.title_one_line) + (2 * resources.getDimensionPixelSize(R.dimen.padding))
            else -> resources.getDimensionPixelSize(R.dimen.normal_fab_size) + (2 * resources.getDimensionPixelSize(R.dimen.padding))
        }
    }
}