package org.sugarandrose.app.ui.displayitems

import android.view.View
import org.sugarandrose.app.data.model.local.LocalDisplayHeader
import org.sugarandrose.app.databinding.ItemDisplayHeaderBinding
import org.sugarandrose.app.ui.base.BaseActivityViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.NoOpViewModel

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */


class LocalDisplayHeaderViewHolder(itemView: View) : BaseActivityViewHolder<ItemDisplayHeaderBinding, NoOpViewModel<MvvmView>>(itemView), MvvmView {

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }

    fun update(item: LocalDisplayHeader) {
        binding.tvTitle.text = item.title
        executePendingBindings()
    }
}