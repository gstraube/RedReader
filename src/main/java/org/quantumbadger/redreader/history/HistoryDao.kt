package org.quantumbadger.redreader.history

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy.Companion.REPLACE
import androidx.room3.Query
import org.quantumbadger.redreader.reddit.kthings.RedditPost

@Dao
interface HistoryDao {

	@Query("SELECT * FROM history ORDER BY viewedAt DESC LIMIT 20")
	fun getRecentPosts(): List<HistoryEntry>

	@Insert(onConflict = REPLACE)
	fun insert(historyEntry: HistoryEntry)

	@Query("DELETE FROM history WHERE id NOT IN (SELECT id FROM history ORDER BY viewedAt DESC LIMIT 20)")
	fun pruneToLimit()
}
