package org.sugarandrose.app.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import org.sugarandrose.app.data.model.remote.Category

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@Parcelize
class LocalCategory(var id: Int,
                    var name: String,
                    var image: String,
                    var children: List<LocalCategory>
) : Parcelable {
    constructor(category: Category, children: List<LocalCategory>) : this(category.id, category.name, category.image, children)
    constructor() : this(0, "", "", emptyList())
}
