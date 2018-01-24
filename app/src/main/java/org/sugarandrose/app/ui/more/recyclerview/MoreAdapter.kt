package org.sugarandrose.app.ui.more.recyclerview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalMoreItem
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.base.navigator.Navigator
import org.sugarandrose.app.util.Utils
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerFragment
class MoreAdapter @Inject
constructor(@ActivityContext context: Context, navigator: Navigator) : RecyclerView.Adapter<MoreItemViewHolder>() {
    private val data: ArrayList<LocalMoreItem> = ArrayList()

    init {
        data.add(LocalMoreItem(R.drawable.ic_facebook, R.string.more_facebook, {
            val facebookUrl = "https://www.facebook.com/${BuildConfig.FB_NAME}"
            val uri = try {
                val applicationInfo = context.applicationContext.packageManager.getApplicationInfo("com.facebook.katana", 0)
                if (applicationInfo.enabled) Uri.parse("fb://page/${BuildConfig.FB_ID}")
                else Uri.parse(facebookUrl)
            } catch (e: Exception) {
                Uri.parse(facebookUrl)
            }
            navigator.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }))
        data.add(LocalMoreItem(R.drawable.ic_instagram, R.string.more_instagram, {
            val uri = Uri.parse("http://instagram.com/_u/${BuildConfig.INSTA_NAME}")
            try {
                navigator.startActivity(Intent(Intent.ACTION_VIEW, uri).apply { `package` = "com.instagram.android" })
            } catch (e: Exception) {
                navigator.startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }))
        //todo add others and header
    }

    override fun getItemCount() = data.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreItemViewHolder = Utils.createViewHolder(parent, R.layout.item_more, ::MoreItemViewHolder)
    override fun onBindViewHolder(viewHolder: MoreItemViewHolder, position: Int) = viewHolder.viewModel.update(data[position])
}