package org.sugarandrose.app.data.model.remote

import org.threeten.bp.ZonedDateTime

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class Post(var id: Int,
           var date: ZonedDateTime,
           var title: DisplayText,
           var link: String,
           val featured_media: Int
)