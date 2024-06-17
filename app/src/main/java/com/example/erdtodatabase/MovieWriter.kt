package com.example.erdtodatabase

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(foreignKeys = [ForeignKey(entity = Movie::class, parentColumns = ["title"], childColumns = ["FMID"]),
    ForeignKey(entity = Writer::class, parentColumns = ["Wname"], childColumns = ["FWID"])],
    primaryKeys = ["FMID","FWID"])
data class MovieWriter(
    val FMID: String,
    val FWID: String
)
