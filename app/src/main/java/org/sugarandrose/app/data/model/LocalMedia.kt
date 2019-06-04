package org.sugarandrose.app.data.model

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.sugarandrose.app.data.model.remote.Media
import org.sugarandrose.app.util.extensions.toRealmString

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@Parcelize
open class LocalMedia(@PrimaryKey override var id: Long,
                      var image: String,
                      override var date: String,
                      override var name: String = date,
                      override var ANALYTICS_CATEGORY: String = "Media"
) : Parcelable, RealmObject(), LocalDisplayItem {
    constructor() : this(0, "", "")
    constructor(media: Media) : this(media.id, media.source_url, media.date.toRealmString())
}