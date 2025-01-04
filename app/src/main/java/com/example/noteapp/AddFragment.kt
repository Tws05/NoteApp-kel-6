package com.example.noteapp

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import java.util.Calendar
import java.util.Locale

class AddFragment : Fragment() {

    private lateinit var etTaskName: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etDetails: EditText
    private lateinit var rgColors: RadioGroup
    private lateinit var btnCreate: Button
    private lateinit var dbHelper: TaskDatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_add, container, false)

        // Initialize views
        etTaskName = rootView.findViewById(R.id.et_task_name)
        etStartTime = rootView.findViewById(R.id.et_start_time)
        etEndTime = rootView.findViewById(R.id.et_end_time)
        spCategory = rootView.findViewById(R.id.sp_task_category)
        etDetails = rootView.findViewById(R.id.et_details)
        rgColors = rootView.findViewById(R.id.rg_colors)
        btnCreate = rootView.findViewById(R.id.btn_create)

        // Initialize database helper
        dbHelper = TaskDatabaseHelper(requireContext()) // Use requireContext() for context

        etStartTime.setOnClickListener { showTimePicker(etStartTime) }
        etEndTime.setOnClickListener { showTimePicker(etEndTime) }

        // Add category items
        val categories = arrayOf("Choose Category", "Study", "Home", "Work", "Other")
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, categories) {
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

        // Handle Create button click
        btnCreate.setOnClickListener {
            val name = etTaskName.text.toString()
            val startTime = etStartTime.text.toString()
            val endTime = etEndTime.text.toString()
            val category = spCategory.selectedItem.toString()
            val details = etDetails.text.toString()
            val selectedColorId = rgColors.checkedRadioButtonId

            // Validate input
            if (name.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank() &&
                selectedColorId != -1 && category != "Choose Category") {

                // Convert radio button ID to color
                val selectedColor = when (selectedColorId) {
                    R.id.color_satu -> "#FF24FC"
                    R.id.color_dua -> "#00394D"
                    R.id.color_tiga -> "#004F9F"
                    R.id.color_empat -> "#6FAE34"
                    R.id.color_lima -> "#56E0A0"
                    R.id.color_enam -> "#FF7070"
                    R.id.color_tujuh -> "#F9A825"
                    R.id.color_delapan -> "#6E72FA"
                    else -> "#FFFFFF"
                }

                val task = Task(
                    id = 0,
                    name = name,
                    startTime = startTime,
                    endTime = endTime,
                    category = category,
                    details = details,
                    color = selectedColor
                )

                // Insert the task into the database
                val result = dbHelper.insertTask(task)
                if (result != -1L) {
                    Toast.makeText(requireContext(), "Task created successfully!", Toast.LENGTH_SHORT).show()

                    // Reset fields after successful task creation
                    etTaskName.text.clear()
                    etStartTime.text.clear()
                    etEndTime.text.clear()
                    etDetails.text.clear()
                    spCategory.setSelection(0) // Reset spinner to the first item
                    rgColors.clearCheck() // Clear the selected radio button
                } else {
                    Toast.makeText(requireContext(), "Failed to create task.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields and select a valid category.", Toast.LENGTH_SHORT).show()
            }
        }


        return rootView
    }

    // Show time picker dialog
    private fun showTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, selectedHour, selectedMinute ->
            editText.setText(String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute))
        }, hour, minute, true).show()
    }
}
