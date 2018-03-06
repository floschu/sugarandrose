package org.sugarandrose.app.data.model.local

import org.sugarandrose.app.util.extensions.toRealmString
import org.threeten.bp.ZonedDateTime


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

data class LocalDisplayHeader(val title: String,
                              override var id: Long = title.hashCode().toLong(),
                              override var date: String = ZonedDateTime.now().toRealmString(),
                              override val ANALYTICS_CATEGORY: String = "header"
) : LocalDisplayItem