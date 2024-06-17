package com.example.erdtodatabase

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Movie(
    @PrimaryKey val title: String,
    val year: Int,
    val description: String,
    val rank: Int?,
    val image: String?
) : Serializable
