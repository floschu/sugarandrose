package org.sugarandrose.app.ui.categories.detail

import android.databinding.Bindable
import android.os.Bundle
import android.view.MenuItem
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.remote.Category
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.databinding.ActivityCategorydetailBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.news.recyclerview.PostAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
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

        fun onRefresh()

//        val adapter: PostAdapter

        @get:Bindable
        var category: Category?
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
//        binding.recyclerView.addOnScrollListener(object : PaginationScrollListener() {
//            override fun loadMoreItems() = viewModel.loadNextPage()
//            override fun isLoading() = viewModel.refreshing
//        })

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
        viewModel.onRefresh()
    }

}


@PerActivity
class CategoryDetailViewModel @Inject
constructor(private val sugarAndRoseApi: SugarAndRoseApi) : BaseViewModel<CategoryDetailMvvm.View>(), CategoryDetailMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)
    override var category: Category? by NotifyPropertyChangedDelegate(null, BR.category)

    override fun onRefresh() {
        //todo
    }
}