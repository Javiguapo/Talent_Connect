package com.example.consumir_api

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
import com.example.consumir_api.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura el botón de regreso
        val backButton: ImageButton = findViewById(R.id.back_button)
        backButton.setOnClickListener {
            onBackPressed()  // Esto regresa a la actividad anterior
        }
    }
}
