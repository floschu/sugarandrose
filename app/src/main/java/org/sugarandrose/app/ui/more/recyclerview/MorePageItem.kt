package org.sugarandrose.app.ui.more.recyclerview

import android.view.View
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalMorePage
import org.sugarandrose.app.databinding.ItemMoreGridBinding
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseFragmentViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.WebManager
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface MorePageItemMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun update(item: LocalMorePage)
        fun onClick()

        var item: LocalMorePage
    }
}

class MorePageItemViewHolder(itemView: View) : BaseFragmentViewHolder<ItemMoreGridBinding, MorePageItemMvvm.ViewModel>(itemView), MorePageItemMvvm.View {
    override val fragmentContainerId get() = R.id.container

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}

@PerViewHolder
class MorePageItemViewModel @Inject
constructor(private val webManager: WebManager) : BaseViewModel<MorePageItemMvvm.View>(), MorePageItemMvvm.ViewModel {
    override lateinit var item: LocalMorePage

    override fun update(item: LocalMorePage) {
        this.item = item
        notifyChange()
    }

    override fun onClick() {
        if (item.link.isNotEmpty()) webManager.open(item.link)
    }
}
