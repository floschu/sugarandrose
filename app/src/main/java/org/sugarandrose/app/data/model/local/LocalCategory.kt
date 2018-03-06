package org.sugarandrose.app.data.model.local

import org.sugarandrose.app.data.model.local.PaperParcelLocalCategory
import org.sugarandrose.app.data.model.remote.Category
import paperparcel.PaperParcel
import paperparcel.PaperParcelable

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PaperParcel
class LocalCategory(var id: Int,
                    var name: String,
                    var image: String,
                    var children: List<LocalCategory>
) : PaperParcelable {
    companion object {
        @JvmField
        val CREATOR = PaperParcelLocalCategory.CREATOR
    }

    constructor(category: Category, children: List<LocalCategory>) : this(category.id, category.name, category.image, children)
    constructor() : this(0, "", "", emptyList())
}
