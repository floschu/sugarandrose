package org.sugarandrose.app.ui.home.viewpager

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.sugarandrose.app.R
import org.sugarandrose.app.injection.qualifier.ChildFragmentManager
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.news.NewFragment
import org.sugarandrose.app.ui.roses.RosesFragment
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerFragment
class HomePagerAdapter @Inject
internal constructor(@ChildFragmentManager fm: FragmentManager, private val resources: Resources) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int = 2
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> NewFragment()
        1 -> RosesFragment()
        else -> Fragment()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> resources.getString(R.string.home_new_title)
        1 -> resources.getString(R.string.home_roses_title)
        else -> ""
    }
}

