package com.example.noteapp

data class NotifItem(
    val title: String,
    val description: String,
    val id: String,  // ID unik untuk setiap notifikasi
    val time: String // Waktu notifikasi
)

