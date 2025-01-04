package com.example.noteapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "task.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_START_TIME = "startTime"
        private const val COLUMN_END_TIME = "endTime"
        private const val COLUMN_CATEGORY = "category"
        private const val COLUMN_DETAILS = "details"
        private const val COLUMN_COLOR = "color"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_START_TIME TEXT,
                $COLUMN_END_TIME TEXT,
                $COLUMN_CATEGORY TEXT,
                $COLUMN_DETAILS TEXT,
                $COLUMN_COLOR TEXT
            )
        """.trimIndent()
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertTask(task: Task): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
            put(COLUMN_START_TIME, task.startTime)
            put(COLUMN_END_TIME, task.endTime)
            put(COLUMN_CATEGORY, task.category)
            put(COLUMN_DETAILS, task.details)
            put(COLUMN_COLOR, task.color)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getTasksByCategory(category: String): List<Task> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_CATEGORY = ?", arrayOf(category))
        val tasks = mutableListOf<Task>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME))
                val category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY))
                val details = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS))
                val color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR))
                tasks.add(Task(id, name, startTime, endTime, category, details, color))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tasks
    }

    fun getTaskCountByCategory(category: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME WHERE $COLUMN_CATEGORY = ?", arrayOf(category))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    fun getTaskById(taskId: Long): Task? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_ID = ?", arrayOf(taskId.toString()))
        var task: Task? = null
        if (cursor.moveToFirst()) {
            task = Task(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)),
                category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                details = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DETAILS)),
                color = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR))
            )
        }
        cursor.close()
        return task
    }

    // Update task in database
    fun updateTask(task: Task): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, task.name)
            put(COLUMN_START_TIME, task.startTime)
            put(COLUMN_END_TIME, task.endTime)
            put(COLUMN_CATEGORY, task.category)
            put(COLUMN_DETAILS, task.details)
            put(COLUMN_COLOR, task.color)
        }

        // Update task based on ID
        return db.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(task.id.toString()))
    }

    // Delete task from database
    fun deleteTask(taskId: Long): Int {
        val db = writableDatabase
        // Delete task based on ID
        return db.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(taskId.toString()))
    }
}