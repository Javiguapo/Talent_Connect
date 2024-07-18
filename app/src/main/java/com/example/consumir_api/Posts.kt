package com.example.consumir_api

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Posts : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddPost: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_my_posts, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        fabAddPost = view.findViewById(R.id.fabAddPost)
        fabAddPost.setOnClickListener {
            // Iniciar la actividad NewPost al hacer clic en el botón
            startActivity(Intent(activity, NewPost::class.java))
        }

        return view
    }

    // Aquí podrías agregar la lógica para cargar los posts en el RecyclerView
}


