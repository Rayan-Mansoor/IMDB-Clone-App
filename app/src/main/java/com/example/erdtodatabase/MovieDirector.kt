package com.example.erdtodatabase

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(foreignKeys = [ForeignKey(entity = Movie::class, parentColumns = ["title"], childColumns = ["FMID"]),
    ForeignKey(entity = Director::class, parentColumns = ["Dname"], childColumns = ["FDID"])],
    primaryKeys = ["FMID","FDID"])
data class MovieDirector(
    val FMID: String,
    val FDID: String
)
