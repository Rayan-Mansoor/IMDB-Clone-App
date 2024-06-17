package com.example.erdtodatabase

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(foreignKeys = [ForeignKey(entity = Movie::class, parentColumns = ["title"], childColumns = ["FMID"]),
    ForeignKey(entity = Genre::class, parentColumns = ["Gname"], childColumns = ["FGID"])],
    primaryKeys = ["FMID","FGID"])
data class MovieGenre(
    val FMID: String,
    val FGID: String
)
