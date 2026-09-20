package org.quantumbadger.redreader.history

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy.Companion.REPLACE
import androidx.room3.Query
import org.quantumbadger.redreader.reddit.kthings.RedditPost

@Dao
interface HistoryDao {

	@Query("SELECT * FROM history")
	fun getRecentPosts(): MutableList<RedditPost>

	@Insert(onConflict = REPLACE)
	fun insert(historyEntry: HistoryEntry)
}
