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
import org.sugarandrose.app.data.local.PrefRepo
import org.sugarandrose.app.data.model.LocalMore
import org.sugarandrose.app.data.model.LocalMoreHeader
import org.sugarandrose.app.data.model.LocalMoreItem
import org.sugarandrose.app.data.model.LocalMorePage
import org.sugarandrose.app.databinding.FragmentMoreBinding
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.qualifier.FragmentDisposable
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.BaseFragment
import org.sugarandrose.app.ui.base.feedback.Snacker
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.ui.base.view.MvvmView
import org.sugarandrose.app.ui.base.viewmodel.BaseViewModel
import org.sugarandrose.app.ui.base.viewmodel.MvvmViewModel
import org.sugarandrose.app.ui.more.recyclerview.MoreAdapter
import org.sugarandrose.app.util.Utils
import org.sugarandrose.app.util.extensions.areYouSureDialog
import org.sugarandrose.app.util.extensions.openNotificationSettings
import org.sugarandrose.app.util.extensions.shareApp
import org.sugarandrose.app.util.manager.ErrorManager
import org.sugarandrose.app.util.manager.SocialMediaManager
import org.sugarandrose.app.util.manager.TutorialManager
import org.sugarandrose.app.util.manager.WebManager
import timber.log.Timber
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface MoreMvvm {
    interface View : MvvmView

    interface ViewModel : MvvmViewModel<View> {
        val adapter: MoreAdapter

        fun onLogoClick()
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
            private val moreCacheManager: MoreCacheManager,
            private val errorManager: ErrorManager,
            private val snacker: Snacker,
            private val tutorialManager: TutorialManager,
            private val prefRepo: PrefRepo
) : BaseViewModel<MoreMvvm.View>(), MoreMvvm.ViewModel {

    override fun attachView(view: MoreMvvm.View, savedInstanceState: Bundle?) {
        super.attachView(view, savedInstanceState)
        disposable.addAll(moreCacheManager.dataSubject.subscribe(this::fillAdapter, Timber::e), reloadData())
    }

    private fun reloadData() = moreCacheManager.reloadData { errorManager.showError(it, snacker::show) }

    private fun fillAdapter(morePages: List<LocalMorePage>) {
        val moreData = ArrayList<Pair<Int, LocalMore>>()

        moreData.add(Pair(MoreAdapter.TYPE_HEADER, LocalMoreHeader(R.string.more_explore)))
        if (morePages.isNotEmpty()) morePages.forEach { moreData.add(Pair(MoreAdapter.TYPE_ITEM_GRID, it)) }
        else moreCacheManager.MORE_PAGES.forEach { moreData.add(Pair(MoreAdapter.TYPE_ITEM_GRID, LocalMorePage())) }

        moreData.add(Pair(MoreAdapter.TYPE_HEADER, LocalMoreHeader(R.string.more_social)))
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
            navigator.startActivity(Utils.mail(BuildConfig.EMAIL))
        })))

        moreData.add(Pair(MoreAdapter.TYPE_HEADER, LocalMoreHeader(R.string.more_info)))
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
                        "rxAndroid", "Realm", "tailoredappsandroidtemplate", "showcase",
                        "SupportLibrary", "Picasso", "sssimageview", "paperparcel"
                )
            }.start(context)
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_account_circle, R.string.more_masthead, {
            webManager.open(BuildConfig.MASTHEAD)
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_lock_open, R.string.more_privacy, {
            webManager.open(BuildConfig.PRIVACY)
        })))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_share, R.string.more_share_app, context::shareApp)))

        moreData.add(Pair(MoreAdapter.TYPE_HEADER, LocalMoreHeader(R.string.more_settings)))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_notifications_none, R.string.more_notifications, context::openNotificationSettings)))
        moreData.add(Pair(MoreAdapter.TYPE_ITEM, LocalMoreItem(R.drawable.ic_delete_forever, R.string.more_delete_data, {
            context.areYouSureDialog {
                favoritedRepo.clearData()
                tutorialManager.resetTutorials()
                prefRepo.onboardingDone = false
            }
        })))

        adapter.data = moreData
    }

    override fun onLogoClick() {
        webManager.open(BuildConfig.WEB_PAGE)
    }
}