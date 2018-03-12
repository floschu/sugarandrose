package org.sugarandrose.app.data.model

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import org.sugarandrose.app.data.model.remote.Media
import org.sugarandrose.app.data.model.remote.Post
import org.sugarandrose.app.util.extensions.fromRealmString
import org.sugarandrose.app.util.extensions.toRealmString
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
open class LocalPost(@PrimaryKey override var id: Long,
                     override var date: String,
                     override var name: String,
                     var url: String,
                     var image: String?,
                     var content: String?,
                     override var ANALYTICS_CATEGORY: String = "Post"
) : PaperParcelable, RealmObject(), LocalDisplayItem {
    constructor() : this(0, ZonedDateTime.now().toRealmString(), "", "", null, "", "")
    constructor(post: Post, media: Media) : this(post.id, post.date.toRealmString(), post.title.rendered, post.link, media.source_url, post.content?.rendered)
    constructor(post: Post) : this(post.id, post.date.toRealmString(), post.title.rendered, post.link, null, post.content?.rendered)

    companion object {
        @JvmField
        val CREATOR = PaperParcelLocalPost.CREATOR
    }

    fun getFormattedDate(): String = date.fromRealmString().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}
