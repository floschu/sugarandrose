package org.sugarandrose.app.data.local

import io.reactivex.Flowable
import org.sugarandrose.app.data.model.LocalDisplayItem

interface FavoritedRepo {
    val allDisplayItems: Flowable<List<LocalDisplayItem>>

    fun isContained(item: LocalDisplayItem): Boolean

    fun addItem(item: LocalDisplayItem)
    fun deleteItem(item: LocalDisplayItem)

    fun clearData()
}
