package org.sugarandrose.app.util.manager

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject
import org.sugarandrose.app.data.model.LocalDisplayItem
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.model.LocalRose
import org.sugarandrose.app.injection.qualifier.AppContext
import org.sugarandrose.app.injection.scopes.PerApplication

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerApplication
class EventLogManager @Inject
constructor(@AppContext private val context: Context) {
    private val analytics = FirebaseAnalytics.getInstance(context)

    fun logFavorite(localDisplayItem: LocalDisplayItem) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, localDisplayItem.id.toString())
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, localDisplayItem.ANALYTICS_CATEGORY)
            when (localDisplayItem) {
                is LocalPost -> putString(FirebaseAnalytics.Param.ITEM_NAME, localDisplayItem.name)
                is LocalRose -> putString(FirebaseAnalytics.Param.ITEM_NAME, localDisplayItem.name)
            }
        }
        analytics.logEvent("favorite", bundle)
    }

    fun logShare(localDisplayItem: LocalDisplayItem) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, localDisplayItem.id.toString())
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, localDisplayItem.ANALYTICS_CATEGORY)
            if (localDisplayItem is LocalPost) {
                putString(FirebaseAnalytics.Param.ITEM_NAME, localDisplayItem.name)
            }
        }
        analytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle)
    }

    fun logOpen(localDisplayItem: LocalDisplayItem) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, localDisplayItem.id.toString())
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, localDisplayItem.ANALYTICS_CATEGORY)
            if (localDisplayItem is LocalPost) {
                putString(FirebaseAnalytics.Param.ITEM_NAME, localDisplayItem.name)
            }
        }
        analytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }
}
