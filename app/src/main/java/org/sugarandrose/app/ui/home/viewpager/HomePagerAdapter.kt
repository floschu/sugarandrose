package org.sugarandrose.app.ui.home.viewpager

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
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
class HomePagerAdapter @Inject constructor(
    @ChildFragmentManager fm: FragmentManager,
    private val resources: Resources
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
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

