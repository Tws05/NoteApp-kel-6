import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.noteapp.R

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val channelId = "task_notification_channel"
        val channelName = "Task Notifications"
        val notificationId = System.currentTimeMillis().toInt()

        val taskName = intent.getStringExtra("TASK_NAME") ?: "Task"
        val taskDetails = intent.getStringExtra("TASK_DETAILS") ?: "No details"

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Buat NotificationChannel (hanya diperlukan untuk Android O dan yang lebih baru)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Tampilkan notifikasi
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification)  // Gunakan ikon notifikasi
            .setContentTitle("Pengingat Task")
            .setContentText("Task: $taskName\nDetails: $taskDetails")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
