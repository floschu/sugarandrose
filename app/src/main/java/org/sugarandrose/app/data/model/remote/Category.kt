package org.sugarandrose.app.data.model.remote

import com.google.gson.annotations.SerializedName
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
class Category(var id: Int,
               var name: String,
               @SerializedName("description")
               var image: String,
               var link: String,
               var count: Int
) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelCategory.CREATOR
    }
}
