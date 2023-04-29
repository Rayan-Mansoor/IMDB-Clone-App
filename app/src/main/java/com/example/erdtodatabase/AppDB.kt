package com.example.erdtodatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Movie::class, Genre::class, MovieGenre::class, User::class, MovieUser::class, Director::class, MovieDirector::class, Writer::class, MovieWriter::class], version = 1)
abstract class AppDB : RoomDatabase() {
    abstract fun AppDAO() : AppDAO
}