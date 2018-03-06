package org.sugarandrose.app.ui.categories.recyclerview

import android.view.View
import org.sugarandrose.app.data.model.local.LocalCategory
import org.sugarandrose.app.databinding.ItemCategoryBinding
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseActivityViewHolder
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.detail.CategoryDetailActivity
import org.sugarandrose.app.ui.categories.detail.SubCategoryActivity
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface CategoryItemMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun update(category: LocalCategory, closeActivityOnClick: Boolean)
        fun onClick()

        var category: LocalCategory
    }
}

class CategoryItemViewHolder(itemView: View) : BaseActivityViewHolder<ItemCategoryBinding, CategoryItemMvvm.ViewModel>(itemView), CategoryItemMvvm.View {

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}

@PerViewHolder
class CategoryItemViewModel @Inject
constructor(private val navigator: Navigator) : BaseViewModel<CategoryItemMvvm.View>(), CategoryItemMvvm.ViewModel {
    override lateinit var category: LocalCategory
    private var closeActivityOnClick = false

    override fun update(category: LocalCategory, closeActivityOnClick: Boolean) {
        this.category = category
        this.closeActivityOnClick = closeActivityOnClick
        notifyChange()
    }

    override fun onClick() {
        val clazz = if (category.children.isNotEmpty()) SubCategoryActivity::class.java
        else CategoryDetailActivity::class.java
        navigator.startActivity(clazz, { putExtra(Navigator.EXTRA_ARG, category) })
        if (closeActivityOnClick) navigator.finishActivity()
    }
}