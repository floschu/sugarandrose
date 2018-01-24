package org.sugarandrose.app.ui.more.recyclerview

import android.view.View
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalMoreItem
import org.sugarandrose.app.databinding.ItemMoreBinding
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseFragmentViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface MoreItemMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun update(item: LocalMoreItem)
        fun onClick()

        var item: LocalMoreItem
    }
}

class MoreItemViewHolder(itemView: View) : BaseFragmentViewHolder<ItemMoreBinding, MoreItemMvvm.ViewModel>(itemView), MoreItemMvvm.View {
    override val fragmentContainerId get() = R.id.container

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}

@PerViewHolder
class MoreItemViewModel @Inject
constructor() : BaseViewModel<MoreItemMvvm.View>(), MoreItemMvvm.ViewModel {
    override lateinit var item: LocalMoreItem

    override fun update(item: LocalMoreItem) {
        this.item = item
        notifyChange()
    }

    override fun onClick() = item.action.invoke()

}
