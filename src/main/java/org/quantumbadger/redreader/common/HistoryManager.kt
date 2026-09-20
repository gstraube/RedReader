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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.quantumbadger.redreader.history.HistoryDatabase
import org.quantumbadger.redreader.history.HistoryEntry
import org.quantumbadger.redreader.reddit.kthings.RedditPost
import org.quantumbadger.redreader.reddit.prepared.RedditPreparedPost
import java.util.concurrent.Executors

object HistoryManager {
	private val executor = Executors.newSingleThreadExecutor()

	@JvmStatic
	fun get(context: Context): MutableList<RedditPost> {
		return executor.submit<List<HistoryEntry>> {
			HistoryDatabase.getDatabase(context).historyDao().getRecentPosts()
		}.get().map { entry ->
			Json.decodeFromString<RedditPost>(entry.redditPostJson)
		}.toMutableList()
	}

	@JvmStatic
	fun add(context: Context, post: RedditPreparedPost) {
		executor.execute {
			val historyDao = HistoryDatabase.getDatabase(context).historyDao()
			val entry = HistoryEntry(
				id = post.src.idAlone,
				viewedAt = System.currentTimeMillis(),
				redditPostJson = Json.encodeToString(post.src.src)
			)
			historyDao.insert(entry)
			historyDao.pruneToLimit()
		}
	}
}
