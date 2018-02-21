package org.sugarandrose.app.data.model

import org.sugarandrose.app.data.model.remote.Media
import org.sugarandrose.app.data.model.remote.More

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */
class LocalMorePage(val image: String, val text: String, val link: String) : LocalMore {
    constructor(more: More) : this("", more.title.rendered, more.link)
    constructor(more: More, media: Media) : this(media.source_url, more.title.rendered, more.link)
}