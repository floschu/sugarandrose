package org.sugarandrose.app.ui.news.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import io.reactivex.subjects.PublishSubject
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
constructor() : RecyclerView.Adapter<PostItemViewHolder>() {
    private val data = ArrayList<LocalPost>()

    fun add(posts: List<LocalPost>) {
        val oldSize = data.size
        data.addAll(posts)
        notifyItemRangeInserted(oldSize, data.size - oldSize)
    }

    fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount() = data.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder = Utils.createViewHolder(parent, R.layout.item_post, ::PostItemViewHolder)
    override fun onBindViewHolder(viewHolder: PostItemViewHolder, position: Int) = viewHolder.viewModel.update(data[position])
}