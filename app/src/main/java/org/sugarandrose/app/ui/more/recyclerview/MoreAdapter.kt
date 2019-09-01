package org.sugarandrose.app.ui.more.recyclerview

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalMore
import org.sugarandrose.app.data.model.LocalMoreHeader
import org.sugarandrose.app.data.model.LocalMoreItem
import org.sugarandrose.app.data.model.LocalMorePage
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.util.NotifyDatasetChangedDelegate
import org.sugarandrose.app.util.Utils
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerFragment
class MoreAdapter @Inject
constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        val TYPE_HEADER = 0
        val TYPE_ITEM = 1
        val TYPE_ITEM_GRID = 2
    }

    var data by NotifyDatasetChangedDelegate(emptyList<Pair<Int, LocalMore>>())

    override fun getItemCount() = data.size
    override fun getItemViewType(position: Int): Int = data[position].first

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_ITEM -> Utils.createViewHolder(parent, R.layout.item_more, ::MoreItemViewHolder)
        TYPE_ITEM_GRID -> Utils.createViewHolder(parent, R.layout.item_more_grid, ::MorePageItemViewHolder)
        else -> Utils.createViewHolder(parent, R.layout.item_more_header, ::MoreHeaderItemViewHolder)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is MoreItemViewHolder -> viewHolder.viewModel.update(data[position].second as LocalMoreItem)
            is MoreHeaderItemViewHolder -> viewHolder.update(data[position].second as LocalMoreHeader)
            is MorePageItemViewHolder -> viewHolder.viewModel.update(data[position].second as LocalMorePage)
        }
    }
}