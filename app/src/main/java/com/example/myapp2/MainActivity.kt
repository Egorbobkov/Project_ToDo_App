package com.example.myapp2

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.view.View
import android.widget.TextView


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()
    private lateinit var editTaskLauncher: ActivityResultLauncher<Intent>
    private lateinit var taskContainer: View
    private lateinit var textViewCompletedCount: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTaskLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val updatedTask = result.data?.getSerializableExtra("UPDATED_TASK") as? Task
                    updatedTask?.let { task ->
                        val position = taskList.indexOfFirst { it.id == task.id }
                        if (position != -1) {
                            taskList[position] = task
                            taskAdapter.notifyItemChanged(position)
                        } else {
                            taskList.add(task)
                            taskAdapter.notifyItemInserted(taskList.size - 1)
                        }
                        updateTaskContainerVisibility()
                    }
                } else if (result.resultCode == Activity.RESULT_FIRST_USER) {
                    val deletedTaskId = result.data?.getIntExtra("DELETED_TASK_ID", -1)
                    deletedTaskId?.let { id ->
                        val position = taskList.indexOfFirst { it.id == id }
                        if (position != -1) {
                            taskList.removeAt(position)
                            taskAdapter.notifyItemRemoved(position)
                            updateTaskContainerVisibility()
                        }
                    }
                }
            }

        taskContainer = findViewById(R.id.taskContainer)
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)
        textViewCompletedCount = findViewById(R.id.textViewCompleted)
        recyclerViewTasks.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(
            taskList,
            onCheckedChange = { position, isChecked ->
                taskList[position].isCompleted = isChecked
                taskAdapter.notifyItemChanged(position)
                updateCompletedTaskCount()
            },
            onInfoClick = { task ->
                val intent = Intent(this, EditTaskActivity::class.java).apply {
                    putExtra("TASK_NAME", task.name)
                    putExtra("TASK_PRIORITY", task.priority)
                    putExtra("TASK_DEADLINE", task.deadline)
                    putExtra("TASK_ID", task.id)
                }
                editTaskLauncher.launch(intent)
            },
            onTaskDelete = { position ->
                taskList.removeAt(position)
                taskAdapter.notifyItemRemoved(position)
                updateTaskContainerVisibility()
                updateCompletedTaskCount()
            },
            onTaskCompleted = { position ->
                val task = taskList[position]
                task.isCompleted = true
                taskAdapter.notifyItemChanged(position)
                updateCompletedTaskCount()
            }
        )
        recyclerViewTasks.adapter = taskAdapter

        taskAdapter.attachToRecyclerView(recyclerViewTasks)

        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            val intent = Intent(this, EditTaskActivity::class.java).apply {
                putExtra("TASK_NAME", "")
                putExtra("TASK_PRIORITY", 0)
                putExtra("TASK_DEADLINE", "")
            }
            editTaskLauncher.launch(intent)
        }

        updateTaskContainerVisibility()
        updateCompletedTaskCount()
    }

    private fun updateCompletedTaskCount() {
        val completedCount = taskList.count { it.isCompleted }
        textViewCompletedCount.text = "Выполнено - $completedCount"
    }

    private fun updateTaskContainerVisibility() {
        taskContainer.visibility = if (taskList.isEmpty()) View.GONE else View.VISIBLE
    }
}

