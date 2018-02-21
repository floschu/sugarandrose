package org.sugarandrose.app.data.model.remote

import org.threeten.bp.ZonedDateTime


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

data class Media(val id: Long, val source_url: String, var date: ZonedDateTime)