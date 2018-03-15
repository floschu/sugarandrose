package org.sugarandrose.app.ui.displayitems.recyclerview

import android.databinding.Bindable
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import org.sugarandrose.app.BR
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.databinding.ItemMediaBinding
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseActivityViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.manager.EventLogManager
import org.sugarandrose.app.util.manager.ShareManager
import timber.log.Timber
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
        @get:Bindable
        var loading: Boolean
    }
}

class MediaItemViewHolder(itemView: View) : BaseActivityViewHolder<ItemMediaBinding, MediaItemMvvm.ViewModel>(itemView), MediaItemMvvm.View {

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}

@PerViewHolder
class MediaItemViewModel @Inject
constructor(@ActivityDisposable private val disposable: CompositeDisposable,
            private val favoritedRepo: FavoritedRepo,
            private val shareManager: ShareManager,
            private val eventLogManager: EventLogManager
) : BaseViewModel<MediaItemMvvm.View>(), MediaItemMvvm.ViewModel {
    override lateinit var media: LocalMedia
    override var favorited: Boolean = false
    override var loading by NotifyPropertyChangedDelegate(false, BR.loading)

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

        if (favorited) eventLogManager.logFavorite(media)
    }


    override fun onShareClick() {
        shareManager.shareMedia(media)
                .doOnSubscribe { loading = true }
                .doOnError(Timber::e)
                .doOnEvent { loading = false }
                .subscribe().let { disposable.add(it) }
    }
}
