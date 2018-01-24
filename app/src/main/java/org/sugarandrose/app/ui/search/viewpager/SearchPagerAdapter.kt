package org.sugarandrose.app.ui.search.viewpager

import android.content.res.Resources
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import org.sugarandrose.app.R
import org.sugarandrose.app.injection.qualifier.ChildFragmentManager
import org.sugarandrose.app.injection.scopes.PerFragment
import org.sugarandrose.app.ui.categories.overview.CategoriesFragment
import org.sugarandrose.app.ui.textsearch.TextSearchFragment
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerFragment
class SearchPagerAdapter @Inject
internal constructor(@ChildFragmentManager fm: FragmentManager, private val resources: Resources) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int = 2
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> TextSearchFragment()
        1 -> CategoriesFragment()
        else -> Fragment()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> resources.getString(R.string.search_text_title)
        1 -> resources.getString(R.string.search_categories_title)
        else -> ""
    }
}
