package org.sugarandrose.app.ui.displayitems.recyclerview

import android.view.View
import org.sugarandrose.app.databinding.ItemLoadingBinding
import org.sugarandrose.app.ui.base.BaseActivityViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.NoOpViewModel

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class LoadingViewHolder(itemView: View) : BaseActivityViewHolder<ItemLoadingBinding, NoOpViewModel<MvvmView>>(itemView), MvvmView {

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }

    fun update(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        executePendingBindings()
    }
}
