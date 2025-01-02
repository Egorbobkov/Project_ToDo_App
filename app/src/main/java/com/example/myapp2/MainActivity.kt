package com.example.myapp2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.widget.Spinner
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private lateinit var editTaskLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTaskLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val updatedTask = result.data?.getSerializableExtra("UPDATED_TASK") as? Task
                updatedTask?.let {
                    val position = taskList.indexOfFirst { it.id == updatedTask.id }
                    if (position != -1) {
                        taskList[position] = updatedTask
                        taskAdapter.notifyItemChanged(position)
                    }
                }
            }
        }


        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Мои дела"

        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)
        taskAdapter = TaskAdapter(
            taskList,
            onCheckedChange = { position, isChecked ->
                taskList[position].isCompleted = isChecked
                taskAdapter.notifyItemChanged(position)
            },
            onInfoClick = { task ->
                val intent = Intent(this, EditTaskActivity::class.java).apply {
                    putExtra("TASK_NAME", task.name)
                    putExtra("TASK_PRIORITY", task.priority)
                    putExtra("TASK_DEADLINE", task.deadline)
                    putExtra("TASK_ID", task.id)
                }
                editTaskLauncher.launch(intent)
            }
        )
        recyclerViewTasks.adapter = taskAdapter

        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_task, null)

        val editTextTaskName = dialogView.findViewById<EditText>(R.id.editTextTaskName)
        val spinnerPriority = dialogView.findViewById<Spinner>(R.id.spinnerPriority)

        ArrayAdapter.createFromResource(
            this,
            R.array.priority_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPriority.adapter = adapter
        }

        AlertDialog.Builder(this)
            .setTitle("Новая задача")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val taskName = editTextTaskName.text.toString()
                val priority = spinnerPriority.selectedItemPosition

                if (taskName.isNotEmpty()) {
                    val newTask = Task(
                        name = taskName,
                        priority = priority,
                        deadline = ""
                    )
                    taskList.add(newTask)
                    taskAdapter.notifyItemInserted(taskList.size - 1)
                } else {
                    Toast.makeText(this, "Название задачи не может быть пустым", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()
            .show()
    }
}
