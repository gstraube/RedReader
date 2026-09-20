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
 * along with RedReader.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.quantumbadger.redreader.common;

import android.content.Context;
import android.os.Parcel;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;
import org.quantumbadger.redreader.reddit.prepared.RedditPreparedPost;
import org.quantumbadger.redreader.reddit.kthings.RedditPost;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class RecentlyViewedPosts {

	private static final String TAG = "RecentPosts";
	private static final String PREF_KEY = "recently_viewed_posts";
	private static final int MAX_POSTS = 20;

	private RecentlyViewedPosts() {
	}

	public static final class Item {
		@NonNull
		public final String id;
		@NonNull
		public final String title;
		@NonNull
		public final String subreddit;
		@NonNull
		public final String permalink;
		@NonNull
		public final RedditPost post;

		private Item(
				@NonNull final String id,
				@NonNull final String title,
				@NonNull final String subreddit,
				@NonNull final String permalink,
				@NonNull final RedditPost post) {
			this.id = id;
			this.title = title;
			this.subreddit = subreddit;
			this.permalink = permalink;
			this.post = post;
		}
	}

	@NonNull
	public static List<Item> get(@NonNull final Context context) {
		final String serialized = General.getSharedPrefs(context).getString(PREF_KEY, null);
		final ArrayList<Item> result = new ArrayList<>();

		if (serialized == null) {
			return result;
		}

		try {
			final JSONArray posts = new JSONArray(serialized);
			for (int i = 0; i < posts.length(); i++) {
				final JSONObject post = posts.getJSONObject(i);
				final String encodedPost = post.optString("post", null);
				if (encodedPost == null) {
					continue;
				}

				final Parcel parcel = Parcel.obtain();
				try {
					final byte[] bytes = Base64.decode(encodedPost, Base64.DEFAULT);
					parcel.unmarshall(bytes, 0, bytes.length);
					parcel.setDataPosition(0);
					final RedditPost redditPost = RedditPost.CREATOR.createFromParcel(parcel);
					result.add(new Item(
							redditPost.getId(),
							redditPost.getTitle() == null
									? "[null]"
									: redditPost.getTitle().getDecoded(),
							redditPost.getSubreddit().getDecoded(),
							redditPost.getPermalink().getDecoded(),
							redditPost));
				} finally {
					parcel.recycle();
				}
			}
		} catch (final Exception e) {
			Log.w(TAG, "Failed to read recently viewed posts", e);
		}

		return result;
	}

	public static void add(
			@NonNull final Context context,
			@NonNull final RedditPreparedPost post) {

		final ArrayList<Item> posts = new ArrayList<>();
		final String id = post.src.getIdAlone();
		posts.add(new Item(
				id,
				post.src.getTitle(),
				post.src.getSubreddit(),
				post.src.getPermalink(),
				post.src.getSrc()));

		for (final Item item : get(context)) {
			if (!item.id.equals(id)) {
				posts.add(item);
			}
			if (posts.size() == MAX_POSTS) {
				break;
			}
		}

		final JSONArray serialized = new JSONArray();
		try {
			for (final Item item : posts) {
				serialized.put(new JSONObject()
						.put("id", item.id)
						.put("title", item.title)
						.put("subreddit", item.subreddit)
						.put("permalink", item.permalink)
						.put("post", encodePost(item.post)));
			}
		} catch (final Exception e) {
			Log.w(TAG, "Failed to write recently viewed posts", e);
			return;
		}

		General.getSharedPrefs(context).edit().putString(PREF_KEY, serialized.toString()).apply();
	}

	@NonNull
	private static String encodePost(@NonNull final RedditPost post) {
		final Parcel parcel = Parcel.obtain();
		try {
			post.writeToParcel(parcel, 0);
			return Base64.encodeToString(parcel.marshall(), Base64.NO_WRAP);
		} finally {
			parcel.recycle();
		}
	}

}
