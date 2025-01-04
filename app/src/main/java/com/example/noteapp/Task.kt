package com.example.noteapp

data class Task(
    val id: Long,
    val name: String,
    val startTime: String,
    val endTime: String,
    val category: String,
    val details: String,
    val color: String
)
