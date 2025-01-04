package com.example.noteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {

    private lateinit var fullName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var registerButton: Button
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi View
        fullName = findViewById(R.id.fullName)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        registerButton = findViewById(R.id.registerButton)
        loginText = findViewById(R.id.loginText)

        val dbHelper = UserDatabaseHelper(this)

        // Fungsi untuk tombol Register
        registerButton.setOnClickListener {
            val inputName = fullName.text.toString()
            val inputEmail = email.text.toString()
            val inputPassword = password.text.toString()

            if (inputName.isNotEmpty() && inputEmail.isNotEmpty() && inputPassword.isNotEmpty()) {
                val writableDb = dbHelper.writableDatabase
                writableDb.execSQL(
                    "INSERT INTO ${UserDatabaseHelper.TABLE_USERS} (${UserDatabaseHelper.COLUMN_NAME}, ${UserDatabaseHelper.COLUMN_EMAIL}, ${UserDatabaseHelper.COLUMN_PASSWORD}) " +
                            "VALUES (?, ?, ?)",
                    arrayOf(inputName, inputEmail, inputPassword)
                )

                Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Fungsi untuk tombol Login
        loginText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}