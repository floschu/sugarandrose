package org.sugarandrose.app.ui.news.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalDisplayItem
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.util.Utils
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerFragment
class PostAdapter @Inject
constructor() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_POST = 0
    private val TYPE_MEDIA = 1

    private val data = ArrayList<LocalDisplayItem>()
    val isEmpty:Boolean get()=data.isEmpty()

    fun add(posts: List<LocalDisplayItem>) {
        val oldSize = data.size
        data.addAll(posts)
        notifyItemRangeInserted(oldSize, data.size - oldSize)
    }

    fun delete(item: LocalDisplayItem) {
        val index = data.indexOf(item)
        data.removeAt(index)
        notifyItemRemoved(index)
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = when (data[position]) {
        is LocalPost -> TYPE_POST
        else -> TYPE_MEDIA
    }

    override fun getItemCount() = data.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_POST -> Utils.createViewHolder(parent, R.layout.item_post, ::PostItemViewHolder)
        else -> Utils.createViewHolder(parent, R.layout.item_media, ::MediaItemViewHolder)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is PostItemViewHolder -> viewHolder.viewModel.update(data[position] as LocalPost)
            is MediaItemViewHolder -> viewHolder.viewModel.update(data[position] as LocalMedia)
        }
    }
}