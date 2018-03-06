package org.sugarandrose.app.ui.roses

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView
import org.sugarandrose.app.data.model.local.LocalDisplayHeader
import org.sugarandrose.app.data.model.local.LocalRose
import org.sugarandrose.app.ui.displayitems.DisplayItemAdapter

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */
class RosesAdapter : DisplayItemAdapter(), FastScrollRecyclerView.SectionedAdapter {

    override fun getSectionName(position: Int): String {
        val item = data[position]
        return when (item) {
            is LocalRose -> item.category
            is LocalDisplayHeader -> item.title
            else -> item.id.toString()
        }
    }
}