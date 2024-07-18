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

class ActivityLogin : AppCompatActivity() {

    private lateinit var emailLogin: EditText
    private lateinit var passwordLogin: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var signUpTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login) // Asegúrate de que esto coincida con tu archivo XML

        emailLogin = findViewById(R.id.username)
        passwordLogin = findViewById(R.id.password)
        loginButton = findViewById(R.id.Login_botton)
        forgotPasswordTextView = findViewById(R.id.forgot_password)
        signUpTextView = findViewById(R.id.sign_up)

        loginButton.setOnClickListener {
            if (validateInputs()) {
                loginUser()
            }
        }

        forgotPasswordTextView.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        signUpTextView.setOnClickListener {
            startActivity(Intent(this, ActivityRegister::class.java))
        }
    }

    private fun validateInputs(): Boolean {
        val email = emailLogin.text.toString().trim()
        val pass = passwordLogin.text.toString().trim()

        var isValid = true

        if (TextUtils.isEmpty(email)) {
            emailLogin.error = "Email is required"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLogin.error = "Enter a valid email"
            isValid = false
        } else {
            emailLogin.error = null
        }

        if (TextUtils.isEmpty(pass)) {
            passwordLogin.error = "Password is required"
            isValid = false
        } else {
            passwordLogin.error = null
        }

        return isValid
    }

    private fun loginUser() {
        val email = emailLogin.text.toString().trim()
        val pass = passwordLogin.text.toString().trim()

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.9:8000/api/login" // Cambia esto a tu URL de login

        val jsonBody = JSONObject()
        try {
            val attributes = JSONObject().apply {
                put("email", email)
                put("password", pass)
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
                    Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                    // Redirigir a la ventana principal
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Login failed!", Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Login failed! ${error.message}", Toast.LENGTH_SHORT).show()
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
