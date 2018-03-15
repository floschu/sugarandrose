package org.sugarandrose.app.ui.roses

import android.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.databinding.FragmentRosesBinding
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.displayitems.recyclerview.FastScrollDisplayItemAdapter
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface RosesMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        @get:Bindable
        var refreshing: Boolean

        val adapter: FastScrollDisplayItemAdapter
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
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(true, BR.refreshing)

    override val adapter = FastScrollDisplayItemAdapter()

    override fun attachView(view: RosesMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        rosesCacheManager.dataSubject
                .doOnNext { if (!it.isEmpty()) refreshing = false }
                .subscribe(adapter::addAllWithHeaders, Timber::e).addTo(disposable)
    }
}