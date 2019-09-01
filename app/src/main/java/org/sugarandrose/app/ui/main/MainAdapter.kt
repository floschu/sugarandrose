package org.sugarandrose.app.ui.main

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.patloew.navigationviewfragmentadapters.NavigationViewFragmentAdapter
import org.sugarandrose.app.R
import org.sugarandrose.app.ui.favorited.FavoritedFragment
import org.sugarandrose.app.ui.home.HomeFragment
import org.sugarandrose.app.ui.more.MoreFragment
import org.sugarandrose.app.ui.search.SearchFragment

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class MainAdapter(fragmentManager: FragmentManager, @IdRes containerId: Int, @IdRes defaultMenuItemId: Int, savedInstanceState: Bundle?)
    : NavigationViewFragmentAdapter(fragmentManager, containerId, defaultMenuItemId, savedInstanceState) {

    override fun getFragment(@IdRes menuItemId: Int): Fragment = when (menuItemId) {
        R.id.bnv_new -> HomeFragment()
        R.id.bnv_search -> SearchFragment()
        R.id.bnv_favorited -> FavoritedFragment()
        R.id.bnv_more -> MoreFragment()
        else -> Fragment()
    }
}