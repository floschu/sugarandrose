package org.sugarandrose.app.ui.main.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.util.NotifyDatasetChangedDelegate
import org.sugarandrose.app.util.Utils
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class PostAdapter @Inject
constructor() : RecyclerView.Adapter<PostItemViewHolder>() {
    var data by NotifyDatasetChangedDelegate<List<LocalPost>>(emptyList())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Utils.createViewHolder(parent, R.layout.item_post, ::PostItemViewHolder)
    override fun getItemCount() = data.size
    override fun onBindViewHolder(viewHolder: PostItemViewHolder, position: Int) = viewHolder.viewModel.update(data[position])
}
