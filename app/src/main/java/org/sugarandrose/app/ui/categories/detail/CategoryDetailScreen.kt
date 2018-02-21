package org.sugarandrose.app.ui.categories.detail

import android.databinding.Bindable
import android.os.Bundle
import android.view.MenuItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.model.remote.Category
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.data.remote.TOTAL_PAGES_HEADER
import org.sugarandrose.app.databinding.ActivityCategorydetailBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.displayitems.DisplayItemAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import org.sugarandrose.app.util.PaginationScrollListener
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface CategoryDetailMvvm {

    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        @get:Bindable
        var refreshing: Boolean

        fun onResume()
        fun onRefresh()
        fun loadNextPage()

        val adapter: DisplayItemAdapter

        @get:Bindable
        var category: Category
    }
}


class CategoryDetailActivity : BaseActivity<ActivityCategorydetailBinding, CategoryDetailMvvm.ViewModel>(), CategoryDetailMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityComponent.inject(this)
        setAndBindContentView(savedInstanceState, R.layout.activity_categorydetail)

        setSupportActionBar(binding.includeToolbar?.toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.recyclerView.itemAnimator = SlideInUpAnimator()
        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener() {
            override fun loadMoreItems() = viewModel.loadNextPage()
            override fun isLoading() = viewModel.refreshing
        })

        viewModel.category = intent.getParcelableExtra(Navigator.EXTRA_ARG)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

}


@PerActivity
class CategoryDetailViewModel @Inject
constructor(private val api: SugarAndRoseApi) : BaseViewModel<CategoryDetailMvvm.View>(), CategoryDetailMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)
    override var category: Category by NotifyPropertyChangedDelegate(Category(), BR.category)

    override val adapter = DisplayItemAdapter()
    private var currentPage = 0
    private var maximumNumberOfPages = 10

    override fun onResume() {
        if (adapter.isEmpty) onRefresh()
    }

    override fun onRefresh() {
        adapter.clear()
        currentPage = 0
        loadNextPage()
    }

    override fun loadNextPage() {
        if (currentPage >= maximumNumberOfPages) return
        currentPage++
        loadPage().subscribe().let { disposable.add(it) }
    }

    private fun loadPage() = api.getPostsForCategory(category.id, currentPage)
            .doOnSuccess { it.response()?.headers()?.values(TOTAL_PAGES_HEADER)?.firstOrNull()?.toInt()?.let { maximumNumberOfPages = it } }
            .map { it.response()?.body() }
            .flattenAsFlowable { it }
            .flatMapSingle { post ->
                if (post.featured_media != 0L) api.getMedia(post.featured_media).map { LocalPost(post, it) }
                else Single.just(LocalPost(post))
            }
            .toList()
            .map { it.sortedBy { it.date } }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { refreshing = true }
            .doOnSuccess(adapter::add)
            .doOnError(Timber::e)
            .doOnEvent { _, _ -> refreshing = false }
}