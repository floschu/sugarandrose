package org.sugarandrose.app.ui.main

import android.annotation.SuppressLint
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
import org.sugarandrose.app.ui.calendar.CalendarFragment
import org.sugarandrose.app.ui.categories.CategoriesFragment
import org.sugarandrose.app.ui.test.TestFragment


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class MainAdapter(fragmentManager: FragmentManager, @IdRes containerId: Int, @IdRes defaultMenuItemId: Int, savedInstanceState: Bundle?)
    : NavigationViewFragmentAdapter(fragmentManager, containerId, defaultMenuItemId, savedInstanceState) {

    override fun getFragment(@IdRes menuItemId: Int): Fragment = when (menuItemId) {
        R.id.bnv_new -> NewFragment()
        R.id.bnv_categories -> CategoriesFragment()
        R.id.bnv_calendar -> CalendarFragment()
        else -> TestFragment()
    }

    companion object {
        @SuppressLint("RestrictedApi")
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