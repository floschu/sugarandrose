package org.sugarandrose.app.ui.displayitems.recyclerview

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.*
import org.sugarandrose.app.util.Utils


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

open class DisplayItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    protected val TYPE_HEADER = 0
    protected val TYPE_POST = 1
    protected val TYPE_MEDIA = 2
    protected val TYPE_ROSE = 3
    protected val TYPE_LOADING = 4

    protected val data = ArrayList<LocalDisplayItem>()
    val isEmpty: Boolean get() = data.isEmpty()
    var endOfPages = false
    var loading = true
    var displayFirstLoading = false

    init {
        this.setHasStableIds(true)
    }

    fun add(items: List<LocalDisplayItem>) {
        val oldSize = data.size
        data.addAll(items)
        notifyItemRangeInserted(oldSize + 1, items.count())
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun set(items: List<LocalDisplayItem>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long = if (position == data.size) 0 else data[position].id

    override fun getItemViewType(position: Int): Int =
            if (position == data.size) TYPE_LOADING else
                when (data[position]) {
                    is LocalPost -> TYPE_POST
                    is LocalRose -> TYPE_ROSE
                    is LocalDisplayHeader -> TYPE_HEADER
                    else -> TYPE_MEDIA
                }

    override fun getItemCount() = if (data.isEmpty() && !displayFirstLoading) 0
    else if (!loading) data.size
    else data.size + 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_POST -> Utils.createViewHolder(parent, R.layout.item_post, ::PostItemViewHolder)
        TYPE_ROSE -> Utils.createViewHolder(parent, R.layout.item_rose, ::RoseItemViewHolder)
        TYPE_HEADER -> Utils.createViewHolder(parent, R.layout.item_display_header, ::LocalDisplayHeaderViewHolder)
        TYPE_MEDIA -> Utils.createViewHolder(parent, R.layout.item_media, ::MediaItemViewHolder)
        else -> Utils.createViewHolder(parent, R.layout.item_loading, ::LoadingViewHolder)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is PostItemViewHolder -> viewHolder.viewModel.update(data[position] as LocalPost)
            is MediaItemViewHolder -> viewHolder.viewModel.update(data[position] as LocalMedia)
            is RoseItemViewHolder -> viewHolder.viewModel.update(data[position] as LocalRose)
            is LocalDisplayHeaderViewHolder -> viewHolder.update(data[position] as LocalDisplayHeader)
            is LoadingViewHolder -> viewHolder.update(!endOfPages)
        }
    }
}