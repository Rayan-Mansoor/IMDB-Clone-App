package com.example.erdtodatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val UID: String,
    val username: String,
    val password: String,

)
