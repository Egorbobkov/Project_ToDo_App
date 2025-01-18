package com.example.myapp2

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat
import android.view.animation.AnimationUtils
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.TextView
import android.widget.ImageButton
import android.view.View
import android.view.ViewGroup


class TaskAdapter(
    private val tasks: MutableList<Task>,
    private val onCheckedChange: (Int, Boolean) -> Unit,
    private val onInfoClick: (Task) -> Unit,
    private val onTaskDelete: (Int) -> Unit,
    private val onTaskCompleted: (Int) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBoxTask)
        val textView: TextView = itemView.findViewById(R.id.textViewTask)
        val infoButton: ImageButton = itemView.findViewById(R.id.buttonInfo)

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

        fun animateTask() {
            val scaleUp = AnimationUtils.loadAnimation(itemView.context, R.anim.scale_up)
            itemView.startAnimation(scaleUp)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task, onCheckedChange, onInfoClick)

        holder.textView.setTextColor(Color.BLACK)

        if (task.isCompleted) {
            holder.animateTask()
        }
    }

    override fun getItemCount(): Int = tasks.size

    val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.bindingAdapterPosition
            val itemView = viewHolder.itemView
            val swipeThreshold = itemView.width * 0.3f

            if (Math.abs(viewHolder.itemView.translationX) >= swipeThreshold) {
                if (direction == ItemTouchHelper.RIGHT) {
                    onTaskCompleted(position)
                } else if (direction == ItemTouchHelper.LEFT) {
                    onTaskDelete(position)
                }
            } else {
                notifyItemChanged(position)
            }
        }


        override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val background = ColorDrawable()
            val icon: Drawable?

            if (dX > 0) {
                background.color = Color.parseColor("#34C759")
                icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_check)
            } else if (dX < 0) {
                background.color = Color.parseColor("#FF3B30")
                icon = ContextCompat.getDrawable(itemView.context, R.drawable.ic_delete)
            } else {
                background.setBounds(0, 0, 0, 0)
                icon = null
            }

            background.setBounds(
                if (dX > 0) itemView.left else itemView.right + dX.toInt(),
                itemView.top,
                if (dX > 0) itemView.left + dX.toInt() else itemView.right,
                itemView.bottom
            )

            background.draw(c)

            icon?.let {
                val iconMargin = (itemView.height - it.intrinsicHeight) / 2
                val iconTop = itemView.top + iconMargin
                val iconLeft =
                    if (dX > 0) itemView.left + iconMargin else itemView.right - iconMargin - it.intrinsicWidth
                val iconRight = iconLeft + it.intrinsicWidth
                val iconBottom = iconTop + it.intrinsicHeight

                it.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                it.draw(c)
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}


