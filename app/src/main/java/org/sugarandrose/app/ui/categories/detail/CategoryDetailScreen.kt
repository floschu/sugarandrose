package org.sugarandrose.app.ui.categories.detail

import android.databinding.Bindable
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalCategory
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.data.remote.TOTAL_PAGES_HEADER
import org.sugarandrose.app.databinding.ActivityCategoryDetailBinding
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.recyclerview.CategoriesAdapter
import org.sugarandrose.app.ui.displayitems.DisplayItemAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */


interface CategoryDetailMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        var category: LocalCategory
        val adapterCategories: CategoriesAdapter
        val adapterItems: DisplayItemAdapter

        fun loadNextPage()

        @get:Bindable
        var refreshing: Boolean
    }
}


class CategoryDetailActivity : BaseActivity<ActivityCategoryDetailBinding, CategoryDetailMvvm.ViewModel>(), CategoryDetailMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, R.layout.activity_category_detail)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.recyclerViewCategories.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerViewCategories.itemAnimator = SlideInUpAnimator()

        binding.recyclerViewItems.itemAnimator = SlideInUpAnimator()

        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = binding.scrollView.getChildAt(binding.scrollView.childCount - 1)
            val diff = view.bottom - (binding.scrollView.height + binding.scrollView.scrollY)

            if (diff == 0 && !viewModel.refreshing) {
                viewModel.loadNextPage()
            }
        }

        viewModel.category = intent.getParcelableExtra(Navigator.EXTRA_ARG)
        viewModel.adapterCategories.data = viewModel.category.children
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}


@PerActivity
class CategoryDetailViewModel @Inject
constructor(@ActivityDisposable private val disposable: CompositeDisposable,
            private val api: SugarAndRoseApi
) : BaseViewModel<CategoryDetailMvvm.View>(), CategoryDetailMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)

    override val adapterCategories: CategoriesAdapter = CategoriesAdapter()
    override val adapterItems: DisplayItemAdapter = DisplayItemAdapter()

    override var category: LocalCategory = LocalCategory()
        set(value) {
            field = value
            loadNextPage()
        }

    private var currentPage = 0
    private var maximumNumberOfPages = 10


    override fun loadNextPage() {
        if (currentPage >= maximumNumberOfPages) return
        currentPage++
        loadPage().subscribe().addTo(disposable)
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
            .map { it.sortedByDescending { it.date } }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { refreshing = true }
            .doOnSuccess(adapterItems::add)
            .doOnError(Timber::e)
            .doOnEvent { _, _ -> refreshing = false }
}