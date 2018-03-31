package org.sugarandrose.app.ui.displayitems.recyclerview

import android.databinding.Bindable
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import org.sugarandrose.app.BR
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.databinding.ItemPostBinding
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseActivityViewHolder
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.post.PostActivity
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.manager.EventLogManager
import org.sugarandrose.app.util.manager.ShareManager
import org.sugarandrose.app.util.manager.TutorialManager
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface PostItemMvvm {
    interface View : MvvmView {
        val favoriteView: android.view.View
        val shareView: android.view.View
    }

    interface ViewModel : MvvmViewModel<View> {
        fun update(post: LocalPost)
        fun onClick()
        fun onFavoriteClick()
        fun onShareClick()

        var post: LocalPost
        @get:Bindable
        var favorited: Boolean
        @get:Bindable
        var loading: Boolean
    }
}

class PostItemViewHolder(itemView: View) : BaseActivityViewHolder<ItemPostBinding, PostItemMvvm.ViewModel>(itemView), PostItemMvvm.View {

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }

    override val favoriteView: View get() = binding.btnFav
    override val shareView: View get() = binding.btnShare
}

@PerViewHolder
class PostItemViewModel @Inject
constructor(@ActivityDisposable private val disposable: CompositeDisposable,
            private val navigator: Navigator,
            private val favoritedRepo: FavoritedRepo,
            private val shareManager: ShareManager,
            private val eventLogManager: EventLogManager,
            private val tutorialManager: TutorialManager
) : BaseViewModel<PostItemMvvm.View>(), PostItemMvvm.ViewModel {
    override lateinit var post: LocalPost
    override var favorited: Boolean = false
    override var loading by NotifyPropertyChangedDelegate(false, BR.loading)

    override fun update(post: LocalPost) {
        this.post = post
        this.favorited = favoritedRepo.isContained(this.post)
        notifyChange()

        view?.let {
            tutorialManager.favorite(it.favoriteView) {
                tutorialManager.share(it.shareView)
            }
        }
    }

    override fun onClick() {
        eventLogManager.logOpen(post)
        navigator.startActivity(PostActivity::class.java, { putExtra(Navigator.EXTRA_ARG, post.id) })
    }

    override fun onFavoriteClick() {
        if (favorited) favoritedRepo.deleteItem(post)
        else favoritedRepo.addItem(post)
        favorited = !favorited
        notifyPropertyChanged(BR.favorited)

        if (favorited) {
            eventLogManager.logFavorite(post)
            view?.let { tutorialManager.unfavorite(it.favoriteView) }
        }
    }

    override fun onShareClick() {
        shareManager.sharePost(post)
                .doOnSubscribe { loading = true }
                .doOnError(Timber::e)
                .doOnEvent { loading = false }
                .subscribe().let { disposable.add(it) }
    }
}
