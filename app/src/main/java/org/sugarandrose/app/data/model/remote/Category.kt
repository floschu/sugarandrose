package org.sugarandrose.app.data.model.remote

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@Parcelize
data class Category(
    var id: Int,
    var name: String,
    @SerializedName("description")
    var image: String,
    var link: String,
    var count: Int,
    var parent: Int
) : Parcelable {
    constructor() : this(0, "", "", "", 0, 0)
}
