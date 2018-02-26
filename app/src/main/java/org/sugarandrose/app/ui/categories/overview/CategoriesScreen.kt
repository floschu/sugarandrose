package org.sugarandrose.app.ui.categories.overview

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.rxkotlin.addTo
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentCategoriesBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.categories.CategoriesCacheManager
import org.sugarandrose.app.ui.categories.overview.recyclerview.CategoriesAdapter
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface CategoriesMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        val adapter: CategoriesAdapter
    }
}


class CategoriesFragment : BaseFragment<FragmentCategoriesBinding, CategoriesMvvm.ViewModel>(), CategoriesMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_categories)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerView.itemAnimator = SlideInUpAnimator()
    }
}


@PerFragment
class CategoriesViewModel @Inject
constructor(private val categoriesCacheManager: CategoriesCacheManager,
            override val adapter: CategoriesAdapter
) : BaseViewModel<CategoriesMvvm.View>(), CategoriesMvvm.ViewModel {

    override fun attachView(view: CategoriesMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)

        disposable.addAll(
                categoriesCacheManager.dataSubject.subscribe({ adapter.data = it }, Timber::e),
                categoriesCacheManager.reloadData()
        )
    }
}