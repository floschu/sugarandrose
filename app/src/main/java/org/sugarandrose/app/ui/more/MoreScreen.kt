package org.sugarandrose.app.ui.more

import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.toObservable
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.LocalMore
import org.sugarandrose.app.data.model.LocalMoreHeader
import org.sugarandrose.app.data.model.LocalMoreItem
import org.sugarandrose.app.data.model.LocalMorePage
import org.sugarandrose.app.data.model.remote.More
import org.sugarandrose.app.data.remote.SugarAndRoseApi
import org.sugarandrose.app.databinding.FragmentMoreBinding
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.more.recyclerview.MoreAdapter
import org.sugarandrose.app.util.SocialMediaManager
import org.sugarandrose.app.util.Utils
import org.sugarandrose.app.util.WebManager
import org.sugarandrose.app.util.extensions.areYouSureDialog
import org.sugarandrose.app.util.extensions.openNotificationSettings
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface MoreMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        val adapter: MoreAdapter
    }
}


class MoreFragment : BaseFragment<FragmentMoreBinding, MoreMvvm.ViewModel>(), MoreMvvm.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(false)
        return setAndBindContentView(inflater, container, savedInstanceState, R.layout.fragment_more)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2).apply {
            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = if (position in 1..4) 1 else 2
            }
        }
    }
}


@PerFragment
class MoreViewModel @Inject
constructor(override val adapter: MoreAdapter,
            @ActivityContext private val context: Context,
            private val webManager: WebManager,
            private val socialMediaManager: SocialMediaManager,
            private val favoritedRepo: FavoritedRepo,
            private val navigator: Navigator,
            private val api: SugarAndRoseApi
) : BaseViewModel<MoreMvvm.View>(), MoreMvvm.ViewModel {
    //rezeptindex, rosenglossar, rosenindex, diy
    private val morePages = listOf(api.getMore(1635), api.getMore(2887), api.getMore(10030), api.getMore(129))

    override fun attachView(view: MoreMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        Single.concat(morePages)
                .flatMapSingle { more ->
                    if (more.featured_media != 0L) api.getMedia(more.featured_media).map { LocalMorePage(more, it) }
                    else Single.just(LocalMorePage(more))
                }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::fillAdapter, Timber::e)
                .addTo(disposable)
    }

    private fun fillAdapter(morePages: List<LocalMorePage>) {
        val moreData = ArrayList<Pair<Int, LocalMore>>()

        moreData.add(Pair(adapter.TYPE_HEADER, LocalMoreHeader(R.string.more_explore)))
        morePages.forEach { moreData.add(Pair(adapter.TYPE_ITEM_GRID, it)) }

        moreData.add(Pair(adapter.TYPE_HEADER, LocalMoreHeader(R.string.more_about)))
        moreData.add(Pair(adapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_facebook, R.string.more_facebook, {
            socialMediaManager.openFacebook(BuildConfig.FB_NAME, BuildConfig.FB_ID)
        })))
        moreData.add(Pair(adapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_instagram, R.string.more_instagram, {
            socialMediaManager.openInstagram(BuildConfig.INSTAGRAM_NAME)
        })))
        moreData.add(Pair(adapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_pinterest, R.string.more_pinterest, {
            socialMediaManager.openPinterest(BuildConfig.PINTEREST_NAME)
        })))
        moreData.add(Pair(adapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_twitter, R.string.more_twitter, {
            socialMediaManager.openTwitter(BuildConfig.TWITTER_NAME, BuildConfig.TWITTER_ID)
        })))
        moreData.add(Pair(adapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_mail_outline, R.string.more_contact, {
            navigator.startActivity(Utils.mail("sugarandrosen@gmail.com"))
        })))
        moreData.add(Pair(adapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_account_circle, R.string.more_privacy, {
            webManager.open("https://sugarandrose.org/kontakt/impressum/")
        })))

        moreData.add(Pair(adapter.TYPE_HEADER, LocalMoreHeader(R.string.more_settings)))
        moreData.add(Pair(adapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_notifications_none, R.string.more_notifications, {
            context.openNotificationSettings()
        })))
        moreData.add(Pair(adapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_delete_forever, R.string.more_delete_data, {
            context.areYouSureDialog { favoritedRepo.clearData() }
        })))

        adapter.data = moreData
    }
}