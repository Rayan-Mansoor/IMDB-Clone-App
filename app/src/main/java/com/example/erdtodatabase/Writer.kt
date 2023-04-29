package com.example.erdtodatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Writer(
    @PrimaryKey val Wname : String
)
