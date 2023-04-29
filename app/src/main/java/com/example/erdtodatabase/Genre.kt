package com.example.erdtodatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Genre(
    @PrimaryKey val Gname : String
)
