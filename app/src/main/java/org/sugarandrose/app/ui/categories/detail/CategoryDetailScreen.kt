package org.sugarandrose.app.ui.categories.detail

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalCategory
import org.sugarandrose.app.databinding.ActivityCategoryDetailBinding
import org.sugarandrose.app.injection.qualifier.ActivityDisposable
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.feedback.Snacker
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.recyclerview.CategoriesAdapter
import org.sugarandrose.app.ui.displayitems.PagedPostLoadingManager
import org.sugarandrose.app.ui.displayitems.recyclerview.DisplayItemAdapter
import org.sugarandrose.app.util.manager.ErrorManager
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

        binding.scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = binding.scrollView.getChildAt(binding.scrollView.childCount - 1)
            val diff = view.bottom - (binding.scrollView.height + binding.scrollView.scrollY)
            if (diff == 0 && !viewModel.adapterItems.loading) viewModel.loadNextPage()
        }

        viewModel.category = intent.getParcelableExtra(Navigator.EXTRA_ARG)
        viewModel.adapterCategories.data = viewModel.category.children
        viewModel.adapterItems.displayFirstLoading = true
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
            private val errorManager: ErrorManager,
            private val snacker: Snacker
) : BaseViewModel<CategoryDetailMvvm.View>(), CategoryDetailMvvm.ViewModel {

    override val adapterCategories: CategoriesAdapter = CategoriesAdapter()
    override val adapterItems: DisplayItemAdapter = DisplayItemAdapter()

    private val pagedPostLoadingManager = PagedPostLoadingManager()

    override var category: LocalCategory = LocalCategory()
        set(value) {
            field = value
            loadNextPage()
        }

    override fun loadNextPage() {
        pagedPostLoadingManager.loadCategoryPage(category)
                .doOnSubscribe { adapterItems.loading = true }
                .doOnEvent { _, _ -> adapterItems.loading = false }
                .doOnSuccess { adapterItems.endOfPages = it.isEmpty() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapterItems::add, { errorManager.showError(it, snacker::show) })
                .addTo(disposable)
    }
}