package org.quantumbadger.redreader.history

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

@Database(entities = [HistoryEntry::class], version = 1, exportSchema = false)
abstract class HistoryDatabase : RoomDatabase() {
	abstract fun historyDao(): HistoryDao

	companion object {
		@Volatile
		private var INSTANCE: HistoryDatabase? = null
		fun getDatabase(context: Context): HistoryDatabase {
			return INSTANCE ?: synchronized(this) {
				val instance = Room.databaseBuilder(
					context.applicationContext,
					HistoryDatabase::class.java,
					"history"
				).build()
				INSTANCE = instance
				instance
			}
		}
	}
}
