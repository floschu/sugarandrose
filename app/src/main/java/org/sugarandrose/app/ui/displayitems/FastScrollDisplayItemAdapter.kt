package org.sugarandrose.app.ui.displayitems

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import org.sugarandrose.app.data.model.LocalDisplayHeader
import org.sugarandrose.app.data.model.LocalDisplayItem
import org.sugarandrose.app.data.model.LocalRose
import org.sugarandrose.app.ui.displayitems.DisplayItemAdapter

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */
class FastScrollDisplayItemAdapter : DisplayItemAdapter(), FastScrollRecyclerView.SectionedAdapter {
    override fun getSectionName(position: Int): String = data[position].name[0].toString()

    fun addAllWithHeaders(items: List<LocalDisplayItem>) {
        val result = ArrayList<LocalDisplayItem>()
        items.groupBy { it.name[0].toString() }.toSortedMap().forEach {
            result.add(LocalDisplayHeader(it.key))
            result.addAll(it.value)
        }

        super.clear()
        super.add(result)
    }
}