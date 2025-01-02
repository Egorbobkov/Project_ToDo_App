package com.example.myapp2

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.ImageButton


class EditTaskActivity : AppCompatActivity() {

    private lateinit var editTextTaskName: EditText
    private lateinit var spinnerPriority: Spinner
    private lateinit var buttonSetDeadline: Button
    private lateinit var textViewDeadline: TextView
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: ImageButton

    private var task: Task? = null
    private var deadline: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)

        editTextTaskName = findViewById(R.id.editTextTaskName)
        spinnerPriority = findViewById(R.id.spinnerPriority)
        buttonSetDeadline = findViewById(R.id.buttonSetDeadline)
        textViewDeadline = findViewById(R.id.textViewDeadline)
        buttonSave = findViewById(R.id.buttonSave)
        buttonCancel = findViewById(R.id.buttonCancel)

        val taskName = intent.getStringExtra("TASK_NAME")
        val taskPriority = intent.getIntExtra("TASK_PRIORITY", 0)
        val taskDeadline = intent.getStringExtra("TASK_DEADLINE")

        editTextTaskName.setText(taskName)

        val priorityOptions = resources.getStringArray(R.array.priority_options)
        val spinnerAdapter = ArrayAdapter(
            this,
            R.layout.spinner_item,
            priorityOptions
        )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerPriority.adapter = spinnerAdapter

        if (taskDeadline != null) {
            textViewDeadline.text = taskDeadline
            deadline = taskDeadline
        }

        buttonSetDeadline.setOnClickListener {
            showDatePicker()
        }

        buttonSave.setOnClickListener {
            saveTask()
        }

        buttonCancel.setOnClickListener {
            cancelTask()
        }
    }

    private fun saveTask() {
        val newTaskName = editTextTaskName.text.toString()
        val newPriority = spinnerPriority.selectedItemPosition
        val newDeadline = deadline

        if (newTaskName.isNotEmpty()) {
            val updatedTask = Task(
                id = task?.id ?: 0,
                name = newTaskName,
                priority = newPriority,
                deadline = newDeadline
            )

            val resultIntent = Intent().apply {
                putExtra("UPDATED_TASK", updatedTask)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            editTextTaskName.error = "Название задачи не может быть пустым"
        }
    }

    private fun cancelTask() {
        setResult(RESULT_CANCELED)
        finish()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, { _, selectedYear, selectedMonth, selectedDay ->
                deadline = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                textViewDeadline.text = deadline
            }, year, month, day
        )
        datePickerDialog.show()
    }
}