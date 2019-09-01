package org.sugarandrose.app.ui.categories.recyclerview

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalCategory
import org.sugarandrose.app.util.NotifyDatasetChangedDelegate
import org.sugarandrose.app.util.Utils

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class CategoriesAdapter : RecyclerView.Adapter<CategoryItemViewHolder>() {
    var data by NotifyDatasetChangedDelegate<List<LocalCategory>>(emptyList())

    override fun getItemCount() = data.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryItemViewHolder = Utils.createViewHolder(parent, R.layout.item_category, ::CategoryItemViewHolder)
    override fun onBindViewHolder(viewHolder: CategoryItemViewHolder, position: Int) = viewHolder.viewModel.update(data[position])
}