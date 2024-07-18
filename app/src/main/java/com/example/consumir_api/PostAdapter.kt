package com.example.consumir_api

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val posts: List<Post>, private val listener: OnItemClickListener) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    interface OnItemClickListener {
        fun onEditClick(post: Post)
        fun onDeleteClick(post: Post)
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postTitle: TextView = itemView.findViewById(R.id.postTitle)
        val postMessage: TextView = itemView.findViewById(R.id.postMessage)
        val postCategory: TextView = itemView.findViewById(R.id.postCategory)
        val postCreatedAt: TextView = itemView.findViewById(R.id.postCreatedAt)
        val editButton: Button = itemView.findViewById(R.id.button_edit)
        val deleteButton: Button = itemView.findViewById(R.id.button_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.postTitle.text = post.title
        holder.postMessage.text = post.message
        holder.postCategory.text = post.category
        holder.postCreatedAt.text = post.createdAt

        holder.editButton.setOnClickListener {
            listener.onEditClick(post)
        }

        holder.deleteButton.setOnClickListener {
            listener.onDeleteClick(post)
        }
    }

    override fun getItemCount() = posts.size
}
