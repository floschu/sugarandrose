package org.sugarandrose.app.ui.news.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.R
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
    private val TYPE_ITEM = 0
    private val TYPE_LOADING = 1
    private val data = ArrayList<LocalPost>()

    var isLoading = false
        set(value) {
            if (value) add(LocalPost())
            else remove(data.size - 1)
        }

    fun add(post: LocalPost) {
        data.add(post)
        notifyItemInserted(data.size - 1)
    }

    fun remove(post: LocalPost) = remove(data.indexOf(post))
    fun remove(index: Int) {
        if (index > -1) {
            data.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
    override fun getItemViewType(position: Int) = if (position == data.size - 1 && isLoading) TYPE_LOADING else TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_ITEM -> Utils.createViewHolder(parent, R.layout.item_post, ::PostItemViewHolder)
        else -> Utils.createViewHolder(parent, R.layout.item_loading, ::LoadingItemViewHolder)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is PostItemViewHolder -> viewHolder.viewModel.update(data[position])
        }
    }
}