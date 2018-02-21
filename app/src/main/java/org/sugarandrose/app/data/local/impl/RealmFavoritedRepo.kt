package org.sugarandrose.app.data.local.impl

import io.reactivex.Flowable
import io.reactivex.rxkotlin.Flowables
import io.realm.Realm
import io.realm.RealmObject
import org.sugarandrose.app.data.local.FavoritedRepo
import org.sugarandrose.app.data.model.LocalDisplayItem
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.model.LocalRose
import org.sugarandrose.app.injection.scopes.PerApplication
import org.sugarandrose.app.util.extensions.findAll
import org.sugarandrose.app.util.extensions.fromRealmString
import org.sugarandrose.app.util.extensions.use

import javax.inject.Inject
import javax.inject.Provider

@PerApplication
class RealmFavoritedRepo @Inject
constructor(private val provider: Provider<Realm>) : FavoritedRepo {

    override val allDisplayItems: Flowable<List<LocalDisplayItem>>
        get() = provider.use {
            Flowables.combineLatest(
                    getChangedObservable<LocalMedia>(it),
                    getChangedObservable<LocalPost>(it),
                    getChangedObservable<LocalRose>(it),
                    { media, posts, roses ->
                        ArrayList<LocalDisplayItem>(media.size + posts.size + roses.size).apply {
                            addAll(it.copyFromRealm(media))
                            addAll(it.copyFromRealm(posts))
                            addAll(it.copyFromRealm(roses))
                        }.sortedByDescending { it.date.fromRealmString() }
                    }
            )
        }

    override fun isContained(item: LocalDisplayItem): Boolean = provider.use {
        when (item) {
            is LocalMedia -> it.where(LocalMedia::class.java).equalTo("id", item.id).findFirst() != null
            is LocalPost -> it.where(LocalPost::class.java).equalTo("id", item.id).findFirst() != null
            is LocalRose -> it.where(LocalRose::class.java).equalTo("id", item.id).findFirst() != null
            else -> false
        }
    }

    override fun addItem(item: LocalDisplayItem) = provider.use {
        when (item) {
            is LocalMedia -> it.executeTransaction { it.copyToRealm(item) }
            is LocalPost -> it.executeTransaction { it.copyToRealm(item) }
            is LocalRose -> it.executeTransaction { it.copyToRealm(item) }
        }
    }

    override fun deleteItem(item: LocalDisplayItem) = provider.use {
        when (item) {
            is LocalMedia -> it.executeTransaction { it.where(LocalMedia::class.java).equalTo("id", item.id).findFirst()?.deleteFromRealm() }
            is LocalPost -> it.executeTransaction { it.where(LocalPost::class.java).equalTo("id", item.id).findFirst()?.deleteFromRealm() }
            is LocalRose -> it.executeTransaction { it.where(LocalRose::class.java).equalTo("id", item.id).findFirst()?.deleteFromRealm() }
        }
    }

    override fun clearData() = provider.use { it.executeTransaction { it.deleteAll() } }

    private inline fun <reified T : RealmObject> getChangedObservable(realm: Realm): Flowable<out List<T>> =
            realm.where(T::class.java).findAll().asFlowable().filter { it.isLoaded }
}