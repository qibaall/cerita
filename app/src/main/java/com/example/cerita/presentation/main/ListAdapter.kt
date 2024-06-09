package com.example.cerita.presentation.main

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cerita.data.response.ListStoryItem
import com.example.cerita.databinding.ItemListBinding
import com.example.cerita.presentation.detail.StoryDetailActivity

class ListAdapter(private var stories: List<ListStoryItem>) : RecyclerView.Adapter<ListAdapter.StoryViewHolder>() {

    inner class StoryViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            with(binding) {
                tvItemName.text = story.name
                tvTime.text = story.createdAt
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(imgPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, StoryDetailActivity::class.java)
                    intent.putExtra("storyName", story.name)
                    intent.putExtra("storyTime", story.createdAt)
                    intent.putExtra("storyDescription", story.description)
                    intent.putExtra("storyPhotoUrl", story.photoUrl)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateStories(newStories: List<ListStoryItem>) {
        stories = newStories
        notifyDataSetChanged()
    }
}

