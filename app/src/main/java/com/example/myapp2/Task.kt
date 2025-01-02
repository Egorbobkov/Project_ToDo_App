package com.example.myapp2

import java.io.Serializable

data class Task(
    val id: Int = 0,
    var name: String,
    var priority: Int,
    var deadline: String,
    var isCompleted: Boolean = false
) : Serializable
