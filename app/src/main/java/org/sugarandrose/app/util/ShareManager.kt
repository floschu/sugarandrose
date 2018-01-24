package org.sugarandrose.app.util

import android.content.Context
import org.sugarandrose.app.data.model.LocalMedia
import org.sugarandrose.app.data.model.LocalPost
import javax.inject.Inject
import android.content.Intent
import org.sugarandrose.app.ui.base.navigator.Navigator
import android.graphics.drawable.Drawable
import com.squareup.picasso.Picasso
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.support.v4.content.FileProvider
import com.squareup.picasso.Target
import org.sugarandrose.app.BuildConfig
import org.sugarandrose.app.injection.qualifier.ActivityContext
import org.sugarandrose.app.injection.scopes.PerActivity


/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */
@PerActivity
class ShareManager @Inject
constructor(@ActivityContext private val context: Context, private val navigator: Navigator) {

    fun share(item: LocalPost) {
        Picasso.with(context).load(item.image).into(object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
            override fun onBitmapFailed(errorDrawable: Drawable?) {}

            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom?) {
                saveImageInternally(bitmap) {
                    val contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", it)
                    val share = Intent(Intent.ACTION_SEND).apply {
                        setDataAndType(contentUri, context.contentResolver.getType(contentUri))
                        putExtra(Intent.EXTRA_TEXT, "Hey view/download this image")
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    navigator.startActivity(Intent.createChooser(share, "testesetest"))
                }
            }
        })
    }

    fun share(item: LocalMedia) {

    }

    private fun saveImageInternally(bitmap: Bitmap, callback: (File) -> Unit) { //todo rx this
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            FileOutputStream(cachePath.toString() + "/image.jpg").use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                bitmap.recycle()
                callback.invoke(File(cachePath, "image.jpg"))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}