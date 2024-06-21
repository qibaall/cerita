package com.example.cerita
import com.example.cerita.data.response.ListStoryItem

object DummyData {

    fun generateDummyStories(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val stories = ListStoryItem(
                id = "Id $i",
                name = "Story Name $i",
                description = "Description $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/$i",
                createdAt = "createdAt $i",
                lat = 40.7434,
                lon = 74.0080,
            )
            items.add(stories)
        }
        return items
    }

}