package org.sugarandrose.app.ui.news.recyclerview

import android.databinding.Bindable
import android.view.View
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.databinding.ItemMediaBinding
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseFragmentViewHolder
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.ShareManager
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface MediaItemMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun update(media: LocalMedia)
        fun onFavoriteClick()
        fun onShareClick()

        var media: LocalMedia
        @get:Bindable
        var favorited: Boolean
    }
}

class MediaItemViewHolder(itemView: View) : BaseFragmentViewHolder<ItemMediaBinding, MediaItemMvvm.ViewModel>(itemView), MediaItemMvvm.View {
    override val fragmentContainerId get() = R.id.container

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}

@PerViewHolder
class MediaItemViewModel @Inject
constructor(private val favoritedRepo: FavoritedRepo, private val shareManager: ShareManager) : BaseViewModel<MediaItemMvvm.View>(), MediaItemMvvm.ViewModel {
    override lateinit var media: LocalMedia
    override var favorited: Boolean = false

    override fun update(media: LocalMedia) {
        this.media = media
        this.favorited = favoritedRepo.isContained(this.media)
        notifyChange()
    }

    override fun onFavoriteClick() {
        if (favorited) favoritedRepo.deleteItem(media)
        else favoritedRepo.addItem(media)
        favorited = !favorited
        notifyPropertyChanged(BR.favorited)
    }

    override fun onShareClick() = shareManager.share(media)
}
