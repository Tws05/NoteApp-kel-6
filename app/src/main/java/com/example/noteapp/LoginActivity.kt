package com.example.noteapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat

class LoginActivity : AppCompatActivity() {
    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var registerText: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Periksa apakah sudah ada sesi pengguna
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("USER_ID", -1)
        if (userId != -1) {
            // Jika ada sesi, langsung arahkan ke MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)
        registerText = findViewById(R.id.signupText)

        loginButton.setOnClickListener {
            val inputEmail = username.text.toString()
            val inputPassword = password.text.toString()

            val userRepository = UserRepository(this)
            val user = userRepository.getUserByEmail(inputEmail)

            if (user != null && user.password == inputPassword) {
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                // Simpan sesi pengguna setelah login berhasil
                val editor = sharedPreferences.edit()
                editor.putInt("USER_ID", user.id) // Menyimpan ID pengguna
                editor.putString("USER_NAME", user.name) // Menyimpan nama pengguna
                editor.putString("USER_EMAIL", user.email) // Menyimpan email pengguna
                editor.apply()

                // Tampilkan notifikasi "Selamat Datang"
                showWelcomeNotification(user.name)

                // Kirim data USER_NAME dan USER_EMAIL ke MainActivity
                val intent = Intent(this, MainActivity::class.java).apply {
                    putExtra("USER_NAME", user.name)
                    putExtra("USER_EMAIL", user.email)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Login Failed! Invalid credentials.", Toast.LENGTH_SHORT).show()
            }
        }

        registerText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showWelcomeNotification(userName: String) {
        val channelId = "login_notification_channel"
        val channelName = "Login Notification"
        val notificationId = 1

        // Buat NotificationManager
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Untuk Android Oreo ke atas, buat NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Buat notifikasi
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_icon) // Ganti dengan ikon notifikasi Anda
            .setContentTitle("Selamat Datang")
            .setContentText("Halo, $userName! Selamat datang di NoteApp.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Tampilkan notifikasi
        notificationManager.notify(notificationId, notification)
    }
}
