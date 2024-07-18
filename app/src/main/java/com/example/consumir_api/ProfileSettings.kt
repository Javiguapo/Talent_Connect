package com.example.consumir_api

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileSettings : AppCompatActivity() {
    private lateinit var profileImageView: ImageView

    // Register for the activity result
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            profileImageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        // Set up edge-to-edge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        profileImageView = findViewById(R.id.profile_imageIconChange)

        // Set an OnClickListener to open the image picker
        findViewById<TextView>(R.id.profile_changeimage).setOnClickListener {
            openImagePicker()
        }

        // Set an OnClickListener to navigate to the Profile activity
        findViewById<LinearLayout>(R.id.profileSettingsLayout).setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        // Set an OnClickListener for the back button to return to the previous activity (Profile)
//        findViewById<ImageButton>(R.id.back_button).setOnClickListener {
//            finish() // This will close the current activity and return to the previous one
//        }
    }

    private fun openImagePicker() {
        // Launch the gallery to pick an image
        pickImageLauncher.launch("image/*")
    }
}
