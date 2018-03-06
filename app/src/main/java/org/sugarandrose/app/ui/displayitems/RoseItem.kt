package org.sugarandrose.app.ui.displayitems

import android.databinding.Bindable
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import org.sugarandrose.app.BR
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.LocalRose
import org.sugarandrose.app.databinding.ItemRoseBinding
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseActivityViewHolder
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.manager.EventLogManager
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.manager.ShareManager
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface RoseItemMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun update(rose: LocalRose)
        fun onFavoriteClick()
        fun onShareClick()

        var rose: LocalRose
        @get:Bindable
        var favorited: Boolean
        @get:Bindable
        var loading: Boolean
    }
}

class RoseItemViewHolder(itemView: View) : BaseActivityViewHolder<ItemRoseBinding, RoseItemMvvm.ViewModel>(itemView), RoseItemMvvm.View {

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}

@PerViewHolder
class RoseItemViewModel @Inject
constructor(@ActivityDisposable private val disposable: CompositeDisposable,
            private val favoritedRepo: FavoritedRepo,
            private val shareManager: ShareManager,
            private val eventLogManager: EventLogManager
) : BaseViewModel<RoseItemMvvm.View>(), RoseItemMvvm.ViewModel {
    override lateinit var rose: LocalRose
    override var favorited: Boolean = false
    override var loading by NotifyPropertyChangedDelegate(false, BR.loading)

    override fun update(rose: LocalRose) {
        this.rose = rose
        this.favorited = favoritedRepo.isContained(this.rose)
        notifyChange()
    }

    override fun onFavoriteClick() {
        if (favorited) favoritedRepo.deleteItem(rose)
        else favoritedRepo.addItem(rose)
        favorited = !favorited
        notifyPropertyChanged(BR.favorited)

        if (favorited) eventLogManager.logFavorite(rose)
    }

    override fun onShareClick() {
        shareManager.shareRose(rose)
                .doOnSubscribe { loading = true }
                .doOnError(Timber::e)
                .doOnEvent { loading = false }
                .subscribe().let { disposable.add(it) }
    }
}