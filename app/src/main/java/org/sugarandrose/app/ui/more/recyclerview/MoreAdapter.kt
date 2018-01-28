package org.sugarandrose.app.ui.more.recyclerview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.LocalMore
import org.sugarandrose.app.data.model.LocalMoreHeader
import org.sugarandrose.app.data.model.LocalMoreItem
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.util.Utils
import org.sugarandrose.app.util.WebManager
import org.sugarandrose.app.util.extensions.areYouSureDialog
import org.sugarandrose.app.util.extensions.openNotificationSettings
import javax.inject.Inject


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerFragment
class MoreAdapter @Inject
constructor(@ActivityContext context: Context, webManager: WebManager, favoritedRepo: FavoritedRepo, navigator: Navigator) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    private val data: ArrayList<Pair<Int, LocalMore>> = ArrayList()

    init {
        data.add(Pair(TYPE_HEADER, LocalMoreHeader(R.string.more_about)))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_facebook, R.string.more_facebook, {
            webManager.openFacebook(BuildConfig.FB_NAME, BuildConfig.FB_ID)
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_instagram, R.string.more_instagram, {
            webManager.openInstagram(BuildConfig.INSTAGRAM_NAME)
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_pinterest, R.string.more_pinterest, {
            webManager.openPinterest(BuildConfig.PINTEREST_NAME)
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_twitter, R.string.more_twitter, {
            webManager.openTwitter(BuildConfig.TWITTER_NAME, BuildConfig.TWITTER_ID)
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_mail_outline, R.string.more_contact, {
            navigator.startActivity(Utils.mail("sugarandrosen@gmail.com"))
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_account_circle, R.string.more_privacy, {
            webManager.open("https://sugarandrose.org/kontakt/impressum/")
        })))

        data.add(Pair(TYPE_HEADER, LocalMoreHeader(R.string.more_explore)))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_format_align_left, R.string.more_rose_index, {
            webManager.open("https://sugarandrose.org/rosenindex/")
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_format_align_left, R.string.more_recepe_index, {
            webManager.open("https://sugarandrose.org/rezeptindex/")
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_format_align_left, R.string.more_glossar, {
            webManager.open("https://sugarandrose.org/rosenglossar/")
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_book, R.string.more_books_blog, {
            webManager.open("https://sugarandrose.org/buecherblog/")
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_book, R.string.more_books_graphics, {
            webManager.open("https://sugarandrose.org/buecher/")
        })))

        data.add(Pair(TYPE_HEADER, LocalMoreHeader(R.string.more_settings)))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_notifications_none, R.string.more_notifications, {
            context.openNotificationSettings()
        })))
        data.add(Pair(TYPE_ITEM, LocalMoreItem(R.drawable.ic_delete_forever, R.string.more_delete_data, {
            context.areYouSureDialog { favoritedRepo.clearData() }
        })))
    }

    override fun getItemCount() = data.size
    override fun getItemViewType(position: Int): Int = data[position].first

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        TYPE_ITEM -> Utils.createViewHolder(parent, R.layout.item_more, ::MoreItemViewHolder)
        else -> Utils.createViewHolder(parent, R.layout.item_more_header, ::MoreHeaderItemViewHolder)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when (viewHolder) {
            is MoreItemViewHolder -> viewHolder.viewModel.update(data[position].second as LocalMoreItem)
            is MoreHeaderItemViewHolder -> viewHolder.update(data[position].second.text)
        }
    }
}