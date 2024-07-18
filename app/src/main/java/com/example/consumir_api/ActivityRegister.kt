package com.example.consumir_api

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class ActivityRegister : AppCompatActivity() {

    private lateinit var nameRegister: EditText
    private lateinit var emailRegister: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var registerButton: Button
    private lateinit var loginHereTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registerins)

        nameRegister = findViewById(R.id.name_register)
        emailRegister = findViewById(R.id.email_register)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        registerButton = findViewById(R.id.register_button)
        loginHereTextView = findViewById(R.id.login_here)

        registerButton.setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }

        loginHereTextView.setOnClickListener {
            val intent = Intent(this, ActivityLogin::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(): Boolean {
        val name = nameRegister.text.toString().trim()
        val email = emailRegister.text.toString().trim()
        val pass = password.text.toString().trim()
        val confirmPass = confirmPassword.text.toString().trim()

        var isValid = true

        if (TextUtils.isEmpty(name)) {
            nameRegister.error = "Name is required"
            isValid = false
        } else {
            nameRegister.error = null
        }

        if (TextUtils.isEmpty(email)) {
            emailRegister.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegister.error = "Enter a valid email"
            isValid = false
        } else {
            emailRegister.error = null
        }

        if (TextUtils.isEmpty(pass)) {
            password.error = "Password is required"
            isValid = false
        } else {
            if (pass.length < 8) {
                password.error = "Password must be at least 8 characters"
                isValid = false
            } else if (!pass.any { it.isDigit() }) {
                password.error = "Password must contain at least one digit"
                isValid = false
            } else if (!pass.any { it.isUpperCase() }) {
                password.error = "Password must contain at least one uppercase letter"
                isValid = false
            } else if (!pass.any { it.isLowerCase() }) {
                password.error = "Password must contain at least one lowercase letter"
                isValid = false
            } else {
                password.error = null
            }
        }

        if (pass != confirmPass) {
            confirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            confirmPassword.error = null
        }

        return isValid
    }

    private fun registerUser() {
        val name = nameRegister.text.toString().trim()
        val email = emailRegister.text.toString().trim()
        val pass = password.text.toString().trim()

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.9:8000/api/register" // Cambia esto a tu URL de registro

        val jsonBody = JSONObject()
        try {
            val attributes = JSONObject().apply {
                put("name", name)
                put("email", email)
                put("password", pass)
                put("password_confirmation", pass)
            }

            jsonBody.put("data", JSONObject().put("attributes", attributes))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, jsonBody,
            Response.Listener { response ->
                try {
                    val token = response.getString("token")
                    saveToken(token) // Guardar el token en SharedPreferences
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    // Redirigir a la ventana principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Registration failed! ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(jsonObjectRequest)
    }

    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }
}
