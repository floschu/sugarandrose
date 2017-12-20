package org.sugarandrose.app.ui.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentTestBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface CategoriesMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View>
}


class CategoriesFragment : BaseFragment<FragmentTestBinding, CategoriesMvvm.ViewModel>(), CategoriesMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_categories)
    }

}


@PerFragment
class CategoriesViewModel @Inject
constructor() : BaseViewModel<CategoriesMvvm.View>(), CategoriesMvvm.ViewModel