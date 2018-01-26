package org.sugarandrose.app.ui.categories.overview.recyclerview

import android.view.View
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.remote.Category
import org.sugarandrose.app.databinding.ItemCategoryBinding
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseFragmentViewHolder
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.detail.CategoryDetailActivity
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface CategoryItemMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun update(category: Category)
        fun onClick()

        var category: Category
    }
}

class CategoryItemViewHolder(itemView: View) : BaseFragmentViewHolder<ItemCategoryBinding, CategoryItemMvvm.ViewModel>(itemView), CategoryItemMvvm.View {
    override val fragmentContainerId get() = R.id.container

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}

@PerViewHolder
class CategoryItemViewModel @Inject
constructor(private val navigator: Navigator) : BaseViewModel<CategoryItemMvvm.View>(), CategoryItemMvvm.ViewModel {
    override lateinit var category: Category

    override fun update(category: Category) {
        this.category = category
        notifyChange()
    }

    override fun onClick() = navigator.startActivity(CategoryDetailActivity::class.java, { putExtra(Navigator.EXTRA_ARG, category) })

}