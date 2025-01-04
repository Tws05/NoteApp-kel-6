package com.example.noteapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private var taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        // Set task data to views
        holder.tvTaskName.text = task.name
        holder.tvStartTime.text = task.startTime
        holder.tvEndTime.text = task.endTime
        holder.tvDetails.text = task.details

        // Create a new Drawable shape with the corner radius
        val background =
            ContextCompat.getDrawable(holder.itemView.context, R.drawable.item_background)

        // Set the background color dynamically
        try {
            val color = Color.parseColor(task.color)  // Parse the color string to a Color object
            background?.setColorFilter(color, PorterDuff.Mode.SRC_IN)  // Apply the color filter
        } catch (e: IllegalArgumentException) {
            // In case the color string is invalid, set a default color
            background?.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)  // Default color
        }

        // Apply the background with color filter
        holder.itemView.background = background

        holder.btnUpdate.setOnClickListener {
            val task = taskList[position]
            val intent = Intent(holder.btnUpdate.context, UpdateNoteActivity::class.java)
            intent.putExtra("TASK_ID", task.id)
            holder.btnUpdate.context.startActivity(intent)
        }
        holder.btnDelete.setOnClickListener {
            val task = taskList[position]
            val dbHelper = TaskDatabaseHelper(holder.itemView.context)
            dbHelper.deleteTask(task.id)  // Hapus dari database
            taskList = taskList.filter { it.id != task.id }  // Hapus dari daftar di UI
            notifyDataSetChanged()  // Perbarui RecyclerView
        }

        // Set Alarm untuk notifikasi 5 menit sebelum startTime
        scheduleNotification(holder.itemView.context, task)
    }

    override fun getItemCount() = taskList.size

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTaskName: TextView = itemView.findViewById(R.id.tv_judul)
        val tvStartTime: TextView = itemView.findViewById(R.id.tv_jamStart)
        val tvEndTime: TextView = itemView.findViewById(R.id.tv_jamEnd)
        val tvDetails: TextView = itemView.findViewById(R.id.tv_detail)
        val btnDelete: ImageView = itemView.findViewById(R.id.ivDelete)
        val btnUpdate: ImageView = itemView.findViewById(R.id.iv_edit)
    }

    fun updateTaskList(newTaskList: List<Task>) {
        taskList = newTaskList
        notifyDataSetChanged()  // Informing the adapter that the data has changed
    }

    private fun scheduleNotification(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent untuk notifikasi
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("TASK_NAME", task.name)
        }

        // PendingIntent untuk AlarmManager
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.hashCode(),  // Gunakan hashCode() untuk requestCode yang lebih aman
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE // Flag untuk menggantikan PendingIntent yang lama
        )

        // Konversi startTime ke format milidetik
        try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val fullStartTime = "$currentDate ${task.startTime}"
            val startTimeMillis = dateFormat.parse(fullStartTime)?.time ?: 0L

            // Waktu notifikasi (5 menit sebelum startTime)
            val notificationTime = startTimeMillis - 5 * 60 * 1000

            Log.d("TaskAdapter", "Current Time: ${System.currentTimeMillis()}, Notification Time: $notificationTime")

            if (notificationTime > System.currentTimeMillis()) {
                // Jadwalkan AlarmManager
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    notificationTime,
                    pendingIntent
                )
                Log.d("TaskAdapter", "Notification scheduled for task: ${task.name} at $notificationTime")
            } else {
                Log.w("TaskAdapter", "Notification time for task: ${task.name} is in the past.")
            }
        } catch (e: Exception) {
            Log.e("TaskAdapter", "Error parsing time for task: ${task.name}", e)
        }
    }


}
