package com.example.noteapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.content.SharedPreferences

class UserRepository(context: Context) {

    private val dbHelper = UserDatabaseHelper(context)
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_pref", Context.MODE_PRIVATE)

    // Menambahkan pengguna baru ke database
    fun addUser(name: String, email: String, password: String): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserDatabaseHelper.COLUMN_NAME, name)
            put(UserDatabaseHelper.COLUMN_EMAIL, email)
            put(UserDatabaseHelper.COLUMN_PASSWORD, password)
        }
        val result = db.insert(UserDatabaseHelper.TABLE_USERS, null, values)
        db.close()
        return result
    }

    // Mendapatkan pengguna berdasarkan ID
    fun getUserById(userId: Int): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor? = db.query(
            UserDatabaseHelper.TABLE_USERS,
            null,
            "${UserDatabaseHelper.COLUMN_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor != null && cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PASSWORD))
            )
        }
        cursor?.close()
        db.close()
        return user
    }

    // Mendapatkan pengguna berdasarkan email
    fun getUserByEmail(email: String): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserDatabaseHelper.TABLE_USERS,
            null,
            "${UserDatabaseHelper.COLUMN_EMAIL} = ?",
            arrayOf(email),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PASSWORD))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    // Mendapatkan pengguna berdasarkan nama
    fun getUserByName(userName: String): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserDatabaseHelper.TABLE_USERS,
            null,
            "${UserDatabaseHelper.COLUMN_NAME} = ?",
            arrayOf(userName),
            null,
            null,
            null
        )

        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_NAME)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_EMAIL)),
                password = cursor.getString(cursor.getColumnIndexOrThrow(UserDatabaseHelper.COLUMN_PASSWORD))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    // Validasi pengguna berdasarkan email dan password
    fun validateUser(email: String, password: String): Boolean {
        val user = getUserByEmail(email)
        return user != null && user.password == password
    }

    // Menyimpan ID pengguna yang sedang login di SharedPreferences
    fun saveUserSession(userId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("USER_ID", userId)
        editor.apply()
    }

    // Mendapatkan ID pengguna yang sedang login dari SharedPreferences
    fun getUserSession(): Int {
        return sharedPreferences.getInt("USER_ID", -1) // -1 jika tidak ada session
    }

    // Menghapus sesi pengguna (logout)
    fun clearUserSession() {
        val editor = sharedPreferences.edit()
        editor.remove("USER_ID")
        editor.apply()
    }
}
