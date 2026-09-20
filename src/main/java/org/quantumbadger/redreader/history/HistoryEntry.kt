package org.quantumbadger.redreader.history

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import org.quantumbadger.redreader.reddit.kthings.RedditPost

@Entity(tableName = "history")
data class HistoryEntry(
	@PrimaryKey val id: String,
	@ColumnInfo(name = "viewedAt") val viewedAt: Long,
	@ColumnInfo(name = "reddit_post_json") val redditPostJson: String
)
