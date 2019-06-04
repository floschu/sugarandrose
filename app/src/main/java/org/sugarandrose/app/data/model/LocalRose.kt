package org.sugarandrose.app.data.model

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.sugarandrose.app.util.extensions.toRealmString
import org.threeten.bp.ZonedDateTime

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@Parcelize
open class LocalRose(@PrimaryKey override var id: Long,
                     override var date: String,
                     var image: String,
                     override var name: String,
                     override var ANALYTICS_CATEGORY: String = "Rose"
) : Parcelable, RealmObject(), LocalDisplayItem {
    constructor() : this(0, ZonedDateTime.now().toRealmString(), "", "")
    constructor(id: Long, image: String, name: String) : this(id, ZonedDateTime.now().toRealmString(), image, name)
}