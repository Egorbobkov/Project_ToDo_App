package com.example.myapp2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.ImageButton


class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onCheckedChange: (Int, Boolean) -> Unit,
    private val onInfoClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxTask)
        private val textView: TextView = itemView.findViewById(R.id.textViewTask)
        private val infoButton: ImageButton = itemView.findViewById(R.id.buttonInfo)

        fun bind(task: Task, onCheckedChange: (Int, Boolean) -> Unit, onInfoClick: (Task) -> Unit) {
            textView.text = task.name

            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = task.isCompleted

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onCheckedChange(bindingAdapterPosition, isChecked)
                }
            }

            infoButton.setOnClickListener {
                if (bindingAdapterPosition != RecyclerView.NO_POSITION) {
                    onInfoClick(task)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], onCheckedChange, onInfoClick)
    }

    override fun getItemCount(): Int = tasks.size
}
