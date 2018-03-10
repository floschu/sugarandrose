package org.sugarandrose.app.ui.favorited

import android.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.databinding.FragmentFavoritedBinding
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.displayitems.DisplayItemAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface FavoritedMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        val adapter: DisplayItemAdapter
        @get:Bindable
        var hasMedia: Boolean
    }
}


class FavoritedFragment : BaseFragment<FragmentFavoritedBinding, FavoritedMvvm.ViewModel>(), FavoritedMvvm.View {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_favorited)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.itemAnimator = SlideInUpAnimator()
    }

}


@PerFragment
class FavoritedViewModel @Inject
constructor(@FragmentDisposable private val disposable: CompositeDisposable,
            private val favoritedRepo: FavoritedRepo
) : BaseViewModel<FavoritedMvvm.View>(), FavoritedMvvm.ViewModel {
    override var hasMedia: Boolean by NotifyPropertyChangedDelegate(false, BR.hasMedia)

    override val adapter: DisplayItemAdapter = DisplayItemAdapter()

    override fun attachView(view: FavoritedMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)

        favoritedRepo.allDisplayItems
                .map { it.sortedByDescending { it.date } }
                .subscribe({
                    adapter.clear()
                    adapter.add(it)
                    hasMedia = it.isNotEmpty()
                }, Timber::e).let { disposable.add(it) }
    }

}