package org.quantumbadger.redreader.history

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import org.quantumbadger.redreader.reddit.kthings.RedditPost

@Entity
data class HistoryEntry(
	@PrimaryKey val id: String,
	@ColumnInfo(name = "reddit_post") val redditPost: RedditPost
)
