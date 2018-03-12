package org.sugarandrose.app.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.sugarandrose.app.data.model.remote.Media
import org.sugarandrose.app.util.extensions.toRealmString
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
open class LocalMedia(@PrimaryKey override var id: Long,
                      var image: String,
                      override var date: String,
                      override var name: String = date,
                      override var ANALYTICS_CATEGORY: String = "Media"
) : PaperParcelable, RealmObject(), LocalDisplayItem {
    constructor() : this(0, "", "")
    constructor(media: Media) : this(media.id, media.source_url, media.date.toRealmString())

    companion object {
        @JvmField
        val CREATOR = PaperParcelLocalMedia.CREATOR
    }
}