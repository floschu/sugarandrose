package org.sugarandrose.app.util.manager

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.view.View
import org.sugarandrose.app.R
import uk.co.deanwild.materialshowcaseview.IShowcaseListener
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class TutorialManager(private val activity: FragmentActivity) {

    fun resetTutorials() = MaterialShowcaseView.resetAll(activity as Context)

    fun favorite(view: View, dismissCallback: () -> Unit) =
            getStandardBuilder(view, "favorite").apply {
                setContentText(R.string.tutorial_favorite)
                setShapePadding(20)
                setDismissCallback(dismissCallback)
                show()
            }

    fun unfavorite(view: View) =
            getStandardBuilder(view, "unfavorite").apply {
                setContentText(R.string.tutorial_unfavorite)
                setShapePadding(20)
                show()
            }

    fun share(view: View) =
            getStandardBuilder(view, "share").apply {
                setContentText(R.string.tutorial_share)
                setShapePadding(20)
                show()
            }

    fun search(view: View) =
            getStandardBuilder(view, "search").apply {
                setContentText(R.string.tutorial_search)
                show()
            }

    private fun getStandardBuilder(view: View, id: String): MaterialShowcaseView.Builder =
            MaterialShowcaseView.Builder(activity).apply {
                setTarget(view)
                setMaskColour(ContextCompat.getColor(activity as Context, R.color.tutorial_color))
                setContentTextColor(ContextCompat.getColor(activity as Context, R.color.textWhitePrimary))
                setDismissText(R.string.tutorial_understand)
                setDismissTextColor(ContextCompat.getColor(activity as Context, R.color.textWhiteSecondary))
                singleUse(id)
            }

    private fun MaterialShowcaseView.Builder.setDismissCallback(dismissCallback: () -> Unit) =
            setListener(object : IShowcaseListener {
                override fun onShowcaseDisplayed(p0: MaterialShowcaseView?) {}
                override fun onShowcaseDismissed(p0: MaterialShowcaseView?) {
                    dismissCallback.invoke()
                }
            })
}