package org.sugarandrose.app.ui.news.recyclerview

import android.net.Uri
import android.view.View
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.databinding.ItemPostBinding
import org.sugarandrose.app.injection.scopes.PerViewHolder
import org.sugarandrose.app.ui.base.BaseFragmentViewHolder
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.post.PostActivity
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

        var post: LocalPost
    }
}

class PostItemViewHolder(itemView: View) : BaseFragmentViewHolder<ItemPostBinding, PostItemMvvm.ViewModel>(itemView), PostItemMvvm.View {
    override val fragmentContainerId get() = R.id.container

    init {
        viewHolderComponent.inject(this)
        bindContentView(itemView)
    }
}

@PerViewHolder
class PostItemViewModel @Inject
constructor(private val navigator: Navigator) : BaseViewModel<PostItemMvvm.View>(), PostItemMvvm.ViewModel {
    override lateinit var post: LocalPost

    override fun update(post: LocalPost) {
        this.post = post
        notifyChange()
    }

    override fun onClick() = navigator.startActivity(PostActivity::class.java, {
        it.putExtra(PostActivity.EXTRA_TITLE, post.title)
        it.putExtra(PostActivity.EXTRA_URL, post.url)
    })
}
