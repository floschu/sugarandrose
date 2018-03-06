package org.sugarandrose.app.ui.more

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import io.reactivex.disposables.CompositeDisposable
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.local.LocalMore
import org.sugarandrose.app.data.model.local.LocalMoreHeader
import org.sugarandrose.app.data.model.local.LocalMoreItem
import org.sugarandrose.app.data.model.local.LocalMorePage
import org.sugarandrose.app.databinding.FragmentMoreBinding
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.more.recyclerview.MoreAdapter
import org.sugarandrose.app.util.Utils
import org.sugarandrose.app.util.extensions.areYouSureDialog
import org.sugarandrose.app.util.extensions.openNotificationSettings
import org.sugarandrose.app.util.manager.SocialMediaManager
import org.sugarandrose.app.util.manager.WebManager
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
            @FragmentDisposable private val disposable: CompositeDisposable,
            @ActivityContext private val context: Context,
            private val webManager: WebManager,
            private val socialMediaManager: SocialMediaManager,
            private val favoritedRepo: FavoritedRepo,
            private val navigator: Navigator,
            private val moreCacheManager: MoreCacheManager
) : BaseViewModel<MoreMvvm.View>(), MoreMvvm.ViewModel {

    override fun attachView(view: MoreMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        disposable.addAll(
                moreCacheManager.dataSubject.subscribe(this::fillAdapter, Timber::e),
                moreCacheManager.reloadData()
        )
    }

    private fun fillAdapter(morePages: List<LocalMorePage>) {
        val moreData = ArrayList<Pair<Int, LocalMore>>()

        moreData.add(Pair(MoreAdapter.TYPE_HEADER, LocalMoreHeader(R.string.more_explore)))
        if (morePages.isNotEmpty()) morePages.forEach { moreData.add(Pair(MoreAdapter.TYPE_ITEM_GRID, it)) }
        else moreCacheManager.MORE_PAGES.forEach { moreData.add(Pair(MoreAdapter.TYPE_ITEM_GRID, LocalMorePage())) }

        moreData.add(Pair(MoreAdapter.TYPE_HEADER, LocalMoreHeader(R.string.more_about)))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_facebook, R.string.more_facebook, {
            socialMediaManager.openFacebook(BuildConfig.FB_NAME, BuildConfig.FB_ID)
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_instagram, R.string.more_instagram, {
            socialMediaManager.openInstagram(BuildConfig.INSTAGRAM_NAME)
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_pinterest, R.string.more_pinterest, {
            socialMediaManager.openPinterest(BuildConfig.PINTEREST_NAME)
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_twitter, R.string.more_twitter, {
            socialMediaManager.openTwitter(BuildConfig.TWITTER_NAME, BuildConfig.TWITTER_ID)
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_mail_outline, R.string.more_contact, {
            navigator.startActivity(Utils.mail("sugarandrosen@gmail.com"))
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_account_circle, R.string.more_privacy, {
            webManager.open("https://sugarandrose.org/kontakt/impressum/")
        })))

        moreData.add(Pair(MoreAdapter.TYPE_HEADER, LocalMoreHeader(R.string.more_settings)))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_notifications_none, R.string.more_notifications, {
            context.openNotificationSettings()
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_delete_forever, R.string.more_delete_data, {
            context.areYouSureDialog { favoritedRepo.clearData() }
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_info_outline, R.string.more_app_info, {
            LibsBuilder().apply {
                withFields(*Libs.toStringArray(R.string::class.java.fields))
                withVersionShown(false)
                withLicenseShown(true)
                withAutoDetect(true)
                withAboutAppName(context.getString(R.string.app_name))
                withAboutIconShown(true)
                withAboutVersionShown(true)
                withActivityTitle(context.getString(R.string.more_app_info))
                withActivityStyle(Libs.ActivityStyle.LIGHT)
                withAboutDescription(context.getString(R.string.more_dev_info))
                withLibraries(
                        "Dagger2", "LeakCanary", "OkHttp", "Retrofit", "Timber", "gson", "rxjava",
                        "rxAndroid", "Realm", "tailoredappsandroidtemplate",
                        "SupportLibrary", "Picasso", "sssimageview", "paperparcel"
                )
            }.start(context)
        })))

        adapter.data = moreData
    }
}