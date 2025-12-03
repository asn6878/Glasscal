package com.example.glasscal.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.glasscal.R
import com.example.glasscal.data.local.entity.Task
import com.example.glasscal.databinding.ItemTaskListBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView Adapter for Task List
 * 할일 목록을 위한 어댑터
 */
class TaskListAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskLongClick: (Task) -> Unit
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding, onTaskClick, onTaskLongClick)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /**
     * ViewHolder for Task Item
     */
    class TaskViewHolder(
        private val binding: ItemTaskListBinding,
        private val onTaskClick: (Task) -> Unit,
        private val onTaskLongClick: (Task) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val timeFormat = SimpleDateFormat("a h:mm", Locale.getDefault())

        fun bind(task: Task) {
            binding.apply {
                // 제목
                tvTaskTitle.text = task.title

                // 내용
                if (task.content.isNotEmpty()) {
                    tvTaskContent.visibility = View.VISIBLE
                    tvTaskContent.text = task.content
                } else {
                    tvTaskContent.visibility = View.GONE
                }

                // 생성 시간
                tvTaskTime.text = timeFormat.format(Date(task.createdAt))

                // 이미지
                if (task.imageUri != null && task.imageUri.isNotEmpty()) {
                    try {
                        cvTaskImage.visibility = View.VISIBLE
                        ivTaskImage.load(Uri.parse(task.imageUri)) {
                            crossfade(true)
                            placeholder(R.drawable.bg_glass_card)
                            error(R.drawable.bg_glass_card)
                            allowHardware(false)
                            listener(
                                onError = { _, _ ->
                                    cvTaskImage.visibility = View.GONE
                                }
                            )
                        }
                    } catch (e: Exception) {
                        cvTaskImage.visibility = View.GONE
                    }
                } else {
                    cvTaskImage.visibility = View.GONE
                }

                // 클릭 이벤트
                taskCard.setOnClickListener {
                    onTaskClick(task)
                }

                // 롱 클릭 이벤트
                taskCard.setOnLongClickListener {
                    onTaskLongClick(task)
                    true
                }

                // 옵션 버튼 클릭
                btnOptions.setOnClickListener {
                    onTaskClick(task)
                }
            }
        }
    }

    /**
     * DiffUtil Callback for efficient list updates
     */
    private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
