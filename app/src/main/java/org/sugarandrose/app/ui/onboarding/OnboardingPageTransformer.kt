package org.sugarandrose.app.ui.onboarding

import android.support.v4.view.ViewPager
import android.view.View
import org.sugarandrose.app.injection.scopes.PerActivity
import javax.inject.Inject

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class OnboardingPageTransformer @Inject
constructor() : ViewPager.PageTransformer {
    override fun transformPage(page: View, position: Float) {

    }
}