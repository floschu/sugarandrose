package org.sugarandrose.app.ui.more.recyclerview

import android.support.annotation.StringRes
import android.view.View
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.ItemMoreHeaderBinding
import org.sugarandrose.app.ui.base.BaseFragmentViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.NoOpViewModel

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface MoreHeaderItemView : MvvmView {
    fun update(@StringRes title: Int)
}

class MoreHeaderItemViewHolder(itemView: View) : BaseFragmentViewHolder<ItemMoreHeaderBinding, NoOpViewModel<MoreHeaderItemView>>(itemView), MoreHeaderItemView {
    override val fragmentContainerId get() = R.id.container

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }

    override fun update(@StringRes title: Int) {
        binding.tvTitle.setText(title)
        executePendingBindings()
    }
}