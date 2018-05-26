package org.sugarandrose.app.data.model.remote

import org.threeten.bp.ZonedDateTime

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class Post(val id: Long,
           val date: ZonedDateTime,
           val title: DisplayText,
           val link: String,
           val better_featured_image: FeaturedImage,
           val content: DisplayText?
)