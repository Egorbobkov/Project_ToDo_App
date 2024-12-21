package com.example.myapp2

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewTasks: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>() // Список задач

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Настройка Toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Мои дела"

        // Инициализация RecyclerView
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks)
        recyclerViewTasks.layoutManager = LinearLayoutManager(this) // Устанавливаем LayoutManager
        taskAdapter = TaskAdapter(taskList, onCheckedChange = { position, isChecked ->
            taskList[position].isCompleted = isChecked
            taskAdapter.notifyItemChanged(position)
        }) // Создаем адаптер для RecyclerView
        recyclerViewTasks.adapter = taskAdapter

        // FloatingActionButton для добавления задачи
        val fabAddTask = findViewById<FloatingActionButton>(R.id.fabAddTask)
        fabAddTask.setOnClickListener {
            showAddTaskDialog()
        }
    }

    private fun showAddTaskDialog() {
        val editText = EditText(this) // Поле ввода для новой задачи
        editText.hint = "Введите задачу"

        AlertDialog.Builder(this)
            .setTitle("Новая задача")
            .setView(editText)
            .setPositiveButton("Добавить") { _, _ ->
                val taskName = editText.text.toString()
                if (taskName.isNotEmpty()) {
                    val newTask = Task(name = taskName) // Создаём объект Task
                    taskList.add(newTask)              // Добавляем объект Task в список

                    // Уведомляем адаптер об изменениях
                    taskAdapter.notifyItemInserted(taskList.size - 1)

                    // Проверка: если задача не добавлена, вывести сообщение
                    if (taskList.size == 0) {
                        Toast.makeText(this, "Задача не добавлена!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Задача не может быть пустой", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null) // Кнопка "Отмена"
            .create()
            .show()
    }
}
