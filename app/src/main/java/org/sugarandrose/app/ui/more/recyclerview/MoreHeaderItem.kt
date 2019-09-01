package org.sugarandrose.app.ui.more.recyclerview

import android.view.View
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalMoreHeader
import org.sugarandrose.app.databinding.ItemMoreHeaderBinding
import org.sugarandrose.app.ui.base.BaseFragmentViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.NoOpViewModel

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface MoreHeaderItemView : MvvmView {
    fun update(item: LocalMoreHeader)
}

class MoreHeaderItemViewHolder(itemView: View) : BaseFragmentViewHolder<ItemMoreHeaderBinding, NoOpViewModel<MoreHeaderItemView>>(itemView), MoreHeaderItemView {
    override val fragmentContainerId get() = R.id.container

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }

    override fun update(item: LocalMoreHeader) {
        binding.tvTitle.setText(item.text)
        executePendingBindings()
    }
}
