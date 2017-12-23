package org.sugarandrose.app.data.model.remote

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
class Category(var id: Int,
               var name: String,
               var link: String,
               var count: Int
) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelCategory.CREATOR
    }
}
