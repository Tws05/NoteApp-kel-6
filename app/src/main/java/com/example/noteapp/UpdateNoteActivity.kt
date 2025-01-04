package com.example.noteapp

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class UpdateNoteActivity : AppCompatActivity() {

    private lateinit var etTaskName: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etDetails: EditText
    private lateinit var rgColors: RadioGroup
    private lateinit var btnUpdate: Button
    private lateinit var dbHelper: TaskDatabaseHelper
    private var taskId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_note)

        dbHelper = TaskDatabaseHelper(this)

        // Initialize views
        etTaskName = findViewById(R.id.et_task_name)
        etStartTime = findViewById(R.id.et_start_time)
        etEndTime = findViewById(R.id.et_end_time)
        spCategory = findViewById(R.id.sp_task_category)
        etDetails = findViewById(R.id.et_details)
        rgColors = findViewById(R.id.rg_colors)
        btnUpdate = findViewById(R.id.btn_save)

        // Set up category Spinner
        val categories = arrayOf("Choose Category", "Study", "Home", "Work", "Other")
        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getView(position, convertView, parent) as TextView
                // Set text color for selected item
                if (position == 0) {
                    view.setTextColor(resources.getColor(android.R.color.darker_gray))
                } else {
                    view.setTextColor(resources.getColor(android.R.color.black))
                }
                return view
            }

            override fun getDropDownView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
                val view = super.getDropDownView(position, convertView, parent) as TextView
                view.setTextColor(resources.getColor(android.R.color.darker_gray))
                return view
            }

            override fun isEnabled(position: Int): Boolean {
                // Disable "Choose Category" option
                return position != 0
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCategory.adapter = adapter

        // Get task ID from intent
        taskId = intent.getLongExtra("TASK_ID", -1)
        if (taskId != -1L) {
            loadTaskDetails(taskId)
        } else {
            Toast.makeText(this, "Invalid task ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Set listeners
        btnUpdate.setOnClickListener {
            updateTask()
        }

        etStartTime.setOnClickListener {
            showTimePicker(etStartTime, etStartTime.text.toString())
        }

        etEndTime.setOnClickListener {
            showTimePicker(etEndTime, etEndTime.text.toString())
        }
    }

    private fun loadTaskDetails(taskId: Long) {
        val task = dbHelper.getTaskById(taskId)

        task?.let {
            etTaskName.setText(it.name)
            etStartTime.setText(it.startTime)
            etEndTime.setText(it.endTime)
            spCategory.setSelection(getSpinnerIndex(spCategory, it.category))
            etDetails.setText(it.details)

            // Set the color radio button based on the stored task color
            for (i in 0 until rgColors.childCount) {
                val radioButton = rgColors.getChildAt(i) as? RadioButton
                if (radioButton?.tag == it.color) {
                    radioButton.isChecked = true
                    break
                }
            }
        } ?: run {
            Toast.makeText(this, "Task not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateTask() {
        val name = etTaskName.text.toString().trim()
        val startTime = etStartTime.text.toString().trim()
        val endTime = etEndTime.text.toString().trim()
        val category = spCategory.selectedItem.toString()
        val details = etDetails.text.toString().trim()
        val selectedColorId = rgColors.checkedRadioButtonId

        if (name.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank() && selectedColorId != -1) {
            // Convert radio button ID to color
            val selectedColor = when (selectedColorId) {
                R.id.color_satu -> "#FF24FC"  // Pink
                R.id.color_dua -> "#00394D"  // Dark Blue
                R.id.color_tiga -> "#004F9F"  // Blue
                R.id.color_empat -> "#6FAE34"  // Green
                R.id.color_lima -> "#56E0A0"  // Light Green
                R.id.color_enam -> "#FF7070"  // Light Red
                R.id.color_tujuh -> "#F9A825"  // Yellow
                R.id.color_delapan -> "#6E72FA"  // Purple
                else -> "#FFFFFF"  // Default to white if no color is selected
            }

            // Create updated Task object
            val updatedTask = Task(
                id = taskId,
                name = name,
                startTime = startTime,
                endTime = endTime,
                category = category,
                details = details,
                color = selectedColor
            )

            // Update task in database
            val result = dbHelper.updateTask(updatedTask)
            if (result > 0) {
                Toast.makeText(this, "Task updated successfully", Toast.LENGTH_SHORT).show()
                finish()  // Return to the previous screen
            } else {
                Toast.makeText(this, "Failed to update task", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getSpinnerIndex(spinner: Spinner, value: String): Int {
        for (i in 0 until spinner.count) {
            if (spinner.getItemAtPosition(i).toString().equals(value, ignoreCase = true)) {
                return i
            }
        }
        return 0  // Default to first item if not found
    }

    private fun showTimePicker(editText: EditText, defaultTime: String) {
        val calendar = Calendar.getInstance()

        // Ensure defaultTime is in "HH:mm" format and split it
        val timeParts = defaultTime.split(":")
        val hour = if (timeParts.size == 2) timeParts[0].toIntOrNull() ?: calendar.get(Calendar.HOUR_OF_DAY) else calendar.get(Calendar.HOUR_OF_DAY)
        val minute = if (timeParts.size == 2) timeParts[1].toIntOrNull() ?: calendar.get(Calendar.MINUTE) else calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Set the selected time to the EditText
            editText.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute))
        }, hour, minute, true).show()
    }
}

