package org.sugarandrose.app.ui.main

import android.support.annotation.IdRes
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.patloew.navigationviewfragmentadapters.NavigationViewFragmentAdapter
import org.sugarandrose.app.R
import org.sugarandrose.app.ui.news.NewFragment
import timber.log.Timber
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import org.sugarandrose.app.ui.favorited.FavoritedFragment
import org.sugarandrose.app.ui.more.MoreFragment
import org.sugarandrose.app.ui.search.SearchFragment
import android.view.ViewGroup
import android.support.v7.view.menu.MenuView
import android.view.View


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class MainAdapter(fragmentManager: FragmentManager, @IdRes containerId: Int, @IdRes defaultMenuItemId: Int, savedInstanceState: Bundle?)
    : NavigationViewFragmentAdapter(fragmentManager, containerId, defaultMenuItemId, savedInstanceState) {

    override fun getFragment(@IdRes menuItemId: Int): Fragment = when (menuItemId) {
        R.id.bnv_new -> NewFragment()
        R.id.bnv_search -> SearchFragment()
        R.id.bnv_favorited -> FavoritedFragment()
        R.id.bnv_more -> MoreFragment()
        else -> Fragment()
    }

    companion object {
        fun disableShiftMode(view: BottomNavigationView) {
            try {
                val menuView = view.getChildAt(0) as BottomNavigationMenuView
                val shiftingMode = menuView.javaClass.getDeclaredField("mShiftingMode")
                shiftingMode.isAccessible = true
                shiftingMode.setBoolean(menuView, false)
                shiftingMode.isAccessible = false
                for (i in 0 until menuView.childCount) {
                    val item = menuView.getChildAt(i) as BottomNavigationItemView
                    item.setShiftingMode(false)
                    item.setChecked(item.itemData.isChecked)
                }
            } catch (e: Exception) {
                Timber.e(e, "Could not disable shift mode on bottom navigation view")
            }
        }
    }
}