package org.sugarandrose.app.data.model

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.sugarandrose.app.data.model.remote.Post
import org.sugarandrose.app.util.extensions.fromRealmString
import org.sugarandrose.app.util.extensions.toRealmString
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@Parcelize
open class LocalPost(
    @PrimaryKey override var id: Long,
    override var date: String,
    override var name: String,
    var url: String,
    var image: String?,
    var content: String?,
    override var ANALYTICS_CATEGORY: String = "Post"
) : Parcelable, RealmObject(), LocalDisplayItem {
    constructor() : this(0, ZonedDateTime.now().toRealmString(), "", "", null, "", "")
    constructor(post: Post) : this(post.id, post.date.toRealmString(), post.title.rendered, post.link, post.better_featured_image.source_url, post.content?.rendered)

    fun getFormattedDate(): String = date.fromRealmString().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
}
