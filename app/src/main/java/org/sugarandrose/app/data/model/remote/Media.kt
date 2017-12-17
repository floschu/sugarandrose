package org.sugarandrose.app.data.model.remote

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
class Media(val id: Int,
            val source_url: String
): PaperParcelable {
    companion object {
        val CREATOR = PaperParcelMedia.CREATOR
    }
}