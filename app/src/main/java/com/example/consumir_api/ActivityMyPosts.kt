package com.example.consumir_api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.consumir_api.databinding.ActivityMyPostsBinding

class ActivityMyPosts : AppCompatActivity(), PostAdapter.OnItemClickListener {

    private lateinit var binding: ActivityMyPostsBinding
    private lateinit var requestQueue: RequestQueue

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyPostsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestQueue = Volley.newRequestQueue(this)

        binding.recyclerViewPosts.layoutManager = LinearLayoutManager(this)
        fetchMyPosts()

        binding.fabAddPost.setOnClickListener {
            startActivity(Intent(this, NewPost::class.java))
        }
    }

    private fun fetchMyPosts() {
        val url = "http://192.168.0.9:8000/api/my-posts" // Cambia esto a tu URL de mis posts

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                val posts = mutableListOf<Post>()
                val dataArray = response.getJSONArray("data")
                for (i in 0 until dataArray.length()) {
                    val postJson = dataArray.getJSONObject(i)
                    val attributes = postJson.getJSONObject("attributes")
                    val categoryJson = postJson.getJSONObject("relationships").getJSONObject("category").getJSONObject("data").getJSONObject("attributes")
                    posts.add(Post(
                        id = postJson.getString("id"),
                        title = attributes.getString("title"),
                        message = attributes.getString("message"),
                        category = categoryJson.getString("name"),
                        createdAt = attributes.getString("created_at")
                    ))
                }
                binding.recyclerViewPosts.adapter = PostAdapter(posts, this@ActivityMyPosts)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error fetching posts: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("ActivityMyPosts", "Error fetching posts", error)
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = getToken()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    override fun onEditClick(post: Post) {
        TODO("Not yet implemented")
    }

//    override fun onEditClick(post: Post) {
//        val intent = Intent(this, EditPostActivity::class.java)
//        intent.putExtra("postId", post.id)
//        startActivity(intent)
//    }

    override fun onDeleteClick(post: Post) {
        deletePost(post.id)
    }

    private fun deletePost(postId: String) {
        val url = "http://192.168.0.9:8000/api/posts/$postId"

        val jsonObjectRequest = object : JsonObjectRequest(
            Request.Method.DELETE, url, null,
            Response.Listener {
                Toast.makeText(this, "Post deleted successfully", Toast.LENGTH_SHORT).show()
                fetchMyPosts() // Refresh posts
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error deleting post: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("ActivityMyPosts", "Error deleting post", error)
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val token = getToken()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        requestQueue.add(jsonObjectRequest)
    }
}
