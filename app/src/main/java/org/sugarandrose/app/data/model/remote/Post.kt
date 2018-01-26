package org.sugarandrose.app.data.model.remote

import org.threeten.bp.ZonedDateTime

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class Post(val id: Long,
           val date: ZonedDateTime,
           val title: DisplayText,
           val content: DisplayText,
           val link: String,
           val featured_media: Long,
           val tags: List<Long>,
           val categories: List<Long>
)