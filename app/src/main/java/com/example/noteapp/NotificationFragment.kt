package com.example.noteapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class NotificationFragment : Fragment() {

    // Data notifikasi
    private val notifications = mutableListOf<NotifItem>()

    // Inisialisasi RecyclerView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        // Ambil nama pengguna dari SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "Guest") ?: "Guest"

        // Ambil daftar notifikasi yang sudah dihapus
        val removedNotifications = sharedPreferences.getStringSet("REMOVED_NOTIFICATIONS", mutableSetOf()) ?: mutableSetOf()

        // Tambahkan notifikasi jika belum dihapus
        val notifWelcome = NotifItem(
            "Selamat datang, $userName! :)",
            "Jangan sampai lupa semua tugas-tugasmu ya",
            "WELCOME",  // ID unik
            "Geser untuk menghapus"  // Contoh waktu
        )

        if (!removedNotifications.contains(notifWelcome.id)) {
            notifications.add(notifWelcome)
        }

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = view.findViewById(R.id.recyclerViewNotifications)
        adapter = NotificationAdapter(notifications)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Tambahkan fitur swipe to dismiss menggunakan ItemTouchHelper
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Tidak mengizinkan perpindahan item
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Hapus notifikasi dari list
                val position = viewHolder.adapterPosition
                val removedNotification = notifications[position]

                // Tambahkan notifikasi ke daftar yang dihapus di SharedPreferences
                removedNotifications.add(removedNotification.id)
                sharedPreferences.edit()
                    .putStringSet("REMOVED_NOTIFICATIONS", removedNotifications)
                    .apply()

                // Hapus dari list dan beri tahu adapter
                notifications.removeAt(position)
                adapter.notifyItemRemoved(position)
            }
        })
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }
}
