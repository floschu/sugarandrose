package org.sugarandrose.app.ui.news.recyclerview

import android.databinding.Bindable
import android.view.View
import org.sugarandrose.app.BR
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.databinding.ItemPostBinding
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseActivityViewHolder
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.post.PostActivity
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.ShareManager
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface PostItemMvvm {
    interface View : MvvmView

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
}

@PerViewHolder
class PostItemViewModel @Inject
constructor(private val navigator: Navigator, private val favoritedRepo: FavoritedRepo, private val shareManager: ShareManager) : BaseViewModel<PostItemMvvm.View>(), PostItemMvvm.ViewModel {
    override lateinit var post: LocalPost
    override var favorited: Boolean = false
    override var loading by NotifyPropertyChangedDelegate(false, BR.loading)

    override fun update(post: LocalPost) {
        this.post = post
        this.favorited = favoritedRepo.isContained(this.post)
        notifyChange()
    }

    override fun onClick() = navigator.startActivity(PostActivity::class.java, { putExtra(Navigator.EXTRA_ARG, post.url) })

    override fun onFavoriteClick() {
        if (favorited) favoritedRepo.deleteItem(post)
        else favoritedRepo.addItem(post)
        favorited = !favorited
        notifyPropertyChanged(BR.favorited)
    }

    override fun onShareClick() {
        shareManager.sharePost(post)
                .doOnSubscribe { loading = true }
                .doOnError(Timber::e)
                .doOnEvent { loading = false }
                .subscribe().let { disposable.add(it) }
    }
}
