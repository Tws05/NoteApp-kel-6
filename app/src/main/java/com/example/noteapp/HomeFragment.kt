package com.example.noteapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var dbHelper: TaskDatabaseHelper
    private lateinit var studyText: TextView
    private lateinit var homeText: TextView
    private lateinit var workText: TextView
    private lateinit var otherText: TextView

    private val defaultCategory = "Study"
    private lateinit var sharedPreferences: SharedPreferences
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        recyclerView = rootView.findViewById(R.id.rvTask)
        studyText = rootView.findViewById(R.id.study)
        homeText = rootView.findViewById(R.id.home)
        workText = rootView.findViewById(R.id.work)
        otherText = rootView.findViewById(R.id.other)
        val greetingText: TextView = rootView.findViewById(R.id.greetingText)
        val userNameText: TextView = rootView.findViewById(R.id.userNameText)
        val dateText: TextView = rootView.findViewById(R.id.dateText)
        // Set RecyclerView layout manager
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        // Initialize Database Helper
        dbHelper = TaskDatabaseHelper(requireContext())

        // Initialize Adapter
        taskAdapter = TaskAdapter(emptyList())
        recyclerView.adapter = taskAdapter

        // Set click listeners for category TextViews
        setCategoryClickListeners()

        // Display the task counts for all categories when the fragment is first created
        updateStatisticsForAllCategories()

        // Set the default category selection and load tasks for "Study"
        updateCategorySelection("Study")
        loadTasks("Study")

        // Mengambil nama dan email pengguna dari intent (gunakan requireActivity())
        val userName = sharedPreferences.getString("USER_NAME", "Guest") ?: "Guest"
        val userEmail = activity?.intent?.getStringExtra("USER_EMAIL") ?: "Email tidak ditemukan"
        userNameText.text = userName

        // Menentukan ucapan berdasarkan waktu saat ini
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        greetingText.text = when (hour) {
            in 4..11 -> "Selamat Pagi!"
            in 12..17 -> "Selamat Siang!"
            else -> "Selamat Malam!"
        }
        // Menampilkan tanggal hari ini
        val dateFormat = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        dateText.text = dateFormat.format(Date())
        return rootView
    }

    private fun updateStatisticsForAllCategories() {
        // Update the statistics for all categories, even if not selected
        studyText.text = "Study: ${dbHelper.getTaskCountByCategory("Study")}"
        homeText.text = "Home: ${dbHelper.getTaskCountByCategory("Home")}"
        workText.text = "Work: ${dbHelper.getTaskCountByCategory("Work")}"
        otherText.text = "Other: ${dbHelper.getTaskCountByCategory("Other")}"
    }

    private fun setCategoryClickListeners() {
        // Set OnClickListener for category TextViews
        studyText.setOnClickListener {
            updateCategorySelection("Study")
            loadTasks("Study")
        }

        homeText.setOnClickListener {
            updateCategorySelection("Home")
            loadTasks("Home")
        }

        workText.setOnClickListener {
            updateCategorySelection("Work")
            loadTasks("Work")
        }

        otherText.setOnClickListener {
            updateCategorySelection("Other")
            loadTasks("Other")
        }
    }

    private fun updateCategorySelection(selectedCategory: String) {
        // Reset background color for all categories
        studyText.setBackgroundResource(R.drawable.stat_background)
        homeText.setBackgroundResource(R.drawable.stat_background)
        workText.setBackgroundResource(R.drawable.stat_background)
        otherText.setBackgroundResource(R.drawable.stat_background)

        // Set background color for selected category
        when (selectedCategory) {
            "Study" -> studyText.setBackgroundResource(R.drawable.selected_background)
            "Home" -> homeText.setBackgroundResource(R.drawable.selected_background)
            "Work" -> workText.setBackgroundResource(R.drawable.selected_background)
            "Other" -> otherText.setBackgroundResource(R.drawable.selected_background)
        }
    }

    private fun loadTasks(category: String) {
        // Get tasks filtered by category
        val tasks = dbHelper.getTasksByCategory(category)
        taskAdapter.updateTaskList(tasks)

        // Update statistics for the selected category
        updateStatistics(category)
    }

    private fun updateStatistics(category: String) {
        // Update only the statistics for the selected category
        when (category) {
            "Study" -> studyText.text = "Study: ${dbHelper.getTaskCountByCategory("Study")}"
            "Home" -> homeText.text = "Home: ${dbHelper.getTaskCountByCategory("Home")}"
            "Work" -> workText.text = "Work: ${dbHelper.getTaskCountByCategory("Work")}"
            "Other" -> otherText.text = "Other: ${dbHelper.getTaskCountByCategory("Other")}"
        }
    }
}