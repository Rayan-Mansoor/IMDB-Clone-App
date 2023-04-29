package com.example.erdtodatabase

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(foreignKeys = [ForeignKey(entity = Movie::class, parentColumns = ["title"], childColumns = ["FMID"]),
    ForeignKey(entity = User::class, parentColumns = ["UID"], childColumns = ["FUID"])],
    primaryKeys = ["FMID","FUID"])
data class MovieUser(
    val FMID: String,
    val FUID: String,
    var comment: String?,
    var selfRating : Double?,
    var likes : Boolean?
)
