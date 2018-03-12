package org.sugarandrose.app.data.model

import org.sugarandrose.app.util.extensions.toRealmString
import org.threeten.bp.ZonedDateTime


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

data class LocalDisplayHeader(override var name: String,
                              override var id: Long = name.hashCode().toLong(),
                              override var date: String = ZonedDateTime.now().toRealmString(),
                              override val ANALYTICS_CATEGORY: String = "header"
) : LocalDisplayItem