package org.sugarandrose.app.ui.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.android.schedulers.AndroidSchedulers
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.databinding.FragmentCalendarBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.news.recyclerview.PostAdapter
import org.threeten.bp.*
import org.threeten.bp.format.DateTimeFormatter
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface CalendarMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        fun init()

        val dateCallback: (LocalDate) -> Unit
        val adapter: PostAdapter
    }
}


class CalendarFragment : BaseFragment<FragmentCalendarBinding, CalendarMvvm.ViewModel>(), CalendarMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_calendar)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.itemAnimator = SlideInUpAnimator()
    }

    override fun onResume() {
        super.onResume()
        binding.calendarView.setDate(System.currentTimeMillis(), false, true)
        viewModel.init()
    }
}


@PerFragment
class CalendarViewModel @Inject
constructor(private val api: SugarAndRoseApi, override val adapter: PostAdapter) : BaseViewModel<CalendarMvvm.View>(), CalendarMvvm.ViewModel {

    override val dateCallback: (LocalDate) -> Unit = {
        adapter.clear()
        api.getPostsForDay(LocalDateTime.of(it, LocalTime.of(0, 0, 0)).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), LocalDateTime.of(it, LocalTime.of(23, 59, 59)).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .flattenAsFlowable { it }
                .map { LocalPost(it) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::add, Timber::e).let { disposable.add(it) }
    }

    override fun init() = dateCallback.invoke(LocalDate.now())
}