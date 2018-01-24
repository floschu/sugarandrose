package org.sugarandrose.app.data.model

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

data class LocalMoreItem(@DrawableRes val icon: Int, @StringRes val text: Int, val action: () -> Unit)