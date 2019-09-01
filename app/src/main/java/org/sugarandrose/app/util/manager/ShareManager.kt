package org.sugarandrose.app.util.manager

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.FileProvider
import coil.Coil
import coil.api.get
import coil.api.load
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.R
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.data.model.LocalPost
import org.sugarandrose.app.data.model.LocalRose
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.ui.base.navigator.Navigator

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class ShareManager @Inject
constructor(@ActivityContext private val context: Context, private val navigator: Navigator, private val eventLogManager: EventLogManager) {

    private val cachedName = "shareimage.jpg"
    private val tempFile: File by lazy { File(context.cacheDir, cachedName) }

    fun sharePost(item: LocalPost): Completable {
        eventLogManager.logShare(item)
        return if (item.image != null) loadImage(item.image)
            .flatMapSingle(this::cacheBitmapForShare)
            .flatMapCompletable { sharePostInternally(item.name, "${BuildConfig.WEB_PAGE}?${context.getString(R.string.deeplink_post_query)}=${item.id}", it) }
        else sharePostInternally(item.name, item.url)
    }

    private fun sharePostInternally(title: String, url: String, file: File? = null): Completable = Completable.create {
        @Suppress("DEPRECATION")
        val share = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text_post, url))
        }
        share.apply {
            if (file != null) {
                val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file)
                setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else type = "text/plain"
        }

        navigator.startActivity(Intent.createChooser(share, context.getString(R.string.share_title)))
        it.onComplete()
    }

    fun shareMedia(item: LocalMedia): Completable {
        eventLogManager.logShare(item)
        return loadImage(item.image)
            .flatMapSingle(this::cacheBitmapForShare)
            .flatMapCompletable(this::shareMediaInternally)
    }

    private fun shareMediaInternally(bitmap: File): Completable = Completable.create {
        val share = Intent(Intent.ACTION_SEND).apply {
            val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", bitmap)
            setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text_media))
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        navigator.startActivity(Intent.createChooser(share, context.getString(R.string.share_title)))
        it.onComplete()
    }

    fun shareRose(item: LocalRose): Completable {
        eventLogManager.logShare(item)
        return loadImage(item.image)
            .flatMapSingle(this::cacheBitmapForShare)
            .flatMapCompletable(this::shareRoseInternally)
    }

    private fun loadImage(url: String?): Maybe<Bitmap> = Maybe.create { emitter ->
        if (url == null) emitter.onComplete()
        else {
            val disposable = Coil.load(context, url) {
                target { emitter.onSuccess((it as BitmapDrawable).bitmap) }
            }
            emitter.setCancellable { disposable.dispose() }
        }
    }

    private fun shareRoseInternally(bitmap: File): Completable = Completable.create {
        val share = Intent(Intent.ACTION_SEND).apply {
            val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", bitmap)
            setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_text_rose))
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        navigator.startActivity(Intent.createChooser(share, context.getString(R.string.share_title)))
        it.onComplete()
    }

    private fun cacheBitmapForShare(bitmap: Bitmap): Single<File> = Single.create { emitter ->
        try {
            FileOutputStream(tempFile).use {
                val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                if (success) emitter.onSuccess(tempFile)
                else emitter.onError(IOException())
            }
        } catch (e: IOException) {
            emitter.onError(e)
        }
    }
}
