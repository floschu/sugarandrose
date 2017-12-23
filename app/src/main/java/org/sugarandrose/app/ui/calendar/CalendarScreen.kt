package org.sugarandrose.app.ui.calendar

import android.databinding.Bindable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import org.sugarandrose.app.BR
import org.sugarandrose.app.R
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.databinding.FragmentCalendarBinding
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.util.NotifyPropertyChangedDelegate
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

        @get:Bindable
        var refreshing: Boolean

        @get:Bindable
        var selectedDate: Long

        fun onRefresh()
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

    override fun onResume() {
        super.onResume()
        viewModel.onRefresh()
    }
}


@PerFragment
class CalendarViewModel @Inject
constructor(private val api: SugarAndRoseApi) : BaseViewModel<CalendarMvvm.View>(), CalendarMvvm.ViewModel {
    override var refreshing: Boolean by NotifyPropertyChangedDelegate(false, BR.refreshing)
    override var selectedDate: Long = 0 //todo why are millis always the same?
        set(value) {
            field = value
            notifyPropertyChanged(BR.selectedDate)

            val date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneId.systemDefault())
            refreshing = true
            api.getPostsForDay(
                    date.withHour(0).withMinute(0).withSecond(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    date.withHour(23).withMinute(59).withSecond(59).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            ).subscribe({ it.forEach { Timber.w(it.title.rendered) }; refreshing = false }, { Timber.e(it); refreshing = false }).let { disposable.add(it) }
        }

    override fun onRefresh() {
        selectedDate = Instant.now().toEpochMilli()
    }
}