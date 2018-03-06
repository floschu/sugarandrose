package org.sugarandrose.app.data.model.local

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.sugarandrose.app.data.model.local.PaperParcelLocalRose
import org.sugarandrose.app.util.extensions.toRealmString
import org.threeten.bp.ZonedDateTime
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
open class LocalRose(@PrimaryKey override var id: Long,
                     override var date: String,
                     var image: String,
                     var name: String,
                     var category: String,
                     override var ANALYTICS_CATEGORY: String = "Rose"
) : PaperParcelable, RealmObject(), LocalDisplayItem {
    constructor() : this(0, ZonedDateTime.now().toRealmString(), "", "", "")
    constructor(id: Long, image: String, name: String) : this(id, ZonedDateTime.now().toRealmString(), image, name, name[0].toString())

    companion object {
        @JvmField
        val CREATOR = PaperParcelLocalRose.CREATOR
    }
}