package com.example.consumir_api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.consumir_api.databinding.FragmentSettingsBinding
import org.json.JSONObject

class NewPost : AppCompatActivity() {

    private lateinit var binding: FragmentSettingsBinding
    private var categories: MutableList<Category> = mutableListOf()
    private lateinit var selectedCategory: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchCategories()

        binding.submitBtn.setOnClickListener {
            createPost()
        }

        binding.backButton.setOnClickListener {
            finish() // Termina la actividad actual para regresar a la anterior
        }
    }

    private fun fetchCategories() {
        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.9:8000/api/categories" // Cambia esto a tu URL de categorías

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            Response.Listener { response ->
                val dataArray = response.getJSONArray("data")
                for (i in 0 until dataArray.length()) {
                    val categoryJson = dataArray.getJSONObject(i)
                    val attributes = categoryJson.getJSONObject("attributes")
                    categories.add(Category(
                        id = categoryJson.getInt("id"),
                        name = attributes.getString("name")
                    ))
                }
                setupCategorySpinner()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error fetching categories: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("NewPost", "Error fetching categories", error)
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun createPost() {
        val title = binding.titleNP.text.toString().trim()
        val message = binding.content.text.toString().trim()
        val categoryId = selectedCategory.id

        if (title.isEmpty() || message.isEmpty()) {
            Toast.makeText(this, "Title and message are required", Toast.LENGTH_SHORT).show()
            return
        }

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.9:8000/api/posts" // Cambia esto a tu URL de posts

        val jsonBody = JSONObject()
        try {
            jsonBody.put("title", title)
            jsonBody.put("message", message)
            jsonBody.put("category_id", categoryId)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonBody,
            Response.Listener { response ->
                Toast.makeText(this, "Post created successfully!", Toast.LENGTH_SHORT).show()
                // Redirigir a la ventana de ActivityMyPosts
                val intent = Intent(this, ActivityMyPosts::class.java)
                startActivity(intent)
                finish()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error creating post: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("NewPost", "Error creating post", error)
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
}

data class Category(val id: Int, val name: String)
