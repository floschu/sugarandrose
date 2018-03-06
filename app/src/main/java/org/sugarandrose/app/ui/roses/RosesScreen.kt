package org.sugarandrose.app.ui.roses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.toObservable
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.R
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.databinding.FragmentRosesBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import javax.inject.Inject
import org.jsoup.Jsoup
import org.sugarandrose.app.data.model.LocalDisplayHeader
import org.sugarandrose.app.data.model.LocalDisplayItem
import org.sugarandrose.app.data.model.LocalRose
import org.sugarandrose.app.data.model.remote.Roses
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import timber.log.Timber


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface RosesMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        val adapter: RosesAdapter
    }
}


class RosesFragment : BaseFragment<FragmentRosesBinding, RosesMvvm.ViewModel>(), RosesMvvm.View {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_roses)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.itemAnimator = SlideInUpAnimator()
    }
}


@PerFragment
class RosesViewModel @Inject
constructor(@FragmentDisposable private val disposable: CompositeDisposable,
            private val rosesCacheManager: RosesCacheManager
) : BaseViewModel<RosesMvvm.View>(), RosesMvvm.ViewModel {
    override val adapter = RosesAdapter()

    override fun attachView(view: RosesMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        rosesCacheManager.dataSubject.subscribe({
            adapter.clear()
            adapter.add(it)
        }, Timber::e).addTo(disposable)
    }
}