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
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.core.content.ContextCompat
import android.widget.Switch



class EditTaskActivity : AppCompatActivity() {

    private lateinit var editTextTaskName: EditText
    private lateinit var spinnerPriority: Spinner
    private lateinit var buttonSetDeadline: Button
    private lateinit var textViewDeadline: TextView
    private lateinit var buttonSave: Button
    private lateinit var buttonCancel: ImageButton
    private lateinit var buttonDelete: Button
    private lateinit var switchDeadline: Switch



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
        buttonDelete = findViewById(R.id.buttonDelete)
        switchDeadline = findViewById(R.id.switchDeadline)


        updateDeleteButtonState()

        buttonDelete.setOnClickListener {
            if (!editTextTaskName.text.isNullOrEmpty()) {
                val resultIntent = Intent().apply {
                    putExtra("DELETED_TASK_ID", task?.id)
                }
                setResult(RESULT_FIRST_USER, resultIntent)
                finish()
            }
        }

        editTextTaskName.addTextChangedListener {
            updateDeleteButtonState()
        }



        val taskName = intent.getStringExtra("TASK_NAME") ?: ""
        val taskDeadline = intent.getStringExtra("TASK_DEADLINE") ?: ""
        val taskPriority = intent.getIntExtra("TASK_PRIORITY", 0)
        val taskId = intent.getIntExtra("TASK_ID", 0)

        editTextTaskName.setText(taskName)
        textViewDeadline.text = if (taskDeadline.isNotEmpty()) taskDeadline else "Нет срока"
        deadline = taskDeadline

        val priorityOptions = resources.getStringArray(R.array.priority_options)
        val spinnerAdapter = PriorityAdapter(
            this,
            R.layout.spinner_item,
            priorityOptions
        )
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerPriority.adapter = spinnerAdapter
        spinnerPriority.setSelection(taskPriority)

        if (taskId == 0) {
            task = Task(id = generateTaskId(), name = "", priority = 0, deadline = "")
        } else {
            task = Task(id = taskId, name = taskName, priority = taskPriority, deadline = taskDeadline)
        }

        switchDeadline.isChecked = deadline.isNotEmpty()

        switchDeadline.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                showDatePicker()
            } else {
                deadline = ""
                textViewDeadline.text = "Нет срока"
            }
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

    private fun updateDeleteButtonState() {
        val context = this
        val grayColor = ContextCompat.getColor(context, R.color.delete_gray)
        val redColor = ContextCompat.getColor(context, R.color.delete_red)

        if (editTextTaskName.text.isNullOrEmpty()) {
            buttonDelete.isEnabled = false
            buttonDelete.setTextColor(grayColor)

            buttonDelete.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, R.drawable.ic_delete),
                null,
                null,
                null
            )
        } else {
            buttonDelete.isEnabled = true
            buttonDelete.setTextColor(redColor)

            buttonDelete.setCompoundDrawablesWithIntrinsicBounds(
                ContextCompat.getDrawable(context, R.drawable.ic_delete_red),
                null,
                null,
                null
            )
        }
    }


    private fun generateTaskId(): Int {
        return System.currentTimeMillis().toInt()
    }

    private fun saveTask() {
        val newTaskName = editTextTaskName.text.toString()
        val newPriority = spinnerPriority.selectedItemPosition
        val newDeadline = deadline

        if (newTaskName.isNotEmpty()) {
            task?.apply {
                name = newTaskName
                priority = newPriority
                deadline = newDeadline
            }

            val resultIntent = Intent().apply {
                putExtra("UPDATED_TASK", task)
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

                switchDeadline.isChecked = true
            }, year, month, day
        )
        datePickerDialog.show()
    }

}