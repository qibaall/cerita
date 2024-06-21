package com.example.cerita.presentation.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cerita.data.response.ListStoryItem
import com.example.cerita.databinding.ItemListBinding
import com.example.cerita.presentation.detail.StoryDetailActivity

class ListAdapter : PagingDataAdapter<ListStoryItem, ListAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class StoryViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            with(binding) {
                tvItemName.text = story.name
                tvTime.text = story.createdAt
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(imgPhoto)


                binding.root.setOnClickListener {
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

}
