package org.sugarandrose.app.data.model.remote

import org.threeten.bp.ZonedDateTime
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
class Post(var id: Int,
           var date_gmt: ZonedDateTime,
           var title: DisplayText,
           var link: String,
           var content: DisplayText,
           var excerpt: DisplayText,
           val featured_media: Int
) : PaperParcelable {
    companion object {
        val CREATOR = PaperParcelPost.CREATOR
    }
}
