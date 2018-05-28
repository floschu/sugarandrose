package org.sugarandrose.app.data.model.remote

import android.support.v7.util.DiffUtil
import org.threeten.bp.ZonedDateTime

/**
 * Created by Florian Schuster
 * florian.schuster@tailored-apps.com
 */

class Post(val id: Long,
           val date: ZonedDateTime,
           val title: DisplayText,
           val link: String,
           val better_featured_image: FeaturedImage,
           val content: DisplayText?
) {
    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<Post> = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
        }
    }
}