package org.sugarandrose.app.util

import android.content.Context
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.data.model.LocalPost
import javax.inject.Inject
import android.content.Intent
import org.sugarandrose.app.ui.base.navigator.Navigator
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.support.v4.content.FileProvider
import io.reactivex.Completable
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerActivity
import org.sugarandrose.app.util.extensions.loadWithPicasso
import io.reactivex.Single
import org.sugarandrose.app.R

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

@PerActivity
class ShareManager @Inject
constructor(@ActivityContext private val context: Context, private val navigator: Navigator) {

    private val cachedName = "shareimage.jpg"
    private val tempFile: File by lazy { File(context.cacheDir, cachedName) }

    fun sharePost(item: LocalPost): Completable =
            if (item.image != null) context.loadWithPicasso(item.image)
                    .flatMap(this::cacheBitmapForShare)
                    .flatMapCompletable { sharePostInternally(item.title, item.url, it) }
            else sharePostInternally(item.title, item.url)

    private fun sharePostInternally(title: String, url: String, file: File? = null): Completable = Completable.create {
        val share = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, url)
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

    fun shareMedia(item: LocalMedia): Completable =
            context.loadWithPicasso(item.image)
                    .flatMap(this::cacheBitmapForShare)
                    .flatMapCompletable(this::shareMediaInternally)

    private fun shareMediaInternally(bitmap: File): Completable = Completable.create {
        val share = Intent(Intent.ACTION_SEND).apply {
            val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", bitmap)
            setDataAndType(contentUri, context.contentResolver.getType(contentUri))
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