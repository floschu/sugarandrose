package org.sugarandrose.app.data.model

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

interface LocalDisplayItem {
    var id: Long
    var date: String
    var name: String

    val ANALYTICS_CATEGORY: String
}
