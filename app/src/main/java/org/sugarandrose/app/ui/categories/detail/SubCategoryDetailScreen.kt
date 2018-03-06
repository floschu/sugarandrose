package org.sugarandrose.app.ui.categories.detail

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.local.LocalCategory
import org.sugarandrose.app.databinding.ActivitySubcategoryBinding
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.BaseActivity
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.recyclerview.CategoriesAdapter
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */


interface SubCategoryMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        var category: LocalCategory
        val adapter: CategoriesAdapter
    }
}


class SubCategoryActivity : BaseActivity<ActivitySubcategoryBinding, SubCategoryMvvm.ViewModel>(), SubCategoryMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAndBindContentView(savedInstanceState, R.layout.activity_subcategory)

        setSupportActionBar(binding.includeToolbar?.toolbar)
        supportActionBar?.let {
            it.setDisplayShowHomeEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)
        }

        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.itemAnimator = SlideInUpAnimator()

        viewModel.category = intent.getParcelableExtra(Navigator.EXTRA_ARG)
        viewModel.adapter.data = viewModel.category.children
        viewModel.adapter.closeActivityOnClick = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}


@PerActivity
class SubCategoryViewModel @Inject
constructor() : BaseViewModel<SubCategoryMvvm.View>(), SubCategoryMvvm.ViewModel {
    override val adapter: CategoriesAdapter = CategoriesAdapter()
    override lateinit var category: LocalCategory
}