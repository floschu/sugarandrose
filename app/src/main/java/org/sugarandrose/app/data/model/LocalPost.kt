package org.sugarandrose.app.data.model

import org.sugarandrose.app.data.model.remote.Media
import org.sugarandrose.app.data.model.remote.Post
import org.threeten.bp.ZonedDateTime
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
class LocalPost(val id: Int,
                val date: ZonedDateTime,
                val title: String,
                val url: String,
                val excerpt: String,
                val image: String?
) : PaperParcelable {
    constructor() : this(0, ZonedDateTime.now(), "", "", "", null)
    constructor(post: Post, media: Media) : this(post.id, post.date, post.title.rendered, post.link, removeHtmlTagsExcerpt(post.excerpt.rendered), media.source_url)
    constructor(post: Post) : this(post.id, post.date, post.title.rendered, post.link, removeHtmlTagsExcerpt(post.excerpt.rendered), null)

    companion object {
        @JvmField
        val CREATOR = PaperParcelLocalPost.CREATOR

        private fun removeHtmlTagsExcerpt(exc: String): String {
            return exc.removeSuffix("\n").removePrefix("<p>").removeSuffix("</p>")
        }
    }

}
