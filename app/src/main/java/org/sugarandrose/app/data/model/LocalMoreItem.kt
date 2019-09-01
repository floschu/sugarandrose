package org.sugarandrose.app.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class LocalMoreItem(@DrawableRes val icon: Int, @StringRes val text: Int, val action: () -> Unit) : LocalMore
