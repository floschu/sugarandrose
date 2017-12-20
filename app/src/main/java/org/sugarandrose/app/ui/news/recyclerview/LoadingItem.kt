package org.sugarandrose.app.ui.news.recyclerview

import android.view.View
import org.sugarandrose.app.databinding.ItemLoadingBinding
import org.sugarandrose.app.ui.base.BaseActivityViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.NoOpViewModel

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface LoadingItemMvvm {
    interface View : MvvmView
}

class LoadingItemViewHolder(itemView: View) : BaseActivityViewHolder<ItemLoadingBinding, NoOpViewModel<LoadingItemMvvm.View>>(itemView), LoadingItemMvvm.View {

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}