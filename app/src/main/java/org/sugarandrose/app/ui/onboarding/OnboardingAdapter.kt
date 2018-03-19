package org.sugarandrose.app.ui.onboarding

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import org.sugarandrose.app.injection.qualifier.ActivityFragmentManager
import org.sugarandrose.app.injection.scopes.PerActivity
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class OnboardingAdapter @Inject
internal constructor(@ActivityFragmentManager fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment = OnboardingItemFragment()
    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE
    override fun getCount(): Int = PAGES

    companion object {
        val PAGES = 3
    }
}