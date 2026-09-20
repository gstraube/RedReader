/*******************************************************************************
 * This file is part of RedReader.
 *
 * RedReader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * RedReader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RedReader.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package org.quantumbadger.redreader.common

import android.content.Context
import android.os.Parcel
import android.util.Base64
import org.quantumbadger.redreader.history.HistoryDatabase
import org.quantumbadger.redreader.history.HistoryEntry
import org.quantumbadger.redreader.reddit.kthings.RedditPost
import org.quantumbadger.redreader.reddit.prepared.RedditPreparedPost

object HistoryManager {

	@JvmStatic
	fun get(context: Context): MutableList<RedditPost> {
		val historyDao = HistoryDatabase.getDatabase(context).historyDao()
		return historyDao.getRecentPosts()
	}

	@JvmStatic
    fun add(
        context: Context,
        post: RedditPreparedPost
    ) {
		val historyDao = HistoryDatabase.getDatabase(context).historyDao()
		historyDao.insert(HistoryEntry(post.src.idAlone, post.src.src))
    }

    private fun encodePost(post: RedditPost): String {
        val parcel = Parcel.obtain()
        try {
            post.writeToParcel(parcel, 0)
            return Base64.encodeToString(parcel.marshall(), Base64.NO_WRAP)
        } finally {
            parcel.recycle()
        }
    }
}
