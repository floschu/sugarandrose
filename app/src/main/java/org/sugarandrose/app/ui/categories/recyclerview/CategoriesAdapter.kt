package org.sugarandrose.app.ui.categories.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.remote.Category
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.util.NotifyDatasetChangedDelegate
import org.sugarandrose.app.util.Utils
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */
@PerFragment
class CategoriesAdapter @Inject
constructor() : RecyclerView.Adapter<CategoryItemViewHolder>() {
    var data by NotifyDatasetChangedDelegate<List<Category>>(emptyList())

    override fun getItemCount() = data.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder = Utils.createViewHolder(parent, R.layout.item_category, ::CategoryItemViewHolder)
    override fun onBindViewHolder(viewHolder: CategoryItemViewHolder, position: Int) = viewHolder.viewModel.update(data[position])
}