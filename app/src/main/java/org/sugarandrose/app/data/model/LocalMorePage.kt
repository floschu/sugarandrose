package org.sugarandrose.app.data.model

import org.sugarandrose.app.data.model.remote.More

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */
class LocalMorePage(val image: String, val text: String, val link: String) : LocalMore {
    constructor() : this("", "", "")
    constructor(more: More) : this(more.better_featured_image.source_url, more.title.rendered, more.link)
}